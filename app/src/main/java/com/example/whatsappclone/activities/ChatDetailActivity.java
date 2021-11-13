package com.example.whatsappclone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.SpecificChatAdapter;
import com.example.whatsappclone.databinding.ActivityChatDetailBinding;
import com.example.whatsappclone.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


        // Activity for chatting
public class ChatDetailActivity extends AppCompatActivity {
    private ActivityChatDetailBinding binding;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        //    dialog.setMessage("Sending Image");
        //    dialog.setCancelable(false);

        //getting Values from chatAdapter
        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String receiverUserName = getIntent().getStringExtra("userName");
        String receiverProfilePic = getIntent().getStringExtra("profilePic");
        String receiverToken = getIntent().getStringExtra("token");
        String receiverAbout = getIntent().getStringExtra("about" );

        binding.receiverUserName.setText(receiverUserName);
        Picasso.get().load(receiverProfilePic).placeholder(R.drawable.ic_user_logo).into(binding.displayProfileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Arraylist to store messages and adapter to show it

        final ArrayList<Message> messageArrayList = new ArrayList<>();
        final SpecificChatAdapter specificChatAdapter = new SpecificChatAdapter(messageArrayList, this);
        binding.RVChatDetails.setAdapter(specificChatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.RVChatDetails.setLayoutManager(manager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        //getting messages from database of clicked user

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message messageModel = snapshot1.getValue(Message.class);
                    messageArrayList.add(messageModel);
                }
                specificChatAdapter.notifyItemInserted(messageArrayList.size()-1);
                // specificChatAdapter.notifyDataSetChanged();
                manager.setStackFromEnd(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // sending a message by clicking send arrow btn

        binding.sendArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.editText.getText().toString();
                final Message currentMessage = new Message(senderId, message);
                currentMessage.setTimeStamp(new Date().getTime());
                binding.editText.setText("");

                database.getReference().child("chats").child(senderRoom).push()
                        .setValue(currentMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).push()
                                .setValue(currentMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                specificChatAdapter.notifyItemInserted(messageArrayList.size());

                                // specificChatAdapter.notifyDataSetChanged();
                                manager.setStackFromEnd(true);
                            }
                        });
                    }
                });
            }
        });

        //  launcher launches to select image when user wants to send a image

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    // dialog.show();

                    Uri imgUri = result;
                    Date date = new Date();
                    StorageReference reference = storage.getReference().child("chatImages").child(senderRoom).child(String.valueOf(date.getTime()));
                    Toast.makeText(ChatDetailActivity.this, "uploading Image", Toast.LENGTH_SHORT).show();

                    reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                //   dialog.dismiss();

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        String message = "photo";
                                        final Message currentMessage = new Message(senderId, message);
                                        currentMessage.setTimeStamp(new Date().getTime());
                                        currentMessage.setImageUrl(filePath);
                                        binding.editText.setText("");

                                        database.getReference().child("chats").child(senderRoom).push()
                                                .setValue(currentMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats").child(receiverRoom).push()
                                                        .setValue(currentMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });

            // launches the above launcher to select and send image
        binding.imageSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });


        // when clicked username of reciever we can view that profile
        binding.receiverUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this , ExpandedProfilePic.class);
                intent.putExtra("imgUri",receiverProfilePic);
                intent.putExtra("userName",receiverUserName);
                intent.putExtra("about", receiverAbout);

                startActivity(intent);
            }
        });
    }

}
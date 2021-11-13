package com.example.whatsappclone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivitySettingsBinding;
import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        // gets current loged in users data
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.ic_user_logo).into(binding.profileImage);
                binding.about.setText(user.getaBout());
                binding.userName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // launcher launches when user wants to select or change profilePic from file manager

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            Uri imgUri = result;
                            binding.profileImage.setImageURI(imgUri);

                            final StorageReference reference = storage.getReference().child("Profile Pictures")
                                    .child(auth.getUid());

                            reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(auth.getUid()).child("profilePic").setValue(uri.toString());
                                            Toast.makeText(SettingsActivity.this, "Profile Picture Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

        //launches the above launcher to change profile pic
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });


        // btn to save the updated user info to database
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String about = binding.about.getText().toString();
                String name = binding.userName.getText().toString();

                HashMap<String ,Object> obj = new HashMap<>();
                obj.put("username",name);
                obj.put("about",about);

                database.getReference().child("Users").child(auth.getUid()).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this).create();
                        dialog.setTitle("Updated");
                        dialog.setMessage("Profile Updated Successfully");
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                });

            }
        });
    }
}
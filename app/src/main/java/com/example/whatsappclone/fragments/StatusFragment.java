package com.example.whatsappclone.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsappclone.adapters.StatusAdapter;
import com.example.whatsappclone.databinding.StatusFragmentBinding;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.models.UserStatus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
        // fragment to display statuses of users
public class StatusFragment extends Fragment {
    StatusFragmentBinding binding;


    ArrayList<UserStatus> userStatusArrayList;
    StatusAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    User currentUser;
    String currentUserName;
    private ProgressDialog dialog;

    public StatusFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StatusFragmentBinding.inflate(inflater, container, false);

        userStatusArrayList = new ArrayList<>();
        adapter = new StatusAdapter(userStatusArrayList, getContext());
        binding.statusFragRV.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.statusFragRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();


        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Uploading Status");
        dialog.setMessage("We are uploading your status...");


        // getting data of current user/sender
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                currentUserName = currentUser.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // launcher is lauched when image is selected to add into our status (user who is using app)
        // or adding my status
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            dialog.show();
                            Uri imgUri = result;
                            Date date = new Date();

                            final StorageReference reference = storage.getReference().child("status").child(auth.getUid()).child(String.valueOf(date.getTime()));
                            reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            UserStatus userStatus = new UserStatus();
                                            userStatus.setName(currentUser.getUsername());
                                            userStatus.setProfileImage(currentUser.getProfilePic());
                                            userStatus.setLastUpdated(date.getTime());

                                            HashMap<String, Object> obj = new HashMap<>();
                                            obj.put("name", userStatus.getName());
                                            obj.put("profileImage", userStatus.getProfileImage());
                                            obj.put("lastUpdated", userStatus.getLastUpdated());

                                            String imgUri = uri.toString();
                                            Status status = new Status(imgUri, userStatus.getLastUpdated());

                                            // adding status node and current user's user id , also adding extra data such as last status time , name , etc
                                            database.getReference().child("status").child(auth.getUid()).updateChildren(obj);

                                            // making another child in the above node to add all statuses of users
                                            database.getReference().child("status").child(auth.getUid()).child("statuses").push().setValue(status);

                                            Toast.makeText(getContext(), "Status Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

    // function to check and delete 24 hr old story
        database.getReference().child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot statusSnapShot : snapshot.getChildren()) {

                        Long userStatusLastUpdated = statusSnapShot.child("lastUpdated").getValue(Long.class);

                        //   long daysOld = new Date().getTime() - userStatusLastUpdated;

                        // remove whole node if last updated is 24 hr old
                        if (userStatusLastUpdated + 86400000 <= new Date().getTime()) {
                            statusSnapShot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                   // Toast.makeText(getContext(), "Data removed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // if whole node is not removed , check individual statuses of specific user and delete 24 hr old

                            for (DataSnapshot statusesSnapshot : statusSnapShot.child("statuses").getChildren()) {
                                Status sampleStatus = statusesSnapshot.getValue(Status.class);

                                long sampleStatusTimeStamp = sampleStatus.getTimeStamp();
                                if (sampleStatusTimeStamp + 86400000 <= new Date().getTime()) {
                                    statusesSnapshot.getRef().removeValue();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // below function is to add the statuses of users into arraylist to show in this fragment
        database.getReference().child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStatusArrayList.clear();
                    int position =1;

                    for (DataSnapshot statusSnapShot : snapshot.getChildren()) {
                        UserStatus userStatus = new UserStatus();

                        // if our own status is shown , give it name as "My Status"
                        if (statusSnapShot.child("name").getValue(String.class).equals(currentUserName)) {
                            userStatus.setName("My Status");
                        } else {
                            //if it is others status give their name
                            userStatus.setName(statusSnapShot.child("name").getValue(String.class));
                        }
                        userStatus.setProfileImage(statusSnapShot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(statusSnapShot.child("lastUpdated").getValue(Long.class));

                        //the above code extracts user data from status node of database
                        //which contains childrens as lastUpdated , userProfilePid , and arraylist of statuses that user added to his status

                        ArrayList<Status> myStatuses = new ArrayList<>();
                        // now extracting that arraylist of statuses

                        for (DataSnapshot statusesSnapshot : statusSnapShot.child("statuses").getChildren()) {
                            Status sampleStatus = statusesSnapshot.getValue(Status.class);
                            myStatuses.add(sampleStatus);
                        }
                        // adding that statuses arraylist to current userStatus
                        userStatus.setStatuses(myStatuses);

                        // adding that current  userStatus to final Arraylist
                        userStatusArrayList.add(userStatus);
                        adapter.notifyItemChanged(position);
                        position++;
                    }
                    //adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // launches the above defined launcher/intent to select image from gallery
        binding.addStatus.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });
        return binding.getRoot();
    }
}

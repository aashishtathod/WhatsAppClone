package com.example.whatsappclone.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.databinding.ActivityEmailVerifyBinding;
import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EmailVerifyActivity extends AppCompatActivity {
    ActivityEmailVerifyBinding binding;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        getCurrentEmail();

        // send verification link to user email
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        AlertDialog dialog = new AlertDialog.Builder(EmailVerifyActivity.this).create();
                        dialog.setTitle("Verification Email Sent");
                        dialog.setMessage("Verification Email has been sent to your registered Email. Also check the Spam folder in case it has been marked as Spam");
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                });
            }
        });

        // updates the registered email of user in database
        binding.changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!binding.editText.getText().toString().trim().isEmpty()) {

                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("mail", binding.editText.getText().toString().trim());

                    database.getReference().child("Users").child(auth.getUid()).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getCurrentEmail();

                            AlertDialog dialog = new AlertDialog.Builder(EmailVerifyActivity.this).create();
                            dialog.setTitle("Email Updated");
                            dialog.setMessage("Your Email has been successfully updated.");
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.setCancelable(true);
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    // fuction to get current email registered with app
    private void getCurrentEmail() {
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.email.setText(user.getMail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return;
    }
}
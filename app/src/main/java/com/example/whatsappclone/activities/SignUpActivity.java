package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappclone.databinding.ActivitySignUpBinding;
import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("We are creating your account...");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // btn  to signup and register user to database

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!binding.edtSignUpEmail.getText().toString().trim().isEmpty()  && !binding.edtSignUpPassword.getText().toString().trim().isEmpty()) {
                    if (!binding.edtSignUpUsername.getText().toString().trim().isEmpty()) {
                        dialog.show();

                        auth.createUserWithEmailAndPassword(binding.edtSignUpEmail.getText().toString().trim(),
                                binding.edtSignUpPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();

                                if (task.isSuccessful()) {

                                    User user = new User(binding.edtSignUpUsername.getText().toString().trim(), binding.edtSignUpEmail.getText().toString().trim(), binding.edtSignUpPassword.getText().toString().trim());
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);

                                    Toast.makeText(SignUpActivity.this, "User created Successfully, Click on already have account to Sign In", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this).create();
                        dialog.setTitle("Required");
                        dialog.setMessage("Your Name can't be empty, it is required by other users to recognize you");
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                    }
                }
            }
        });


        binding.tvSignUpAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this , SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
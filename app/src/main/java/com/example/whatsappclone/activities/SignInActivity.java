package com.example.whatsappclone.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivitySignInBinding;
import com.example.whatsappclone.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        dialog = new ProgressDialog(SignInActivity.this);
        dialog.setTitle("Signing In");
        dialog.setMessage("We are signing in your account...");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // if user has signed in once and has verified email then he remains loged in until he sign outs ,no matter whether he closes app
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }


        binding.tvSignInNewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        // btn to sign in
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!binding.edtSignInEmail.getText().toString().trim().isEmpty() && !binding.edtSignInPassword.getText().toString().trim().isEmpty()){
                dialog.show();
                auth.signInWithEmailAndPassword(binding.edtSignInEmail.getText().toString().trim(),
                        binding.edtSignInPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent2 = new Intent(SignInActivity.this, EmailVerifyActivity.class);
                                startActivity(intent2);
                            }
                        } else {

                            AlertDialog dialog = new AlertDialog.Builder(SignInActivity.this).create();
                            dialog.setTitle("Failed to Sign In");
                            dialog.setMessage(task.getException().getMessage().toString());
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }
                });
            }
            }
        });

        // builder to lauch the laucher to select google account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        resultLauncher.launch(signInIntent);
    }


    // launcher to select google account on device/phone
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    AlertDialog dialog = new AlertDialog.Builder(SignInActivity.this).create();
                    dialog.setTitle("Failed to Sign In");
                    dialog.setMessage(e.getMessage().toString());
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        }
    });


    // after selecting any one google accout , register it to database

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();
                            User newUser = new User();
                            newUser.setUserId(user.getUid());
                            newUser.setUsername(user.getDisplayName());
                            newUser.setProfilePic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(newUser);

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Signed In with Google", Toast.LENGTH_SHORT).show();
                            //  updateUI(user);
                        } else {
                            //  updateUI(null);
                            AlertDialog dialog = new AlertDialog.Builder(SignInActivity.this).create();
                            dialog.setTitle("Failed to Sign In");
                            dialog.setMessage(task.getException().getMessage().toString());
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }
                });
    }
}
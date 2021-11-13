package com.example.whatsappclone.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityExpandedProfilePicBinding;
import com.squareup.picasso.Picasso;

        // activity to view any user profile
public class ExpandedProfilePic extends AppCompatActivity {
    ActivityExpandedProfilePicBinding binding;
    String imgUri , userName , about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpandedProfilePicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //gets intent from ChatDetailActivity and ChatFragment

        imgUri = getIntent().getStringExtra("imgUri");
        userName = getIntent().getStringExtra("userName");
        about = getIntent().getStringExtra("about");
        Picasso.get().load(imgUri).placeholder(R.drawable.ic_user_logo).into(binding.expandProfilePic);
        binding.userName.setText(userName);
        binding.about.setText(about);
    }
}
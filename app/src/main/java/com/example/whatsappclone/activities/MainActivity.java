package com.example.whatsappclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ViewPagerAdapter;
import com.example.whatsappclone.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
    // Activity to load 3 fragments of whatsapp
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseMessaging messaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        messaging = FirebaseMessaging.getInstance();

        binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle()));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });


        // gets token for cloud messaging and updating to database
        messaging.getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                HashMap<String , Object> obj = new HashMap<>();
                obj.put("token", s);

                database.getReference().child("Users").child(auth.getUid()).updateChildren(obj);
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.group_chat:
                Intent intent1 = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(intent1);
                break;

            case R.id.sign_out:
                auth.signOut();
                Intent intent2 = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}
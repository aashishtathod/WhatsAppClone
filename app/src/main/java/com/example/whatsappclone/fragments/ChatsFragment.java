package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsappclone.adapters.ChatFragmentAdapter;
import com.example.whatsappclone.databinding.ChatsFragmentBinding;
import com.example.whatsappclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

        // fragment to display all users loged in to app (ChatFragmentAdapter)
public class ChatsFragment extends Fragment {
    private ChatsFragmentBinding binding;

    ArrayList<User> userArrayList = new ArrayList<>();
    FirebaseDatabase database;

    public ChatsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChatsFragmentBinding.inflate(inflater, container, false);

        ChatFragmentAdapter adapter = new ChatFragmentAdapter(userArrayList, getContext());
        binding.chatsFragRV.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.chatsFragRV.setLayoutManager(manager);

        // getting all registered users from database
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int position = 1;

                    User user = dataSnapshot.getValue(User.class);
                    user.setUserId(dataSnapshot.getKey());
                    user.setaBout(dataSnapshot.child("about").getValue(String.class));
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        userArrayList.add(user);
                        adapter.notifyItemChanged(position);
                        position++;
                    }
                }
              //  adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }
}

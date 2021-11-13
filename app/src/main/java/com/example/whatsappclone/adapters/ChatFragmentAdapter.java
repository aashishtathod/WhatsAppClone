package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatDetailActivity;
import com.example.whatsappclone.activities.ExpandedProfilePic;
import com.example.whatsappclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

        // adapter to show all users loged in to the app
public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ChatFragmentViewHolder> {

    ArrayList<User> userArrayList;
    Context context;

    public ChatFragmentAdapter(ArrayList<User> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatFragmentAdapter.ChatFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user_item,parent,false);
        return new ChatFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFragmentAdapter.ChatFragmentViewHolder holder, int position) {
        User user = userArrayList.get(position);
        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.ic_user_logo).into(holder.displayImage);
        holder.displayName.setText(user.getUsername());
        holder.displayLastMsg.setText(user.getaBout());

        // gets the last msg if any to show in place of about , if there is no last msg about is shown
        FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getUid() + user.getUserId())
                        .orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){

                        if (!snapshot1.child("message").getValue(String.class).isEmpty()){
                            holder.displayLastMsg.setText(snapshot1.child("message").getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // launches activity to chat with clicked user (ChatDetailActivity)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",user.getUserId());
                intent.putExtra("profilePic",user.getProfilePic());
                intent.putExtra("userName",user.getUsername());
                intent.putExtra("token",user.getToken());
                intent.putExtra("about",user.getaBout());

                context.startActivity(intent);
            }
        });

        // launches activity to view clicked user profile
        holder.displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ExpandedProfilePic.class);
                intent.putExtra("imgUri",user.getProfilePic());
                intent.putExtra("userName",user.getUsername());
                intent.putExtra("about",user.getaBout());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ChatFragmentViewHolder extends RecyclerView.ViewHolder {


        ImageView displayImage;
        TextView displayLastMsg, displayName;

        public ChatFragmentViewHolder(@NonNull View itemView) {
            super(itemView);

            displayImage = itemView.findViewById(R.id.display_profile_image);
            displayLastMsg = itemView.findViewById(R.id.display_last_msg);
            displayName = itemView.findViewById(R.id.display_name);
        }
    }
}

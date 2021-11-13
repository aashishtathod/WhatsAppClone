package com.example.whatsappclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.MainActivity;
import com.example.whatsappclone.databinding.SampleStatusItemBinding;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.UserStatus;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

//adapter to show statuses of all users and own status (status fragment)
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    ArrayList<UserStatus> userStatusArrayList;
    Context context;


    public StatusAdapter(ArrayList<UserStatus> userStatusArrayList, Context context) {
        this.userStatusArrayList = userStatusArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_status_item, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

        // arraylist contains dataof  users who have posted status with time , name and another arraylist of statuses that user posted
        UserStatus userStatus = userStatusArrayList.get(position);
        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);

        // loading last status of a user as user profile
        Picasso.get().load(lastStatus.getImageUrl()).into(holder.binding.statusProfileImage);

        SimpleDateFormat sfd = new SimpleDateFormat(" dd-MM-yyyy   hh:mm aa");
        String msgTime = sfd.format(new Date(userStatus.getLastUpdated()));

        holder.binding.userName.setText(userStatus.getName());
        holder.binding.userName.setText(userStatus.getName());

        holder.binding.statusTime.setText(msgTime);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        // view status of clicked user
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // below is the story view library code to implement storyView
                ArrayList<MyStory> myStories = new ArrayList<>();
                // getting all statuses of a specific user and adding them to Storyview Library
                for (Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(8000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // name of user to display when his status is opened
                        .setSubtitleText(msgTime) // status added time of user to display when his status is opened
                        .setTitleLogoUrl(userStatus.getProfileImage()) // profilePic of user to display when his status is opened
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return userStatusArrayList.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        SampleStatusItemBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleStatusItemBinding.bind(itemView);
        }
    }
}

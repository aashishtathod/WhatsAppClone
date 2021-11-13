package com.example.whatsappclone.models;

import java.util.ArrayList;


            // model class to store status data and statuses
public class UserStatus {
    private String name, profileImage;
    private long lastUpdated;
    // all statuses that user added
    private ArrayList<Status> statuses;

    public UserStatus() {
    }

    public UserStatus(String name, String profileImage, long lastUpdated, ArrayList<Status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }


}

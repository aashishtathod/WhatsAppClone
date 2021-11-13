package com.example.whatsappclone.models;

            //model class for a single message
public class Message {
    String uId, message, imageUrl;
    long timeStamp;

    public Message(String uId, String message, String imageUrl, long timeStamp) {
        this.uId = uId;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Message(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public Message() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

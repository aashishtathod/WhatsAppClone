package com.example.whatsappclone.models;


            // model class for user to store and retrieve data from database
public class User extends  Object{
    private String profilePic , username , mail , password , userId , lastMsg , about , token;

    public User() {
    }

    public User(String profilePic, String username, String mail, String password, String userId, String lastMsg, String about, String token) {
        this.profilePic = profilePic;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMsg = lastMsg;
        this.about = about;
        this.token = token;
    }

    public User(String username, String mail, String password) {
        this.username = username;
        this.mail = mail;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getaBout() {
        return about;
    }

    public void setaBout(String aBout) {
        this.about = aBout;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {//String key
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}

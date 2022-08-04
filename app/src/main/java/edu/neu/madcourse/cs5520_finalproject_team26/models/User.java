package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class User {
    private String userId;
    private String emailId;
    private String username;
    private int geoCoins;
    private String profilePic;

    public User(String userId,String name, String email) {
        this.userId = userId;
        this.emailId = email;
        this.username = name;
        this.geoCoins = 0;
        this.profilePic = "https://firebasestorage.googleapis.com/v0/b/mad-finalproject-team26.appspot.com/o/defaultProfileImage.jpg?alt=media&token=0943abc0-afed-4614-8c0e-7937f2dcb6ff";
    }

    public String getUserId(){
        return userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getUsername() {
        return username;
    }

    public int getGeoCoins() {
        return geoCoins;
    }

    public String getProfilePic() {
        return profilePic;
    }

}

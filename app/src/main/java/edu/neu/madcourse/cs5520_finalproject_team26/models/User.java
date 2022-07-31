package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class User {

    public String username;
    public String userId;
    public String profilePic;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public User() {
        super();
    }


    public User(String username, String userId,String profilePic) {
        this.username = username;
        this.userId = userId;
        this.profilePic = profilePic;
    }
}

package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class User {

    public String username;
    public String userId;
    public int geoCoins;
    public String emailId;
    public String password;

    public User(String username, String userId, int geoCoins, String emailId, String password) {
        this.username = username;
        this.userId = userId;
        this.geoCoins = geoCoins;
        this.emailId = emailId;
        this.password = password;
    }
}
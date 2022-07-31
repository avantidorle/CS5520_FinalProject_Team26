package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class User {

    private String email;
    private String userId;
    private String name;
    private int coins;

    public User(String userId, String name, String email) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.coins = 0;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

}

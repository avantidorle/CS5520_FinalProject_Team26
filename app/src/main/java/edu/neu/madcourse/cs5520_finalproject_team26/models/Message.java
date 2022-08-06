package edu.neu.madcourse.cs5520_finalproject_team26.models;

import java.time.LocalDateTime;

public class Message {
    public String senderId;
    public String receiverId;
    public String address;
    public String messageText;
    public boolean seen;
    public LocalDateTime dateTime;

    public Message(String senderId, String receiverId, String address, String messageText, boolean seen, LocalDateTime dateTime) {
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.address=address;
        this.messageText=messageText;
        this.seen=seen;
        this.dateTime=dateTime;
    }

}
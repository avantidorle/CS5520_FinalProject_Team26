package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    public String sentTime;
    public String location;
    public String seen;

    public Message() {
        super();
    }

    public Message(String senderId, String receiverId, String messageText, String sentTime, String location, String seen) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.sentTime = sentTime;
        this.location = location;
        this.seen = seen;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    public boolean isSeen() {
//        return seen;
//    }
//
//    public void setSeen(boolean seen) {
//        this.seen = seen;
//    }
}

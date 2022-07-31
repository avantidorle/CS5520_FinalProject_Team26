package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class QuestionUser {
    String userId;
    String questionId;
    boolean vote;
    boolean answer;

    public QuestionUser(String userId, String questionId, boolean vote, boolean answer) {
        this.userId = userId;
        this.questionId = questionId;
        this.vote = vote;
        this.answer = answer;
    }
}

package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class QuestionUser {
    public String userId;
    public String questionId;
    public String vote;
    public boolean answer;
    public String userIdAndQuestionId;

    public QuestionUser(String userId, String questionId, String vote, boolean answer, String userIdAndQuestionId) {
        this.userId = userId;
        this.questionId = questionId;
        this.vote = vote;
        this.answer = answer;
        this.userIdAndQuestionId = userIdAndQuestionId;
    }
}
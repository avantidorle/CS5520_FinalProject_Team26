package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class QuestionUser {
    public String userId;
    public String questionId;
    public String vote;
    public boolean answer;
    public String userIdAndQuestionId;
    public String userIdAndLocationAndAnswer;
    public String location;

    public QuestionUser(String userId, String questionId, String vote, boolean answer, String userIdAndQuestionId, String userIdAndLocationAndAnswer, String location) {
        this.userId = userId;
        this.questionId = questionId;
        this.vote = vote;
        this.answer = answer;
        this.userIdAndQuestionId = userIdAndQuestionId;
        this.userIdAndLocationAndAnswer = userIdAndLocationAndAnswer;
        this.location = location;
    }
}
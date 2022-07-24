package edu.neu.madcourse.cs5520_finalproject_team26.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Question {

    private String questionId;
    private String questionText;
    private String hint;
    private String[] options;
    private String createdBy;
    private int answer;
    private int upVotes;
    private int downVotes;
    private Location location;

    public Question(String questionText, String[] options, String hint, int answer, Location location, String createdBy) {
        this.questionId = UUID.randomUUID().toString();
        this.questionText = questionText;
        this.hint = hint;
        this.answer = answer;
        this.location = location;
        this.options = options;
        this.createdBy = createdBy;
        this.upVotes = 0;
        this.downVotes = 0;
    }

    public void upvote() {
        upVotes++;
    }

    public void downvote() {
        downVotes++;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getHint() {
        return hint;
    }

    public String[] getOptions() {
        return options;
    }

    public int getAnswer() {
        return answer;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }
}

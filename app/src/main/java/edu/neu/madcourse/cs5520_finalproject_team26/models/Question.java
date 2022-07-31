package edu.neu.madcourse.cs5520_finalproject_team26.models;

import java.util.List;
import java.util.UUID;

public class Question {

    private String questionId;
    private String questionText;
    private String hint;
    private List<String> options;
    private String createdBy;
    private int answer;
    private int upVotes;
    private int downVotes;

    public Question(String questionText, List<String> options, String hint, int answer, String createdBy) {
        this.questionId = UUID.randomUUID().toString();
        this.questionText = questionText;
        this.hint = hint;
        this.answer = answer;
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

    public List<String> getOptions() {
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

    public String getQuestionId() {
        return questionId;
    }
}

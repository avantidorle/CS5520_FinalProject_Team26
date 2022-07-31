package edu.neu.madcourse.cs5520_finalproject_team26.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Location {

    private final String location;
    private final Map<String, String> questions;

    public Location(String location, String questions) {;
        this.location = location;
        this.questions = new HashMap<>();
        this.questions.put(UUID.randomUUID().toString(), questions);
    }

    public String getLocation() {
        return location;
    }

    public Map<String,String> getQuestions() {
        return questions;
    }
}
package edu.neu.madcourse.cs5520_finalproject_team26.models;

import java.util.ArrayList;

public class Location {
    public int locationId;
    public String address;
    public ArrayList<String> questionIds;


    public Location(int locationId, String address, ArrayList<String> questionIds) {
        this.locationId = locationId;
        this.address = address;
        this.questionIds = questionIds;
    }


}
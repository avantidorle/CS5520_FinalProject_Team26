package edu.neu.madcourse.cs5520_finalproject_team26.models;

public class Location {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

   public double getLatitude() {
        return latitude;
   }

   public double getLongitude() {
        return longitude;
   }
}
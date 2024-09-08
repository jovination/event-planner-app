package com.nanologic.eventify;

public class Event {
    public String eventId;
    public String eventName;
    public String location;
    public String date;
    public String startTime;
    public String endTime;
    public int numberOfSeats;
    public String imageUrl;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String eventId, String eventName, String location, String date,
                 String startTime, String endTime, int numberOfSeats, String imageUrl) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfSeats = numberOfSeats;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for each field (optional but recommended)
}

package com.nanologic.eventify;

public class Event {
    private String eventId;
    private String eventName;
    private String date;
    private String startTime;
    private String endTime;
    private String location;
    private int numberOfSeats;
    private String imageUrl;

    // Default constructor for Firestore deserialization
    public Event() {}

    // Constructor with parameters
    public Event(String eventId, String eventName,String location, String date, String startTime, String endTime,
                  int numberOfSeats, String imageUrl) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfSeats = numberOfSeats;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Method to extract the day of the month (dateId)
    public String getDateId() {
        if (date == null || date.isEmpty()) return "";
        String[] dateParts = date.split(" ");
        return (dateParts.length > 1) ? dateParts[1].replace(",", "") : "";
    }

    // Method to extract the month (monthId)
    public String getMonthId() {
        if (date == null || date.isEmpty()) return "";
        String[] dateParts = date.split(" ");
        return (dateParts.length > 0) ? dateParts[0] : "";
    }
}

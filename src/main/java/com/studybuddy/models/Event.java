package com.studybuddy.models;

import java.time.LocalDateTime;
import java.lang.*;
import java.util.List;

public class Event {

    private long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private List<User> attendees;
    private String location;
    private boolean isGoogleEvent;
    private boolean expired;

    public Event(int id, String title, LocalDateTime startTime, LocalDateTime endTime, String description, List<User> attendees, String location, Boolean isGoogleEvent) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.attendees = attendees;
        this.location = location;
        this.isGoogleEvent = isGoogleEvent;
        this.expired = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<User> attendees) {
        this.attendees = attendees;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isGoogleEvent() { return isGoogleEvent; }

    public boolean isExpired() { return expired; }
}

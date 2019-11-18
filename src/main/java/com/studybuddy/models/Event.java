package com.studybuddy.models;

import java.time.LocalDateTime;
import java.lang.*;
//import java.util.Comparator;
import java.util.List;

public class Event {

    private long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private List<String> attendees;
    private String location;

    public Event(long id, String title, LocalDateTime startTime, LocalDateTime endTime, String description, List<String> attendees, String location) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.attendees = attendees;
        this.location = location;
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

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                ", attendees=" + attendees +
                ", location='" + location + '\'' +
                '}';
    }
}

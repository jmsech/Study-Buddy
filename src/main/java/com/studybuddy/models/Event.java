package com.studybuddy.models;

import java.time.LocalDateTime;


public class Event {

    private LocalDateTime time; // TODO - Fix This
    private String description;
    private int id;

    public Event(LocalDateTime time, String description, int id) {
        this.time = time;
        this.description = description;
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

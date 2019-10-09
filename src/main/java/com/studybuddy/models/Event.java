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
    private List<User> hosts;
    private static long idCounter = 0;

    public Event(String title, LocalDateTime startTime, LocalDateTime endTime, String description, List<User> hosts) {
        this.id = this.idCounter++;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.hosts = hosts;
    }

    public void addToCalendar(String calendarID) {
        // TODO ?
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<User> getHosts() { return hosts; }

    public void setHosts(List<User> hosts) { this.hosts = hosts; }


   public int compareTo(Event e) {
        return Long.compare(this.id, e.getId());
    }

    public boolean equals(Event e) {
        return this.id == e.getId();
    }

    public void removeHost(int hostid) {
        this.hosts.remove(hostid);
    }

    public void addHost(User user) {
        this.hosts.add(user);
    }

    public boolean isHost(User user) {
        return hosts.contains(user);
    }
}

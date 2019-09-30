package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

public class Event {

    private Date date; // TODO - Fix This
    private String description;
    private int id;
    private Set<Integer> hosts;

    public Event(Date date, String description, int id, Set<Integer> hosts) {
        this.date = date;
        this.description = description;
        this.id = id;
        this.hosts = hosts;
    }

    public void addToCalendar(String calendarID) {
        // TODO ?
    }

    public int compareTo(Event e) {
        return this.date.compareTo(e.getDate());
    }

    public boolean equals(Event e) {
        return this.id == e.getId();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Set<Integer> getHosts() {
        return this.hosts;
    }

    public void removeHost(int hostid) {
        this.hosts.remove(hostid);
    }

    public void addHost(int hostid) {
        this.hosts.add(hostid);
    }

    public boolean isHost(int id) {
        return hosts.contains(id);
    }
}

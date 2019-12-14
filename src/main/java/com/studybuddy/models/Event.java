package com.studybuddy.models;

import java.time.LocalDateTime;
import java.lang.*;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

public class Event implements Comparable<Event> {

    private long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private List<User> attendees;
    private String location;
    private boolean isGoogleEvent;
    private boolean expired;
    private boolean isDeadline;
    private boolean conflict;

    public Event(int id, String title, LocalDateTime startTime, LocalDateTime endTime, String description, List<User> attendees, String location, boolean isDeadline) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.attendees = attendees;
        this.location = location;
        this.isGoogleEvent = false;
        this.expired = false;
        this.isDeadline = isDeadline;
        this.conflict = false;
    }

    @Override
    public boolean equals(Object e1) {
        if (!(e1 instanceof Event)) {
            return false;
        }
        Event e = (Event) e1;
        return this.compareTo(e) == 0;
    }

    @Override
    public int compareTo(Event e2)  {
        long start1 = this.startTime.toEpochSecond(ZoneOffset.UTC);
        long start2 = e2.startTime.toEpochSecond(ZoneOffset.UTC);
        if (start1 < start2) { return -1; }
        if (start1 > start2) {return 1; }
        else {
            String s1 = this.title;
            String s2 = e2.title;
            return s1.compareTo(s2);
        }
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

    public void setGoogleEvent(boolean googleEvent) {
        isGoogleEvent = googleEvent;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isDeadline() {
        return isDeadline;
    }

    public boolean getIsDeadline() {
        return isDeadline;
    }

    public void setDeadline(boolean deadline) {
        isDeadline = deadline;
    }

    public void setIsDeadline(boolean deadline) {
        isDeadline = deadline;
    }

    public void setConflict(boolean c) { conflict = c; }

    public boolean getConflict() { return this.conflict; }

    public static boolean overlaps(Event e1, Event e2) {
        long start1 = e1.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long start2 = e2.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long end1 = e1.getEndTime().toEpochSecond(ZoneOffset.UTC);
        long end2 = e2.getEndTime().toEpochSecond(ZoneOffset.UTC);
        if (e1.isDeadline() || e2.isDeadline()) { return false; }
        if (start1 < start2) {
            return start2 < end1;
        } else if (start2 < start1) {
            return start1 < end2;
        } else {
            return true;
        }
    }

    public static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            long start1 = e1.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long start2 = e2.getStartTime().toEpochSecond(ZoneOffset.UTC);
            if (start1 < start2) { return -1; }
            if (start1 > start2) {return 1; }
            else {
                String s1 = e1.getTitle();
                String s2 = e2.getTitle();
                return s1.compareTo(s2);
            }
        }
    }
}

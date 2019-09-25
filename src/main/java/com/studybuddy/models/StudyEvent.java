package com.studybuddy.models;

import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.util.List;

public class StudyEvent extends Event{

    private Location place; // TODO - Fix This
    private List<User> peopleAttending;
    private List<User> pendingInvitations;
    private User host;
    private double importance;

    public StudyEvent(LocalDateTime time, String description, int id, Location place,
                      List<User> peopleAttending, List<User> pendingInvitations,
                      User host, double importance) {
        super(time, description, id);
        this.place = place;
        this.peopleAttending = peopleAttending;
        this.pendingInvitations = pendingInvitations;
        this.host = host;
        this.importance = importance;
    }

    public void addUser(User user) { /*TODO*/}

//    public Location getPlace() {
//        return place;
//    }
//
//    public void setPlace(Location place) {
//        this.place = place;
//    }
//
//    public List<User> getPeopleAttending() {
//        return peopleAttending;
//    }
//
//    public void setPeopleAttending(List<User> peopleAttending) {
//        this.peopleAttending = peopleAttending;
//    }

//    public List<User> getPendingInvitations() {
//        return pendingInvitations;
//    }
//
//    public void setPendingInvitations(List<User> pendingInvitations) {
//        this.pendingInvitations = pendingInvitations;
//    }
//
//    public User getHost() {
//        return host;
//    }
//
//    public void setHost(User host) {
//        this.host = host;
//    }
//
//    public double getImportance() {
//        return importance;
//    }
//
//    public void setImportance(double importance) {
//        this.importance = importance;
//    }
}

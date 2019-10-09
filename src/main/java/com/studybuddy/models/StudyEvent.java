package com.studybuddy.models;

// import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.lang.Double.compare;

public class StudyEvent extends Event{

    private List<User> peopleAttending;
    private List<User> pendingInvitations;
    private String location;
    private double importance;

    public StudyEvent(String title, LocalDateTime startTime, LocalDateTime endTime, String location, String description,
                      List<User> hosts, List<User> pendingInvitations, double importance) {
        super(title, startTime, endTime, description, hosts);
        this.peopleAttending = null;
        this.pendingInvitations = pendingInvitations;
        this.location = location;
        this.importance = importance;
    }

    public void inviteUser(User user) {
        this.pendingInvitations.add(user);
        //TODO send invite
    }

    public void markAttending(User user) {
        this.peopleAttending.add(user);
        this.pendingInvitations.remove(user);
    }

    public void removeAttendee(User user) {
        this.peopleAttending.remove(user);
    }

    public int compareImportance(StudyEvent e) {
        return compare(this.importance, e.getImportance());
    }

    public boolean deleteStudyEvent(User user) {
        if (this.isHost(user)) {
            this.peopleAttending.clear();
            this.pendingInvitations.clear();
            // TODO notify users?
            return true;
        }
        return false;
    }

    public List<User> getPeopleAttending() {
        return peopleAttending;
    }

    public void setPeopleAttending(List<User> peopleAttending) {
        this.peopleAttending = peopleAttending;
    }

    public List<User> getPendingInvitations() {
        return pendingInvitations;
    }

    public void setPendingInvitations(List<User> pendingInvitations) {
        this.pendingInvitations = pendingInvitations;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }
}

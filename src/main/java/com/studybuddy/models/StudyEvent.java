package com.studybuddy.models;

import javax.xml.stream.Location;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.lang.Double.compare;

public class StudyEvent extends Event{

    private Location place;
    private List<User> peopleAttending;
    private List<User> pendingInvitations;
    private double importance;

    public StudyEvent(Date date, String description, int id, Location place,
                      List<User> peopleAttending, List<User> pendingInvitations,
                      Set<Integer> hosts, double importance) {
        super(date, description, id, hosts);
        this.place = place;
        this.peopleAttending = peopleAttending;
        this.pendingInvitations = pendingInvitations;
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

    public boolean deleteStudyEvent(int id) {
        if (this.isHost(id)) {
            this.peopleAttending.clear();
            this.pendingInvitations.clear();
            // TODO notify users?
            return true;
        }
        return false;
    }

    public Location getPlace() {
        return place;
    }

    public void setPlace(Location place) {
        this.place = place;
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

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }
}

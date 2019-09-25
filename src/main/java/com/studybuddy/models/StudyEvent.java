package com.studybuddy.models;

import javax.xml.stream.Location;
import java.util.List;

public class StudyEvent extends Event {

    private Location place; // TODO - Fix This
    private List<User> peopleAttending;
    private List<User> pendingInvitations;
    private User host;
    private double importance;



}

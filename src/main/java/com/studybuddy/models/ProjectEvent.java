package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectEvent extends AssessmentEvent{

    private List<User> group;
    private List<Event> meetings;

    public ProjectEvent(LocalDateTime startTime, LocalDateTime endTime, String description, List<User> hosts, double weight,
                        ParticularCourse course, List<Event> studyEvents, List<User> group, List<Event> meetings) {
        super(startTime, endTime, description, hosts, weight, course, studyEvents);
        this.group = group;
        this.meetings = meetings;
    }

    public List<User> getGroup() {
        return group;
    }

//    public void setGroup(List<User> group) {
//        this.group = group;
//    }
//
//    public List<Event> getMeetings() {
//        return meetings;
//    }
//
//    public void setMeetings(List<Event> meetings) {
//        this.meetings = meetings;
//    }
}

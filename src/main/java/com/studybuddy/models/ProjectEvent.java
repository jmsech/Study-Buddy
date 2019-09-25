package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectEvent extends AssessmentEvent{

    private List<User> group;
    private List<Event> meetings;

    public ProjectEvent(LocalDateTime time, String description, int id, double weight, ParticularCourse course,
                        List<User> group, List<Event> meetings) {
        super(time, description, id, weight, course);
        this.group = group;
        this.meetings = meetings;
    }

    public List<User> getGroup() {
        return group;
    }

    public void setGroup(List<User> group) {
        this.group = group;
    }

    public List<Event> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Event> meetings) {
        this.meetings = meetings;
    }
}

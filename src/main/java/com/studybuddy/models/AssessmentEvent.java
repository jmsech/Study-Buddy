package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.lang.Double.compare;

public class AssessmentEvent extends Event {

    private double weight;
    private ParticularCourse course;
    private List<Event> studyEvents;

    public AssessmentEvent(int id, String title, LocalDateTime startTime, LocalDateTime endTime, String description,
                           List<User> hosts, double weight, ParticularCourse course, List<Event> studyEvents) {
        super(id, title, startTime, endTime, description, hosts);
        this.weight = weight;
        this.course = course;
        this.studyEvents = studyEvents;
    }

    public void remind() { /*TODO*/ }

    public int compareWeight(AssessmentEvent e1) {
        return compare(this.weight, e1.getWeight());
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ParticularCourse getCourse() {
        return course;
    }

    public void setCourse(ParticularCourse course) {
        this.course = course;
    }

    public List<Event> getStudyEvents() {
        return studyEvents;
    }

    public void setStudyEvents(List<Event> studyEvents) {
        this.studyEvents = studyEvents;
    }

    public void deleteStudyEvents(User user, Event e1) {
        if (this.isHost(user)) {
            studyEvents.remove(e1);
        }
        return;
    }

}

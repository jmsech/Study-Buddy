package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.List;

public class AssessmentEvent extends Event{

    private double weight;
    private ParticularCourse course;
    private List<Event> studyEvents;

    public AssessmentEvent(LocalDateTime time, String description, int id, double weight, ParticularCourse course) {
        super(time, description, id);
        this.weight = weight;
        this.course = course;
    }

    public void remind() { /*TODO*/ }

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
}

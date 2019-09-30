package com.studybuddy.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.lang.Double.compare;

public class AssessmentEvent extends Event{

    private double weight;
    private ParticularCourse course;
    private List<Event> studyEvents;

    public AssessmentEvent(Date date, String description, int id, Set<Integer> hosts, double weight, ParticularCourse course) {
        super(date, description, id, hosts);
        this.weight = weight;
        this.course = course;
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

    public void deleteStudyEvents(int id, Event e1) {
        if (this.isHost(id)) {
            studyEvents.remove(e1);
        }
        return;
    }

}

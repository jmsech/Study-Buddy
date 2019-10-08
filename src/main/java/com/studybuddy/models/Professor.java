package com.studybuddy.models;

import java.util.List;

public class Professor extends User {

    private List<ParticularCourse> currentCourses;
    private List<ParticularCourse> pastCourses;

    public Professor(int id, String name, Calendar cal) {
        super(id, name);
    }

    public void scheduleOfficeHours() {
        //TODO
    }

    public void createAssessment() {
        //TODO
    }

    public void scheduleTAMeeting() {
        //TODO
    }

    public void sendAssessmentReminder() {
        //TODO
    }

//    public List<ParticularCourse> getCurrentCourses() {
//        return currentCourses;
//    }
//
//    public void setCurrentCourses(List<ParticularCourse> currentCourses) {
//        this.currentCourses = currentCourses;
//    }
//
//    public List<ParticularCourse> getPastCourses() {
//        return pastCourses;
//    }
//
//    public void setPastCourses(List<ParticularCourse> pastCourses) {
//        this.pastCourses = pastCourses;
//    }
}

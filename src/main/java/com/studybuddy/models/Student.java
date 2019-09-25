package com.studybuddy.models;

import java.util.List;

public class Student extends User {

    private String studentID;
    private University university;
    private List<ParticularCourse> currentClasses;
    private List<ParticularCourse> pastCourses;
    private List<ParticularCourse> TAing;
    private List<Course> Tutoring;

    public Student(String studentID, University university) {
        this.studentID = studentID;
        this.university = university;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public List<ParticularCourse> getCurrentClasses() {
        return currentClasses;
    }

    public void setCurrentClasses(List<ParticularCourse> currentClasses) {
        this.currentClasses = currentClasses;
    }

    public List<ParticularCourse> getPastCourses() {
        return pastCourses;
    }

    public void setPastCourses(List<ParticularCourse> pastCourses) {
        this.pastCourses = pastCourses;
    }

    public List<ParticularCourse> getTAing() {
        return TAing;
    }

    public void setTAing(List<ParticularCourse> TAing) {
        this.TAing = TAing;
    }

    public List<Course> getTutoring() {
        return Tutoring;
    }

    public void setTutoring(List<Course> tutoring) {
        Tutoring = tutoring;
    }
}

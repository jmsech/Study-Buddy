package com.studybuddy.models;

import java.util.HashMap;
import java.util.List;

public class Course {

    private String id;
    private String courseNumber;
    private List<User> alumni;
    private List<ParticularCourse> semesters;
    private List<ParticularCourse> activeClasses;
    private HashMap<User, String> grades;

    public Course(String id, String courseNumber, List<User> alumni, List<ParticularCourse> semesters, List<ParticularCourse> activeClasses, HashMap<User, String> grades) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.alumni = alumni;
        this.semesters = semesters;
        this.activeClasses = activeClasses;
        this.grades = grades;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public List<User> getAlumni() {
        return alumni;
    }

    public void setAlumni(List<User> alumni) {
        this.alumni = alumni;
    }

    public List<ParticularCourse> getSemesters() {
        return semesters;
    }

    public void setSemesters(List<ParticularCourse> semesters) {
        this.semesters = semesters;
    }

    public List<ParticularCourse> getActiveClasses() {
        return activeClasses;
    }

    public void setActiveClasses(List<ParticularCourse> activeClasses) {
        this.activeClasses = activeClasses;
    }

    public HashMap<User, String> getGrades() {
        return grades;
    }

    public void setGrades(HashMap<User, String> grades) {
        this.grades = grades;
    }
}

package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

public class Course implements Cloneable{

    private String id;
    private String courseNumber;
    private List<User> alumni;
    private List<User> potentialTutors;
    private List<ParticularCourse> semesters;
    private List<ParticularCourse> activeClasses;
    private Map<User, String> grades;
    private LocalDateTime time;

    public Course(String id, String courseNumber, List<User> alumni, List<User> potentialTutors,
                  List<ParticularCourse> semesters, List<ParticularCourse> activeClasses,
                  Map<User, String> grades, LocalDateTime time) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.alumni = alumni;
        this.potentialTutors = potentialTutors;
        this.semesters = semesters;
        this.activeClasses = activeClasses;
        this.grades = grades;
        this.time = time;
    }

    public List<User> getPotentialTutors() {
        return potentialTutors;
    }

    public void setPotentialTutors(List<User> potentialTutors) {
        this.potentialTutors = potentialTutors;
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

    public Map<User, String> getGrades() {
        return grades;
    }

    public void setGrades(Map<User, String> grades) {
        this.grades = grades;
    }

    public LocalDateTime getTime() { return time; }

    public void setTime(LocalDateTime time) { this.time = time; }

    public void generateMatches(){
        //TODO
    }
    public void makeDistribution(){
        //TODO
    }

}

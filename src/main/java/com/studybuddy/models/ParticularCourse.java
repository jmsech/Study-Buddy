package com.studybuddy.models;

import java.util.List;

public class ParticularCourse implements Cloneable{

    private Course course;
    private String semester;
    private boolean active;
    private List<User> students;
    private List<User> TAs;
    private List<User> Instructor;
    private List<Event> classEvents;

    public ParticularCourse(Course course, String semester, boolean active, List<User> students, List<User> TAs, List<User> instructor, List<Event> classEvents) {
        this.course = course;
        this.semester = semester;
        this.active = active;
        this.students = students;
        this.TAs = TAs;
        Instructor = instructor;
        this.classEvents = classEvents;
    }
    /*
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<User> getTAs() {
        return TAs;
    }

    public void setTAs(List<User> TAs) {
        this.TAs = TAs;
    }

    public List<User> getInstructor() {
        return Instructor;
    }

    public void setInstructor(List<User> instructor) {
        Instructor = instructor;
    }

    public List<Event> getClassEvents() {
        return classEvents;
    }

    public void setClassEvents(List<Event> classEvents) {
        this.classEvents = classEvents;
    }
    */
    public void initializeRoster() {
        //TODO
    }

    public boolean isActive() {
        return active;
    }

    public void createGroup() {
        //TODO
    }

    public void getDistribution() {
        //TODO
    }

    public void generateOfficeHours() {
        //TODO
    }
}

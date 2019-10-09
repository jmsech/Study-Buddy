package com.studybuddy.models;

import java.util.ArrayList;
import java.util.HashMap;
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
        this.Instructor = instructor;
        this.classEvents = classEvents;
    }

    /** Initialize the roster of students for a ParticularCourse
     *
     * @param stu - students that will be enrolled in class. Let List be null if now students in roster yet.
     */
    public void initializeRoster(List<User> stu) {
        if (stu == null) {
            this.students = new ArrayList<User>();
        } else {
            this.students = stu;
        }
    }

    /** Add students to roster
     *
     * @param stu - students to be added to roster
     */
    public void addToRoster(List<User> stu) {
        this.students.addAll(stu);
    }

    /** Add students to roster
     *
     * @param stu - students to be added to roster
     */
    public void removeFromRoster(List<User> stu) {
        this.students.removeAll(stu);
    }

    /** Create a group of students (likely for a group project)
     *
     * @param stu - students to be added to roster
     */
    public void createGroup(List<User> stu) {
        //TODO
    }

    /** Returns grade distribution of course in the form of a HashMap
     *
     * @return - HashMap maps from grades to percentage of students in each grade range.
     */
    public HashMap<String, Double> getDistribution() {
        //TODO
    }

    /** Looks through schedules of students and TA's and determines when best time for office hours are.
     *
     */
    public void generateOfficeHours() {
        //TODO
    }

    ///////////////////////////////
    // Getters and Setters Below //
    ///////////////////////////////

    public boolean isActive() {
        return active;
    }

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
}

package com.studybuddy.models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ParticularCourse implements Cloneable {

    private String courseId; // xxx.xxx(01)Fayyyy
    private String courseName;
    private String courseDescription;
    private String courseNumber;
    private String semester;
    private String section;
    private String location;
    private String credits;
    private String timeString;
    private boolean active;
    private List<User> students;
    private List<User> tas;
    private String instructor;
    private List<Event> classEvents;

    public ParticularCourse(String courseId, String courseName, String courseDescription, String courseNumber,
                            String semester, String section, String location, String credits, String timeString,
                            boolean active, List<User> students, List<User> tas, String instructor,
                            List<Event> classEvents) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseNumber = courseNumber;
        this.semester = semester;
        this.section = section;
        this.location = location;
        this.credits = credits;
        this.timeString = timeString;
        this.active = active;
        this.students = students;
        this.tas = tas;
        this.instructor = instructor;
        this.classEvents = classEvents;
    }

    public static class CourseComparator implements Comparator<ParticularCourse> {
        @Override
        public int compare(ParticularCourse c1, ParticularCourse c2) {
             return 0;
        }
    }


    /**
     * Create a group of students (likely for a group project)
     *
     * @param stu - students to be added to roster
     */
    public void createGroup(List<User> stu) {
    }

    /**
     * Returns grade distribution of course in the form of a HashMap
     *
     * @return - HashMap maps from grades to percentage of students in each grade range.
     */
    public HashMap<String, Double> getDistribution() {
        return null;
    }


    ///////////////////////////////
    // Getters and Setters Below //
    ///////////////////////////////


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

     public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
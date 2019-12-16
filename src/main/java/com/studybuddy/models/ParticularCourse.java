package com.studybuddy.models;

import java.time.ZoneOffset;
import java.util.ArrayList;
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
     * Initialize the roster of students for a ParticularCourse
     *
     * @param stu - students that will be enrolled in class. Let List be null if now students in roster yet.
     */
    public void initializeRoster(List<User> stu) {
        if (stu == null) {
            this.students = new ArrayList<>();
        } else {
            this.students = stu;
        }
    }

    /**
     * Add students to roster
     *
     * @param stu - students to be added to roster
     */
    public void addToRoster(List<User> stu) {
        this.students.addAll(stu);
    }

    public void addToRoster(User stu) {
        this.students.add(stu);
    }

    /**
     * Add students to roster
     *
     * @param stu - students to be added to roster
     */
    public void removeFromRoster(List<User> stu) {
        this.students.removeAll(stu);
    }

    public void removeFromRoster(User stu) {
        this.students.add(stu);
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

    /**
     * Looks through schedules of students and TA's and determines when best time for office hours are.
     */
    public void generateOfficeHours() {
        //TODO
    }


    ///////////////////////////////
    // Getters and Setters Below //
    ///////////////////////////////

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

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

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public boolean isActive() {
        return active;
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

    public List<User> getTas() {
        return tas;
    }

    public void setTas(List<User> tas) {
        this.tas = tas;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public List<Event> getClassEvents() {
        return classEvents;
    }

    public void setClassEvents(List<Event> classEvents) {
        this.classEvents = classEvents;
    }

}
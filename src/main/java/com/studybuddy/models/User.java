package com.studybuddy.models;

public class User {

    private int id;
    private String name;
    private Calendar cal;
    private Course[] courseList;

    public User(int id, String name, Calendar cal, Course[] courseList) {
        this.id = id;
        this.name = name;
        this.cal = cal;
        this.courseList = courseList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getCal() {
        return cal;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    public Course[] getCourseList() {
        return courseList;
    }

    public void setCourseList(Course[] courseList) {
        this.courseList = courseList;
    }
}
package com.studybuddy.models;

import java.util.List;

public class User implements Cloneable{

    private Integer id;
    private String name;
    private Calendar cal;
    private Course[] courseList;

    public User(int id, String name, Calendar cal) {
        this.id = id;
        this.name = name;
        this.cal = cal;
    }

    public void createEvent() {
        // TODO
    }

    public void attendEvent(Event e) {
        // TODO
    }

    public void declineEvent(Event e) {
        // TODO
    }

    public List<Event> getRecommendations() {
        //  TODO
        return null;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Calendar getCal() {
//        return cal;
//    }
//
//    public void setCal(Calendar cal) {
//        this.cal = cal;
//    }
//
//    public Course[] getCourseList() {
//        return courseList;
//    }
//
//    public void setCourseList(Course[] courseList) {
//        this.courseList = courseList;
//    }
}
package com.studybuddy.models;

import java.util.List;

public class Student extends User {

    private String studentID;
    private University university;
    private List<ParticularCourse> currentClasses;
    private List<ParticularCourse> pastCourses;
    private List<ParticularCourse> TAing;
    private List<Course> Tutoring;


}

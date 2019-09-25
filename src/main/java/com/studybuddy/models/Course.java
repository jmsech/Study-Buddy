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

}

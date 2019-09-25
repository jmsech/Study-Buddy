package com.studybuddy.models;

import java.util.List;

public class University {

    private String name;
    private String address;
    private List<Course> coursesOffered;
    private List<Student> students;
    private List<Professor> professors;

    public University(String name, String address, List<Course> coursesOffered, List<Student> students, List<Professor> professors) {
        this.name = name;
        this.address = address;
        this.coursesOffered = coursesOffered;
        this.students = students;
        this.professors = professors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Course> getCoursesOffered() {
        return coursesOffered;
    }

    public void setCoursesOffered(List<Course> coursesOffered) {
        this.coursesOffered = coursesOffered;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
    }
}

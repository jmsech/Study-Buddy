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

    /*public String getName() {
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
    }*/

    public void addStudent(Student student) {
        if (this.students.indexOf(student) == -1) {
            this.students.add(student);
        }
        return;
    }

    public void removeStudent(Student student) {
        students.remove(student);
        return;
    }

    public void addCourse(Course course) {
        if (this.coursesOffered.indexOf(course) == -1) {
            this.coursesOffered.add(course);
        }
        return;
    }

    public void removeCourse(Course course) {
        this.coursesOffered.remove(course);
        return;
    }

    public void addProfessor(Professor professor) {
        if (professors.indexOf(professor) == -1) {
            this.professors.add(professor);
        }
        return;
    }

    public void removeProfessor(Professor professor) {
        this.professors.remove(professor);
        return;
    }
}

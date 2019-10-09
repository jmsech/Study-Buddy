package com.studybuddy.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Student extends User {

    private String studentID;
    private University university;
    private List<ParticularCourse> currentClasses;
    private List<ParticularCourse> pastCourses;
    private List<ParticularCourse> TAing;
    private List<Course> Tutoring;

    /** Constructor for student object.
     *
     * @param id - userID which will identify the user (11xxxxxxxxxx)
     * @param name - String representation of user name
     * @param studentID - Student ID number (probably from a list given by university)
     * @param university - University which student attends (or maybe other schools in future)
     */
    public Student(int id, String name, String studentID, University university) {
        super(id, name);
        super.setCal(new Calendar(this, new ArrayList<Event>()));
        this.studentID = studentID;
        this.university = null;  // TODO (maybe) If we want to expand to other universities
    }

    /** Add an event to Student Calendar, this will be used so that group projects, homeowrks, assessments etcetera can
     * be added directly to student calendar.
     *
     * @param e - Event to add to calendar
     */
    public void addEvent2Calendar(Event e) {
        super.cal.addEvent(e);
    }

    /** Use students calendar to generate personalized study plan by finding when they have events and organize study
     *  time for assessments and complete homework.
     *
     */
    public void generateStudyPlan() {
        // TODO (maybe) come up with a schedule of free time to go to study events & study alone??
    }

    /** Get students grades in courses. This will be used to determine if they are a good match to study with someone
     * else or tutor someone else in a particular course
     *
     * @return - A HashMap which maps the courses a students has completed to their grades in that course.
     */
    public HashMap<Course, String> getGrades() {
        // go through each of your past courses' grade list and pull out the grade you got
        // TODO (maybe)
        //for use when determining who would be a good tutor
        return null;
    }

    /** Go to ParticularCourses and determine who in these courses has compatible schedules and background to find
     * similar students. This function should call getGrades() for other students?
     *
     * @return - List of students with compatible schedules/backgrounds/work loads.
     */
    public List<Student> findBuddies() {
        // we want friends in our classes!!
        // TODO **
        return null;
    }

    /** Find tutors for you're course.
     *
     * @param c - Course that you want to find tutors for.
     * @return - List of potential tutors
     */
    public List<User> findTutor(Course c) {
        //messageTutors will reach out to all of the courses' potential tutors and find you a match
        // c.messageTutors();
        return null;
    }

    ///////////////////////////////
    // Getters and Setters Below //
    ///////////////////////////////

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public List<ParticularCourse> getCurrentClasses() {
        return currentClasses;
    }

    public void setCurrentClasses(List<ParticularCourse> currentClasses) {
        this.currentClasses = currentClasses;
    }

    public List<ParticularCourse> getPastCourses() {
        return pastCourses;
    }

    public void setPastCourses(List<ParticularCourse> pastCourses) {
        this.pastCourses = pastCourses;
    }

    public List<ParticularCourse> getTAing() {
        return TAing;
    }

    public void setTAing(List<ParticularCourse> TAing) {
        this.TAing = TAing;
    }

    public List<Course> getTutoring() {
        return Tutoring;
    }

    public void setTutoring(List<Course> tutoring) {
        Tutoring = tutoring;
    }
}

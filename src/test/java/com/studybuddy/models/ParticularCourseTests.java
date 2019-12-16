package com.studybuddy.models;

import com.google.api.services.calendar.model.Events;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ParticularCourseTests {


    @Test
    void testGetters() {
        String courseId = "AS.999.999(01)Fa2018";
        String courseName = "Fake Particular Course";
        String courseDescription = "Fake Description";
        String courseNumber = "AS.999.999";
        String semester = "Fa2018";
        String section = "01";
        String location = "Gilman";
        String credits = "3.00";
        String timeString = "M 3:00 PM - 3:50 PM";
        boolean active = true;
        String instructor = "Fake Instructor";
        User fakeStudent = new User(90911, "Fake Student", "FakeStudent@studybuddy.com");
        User fakeTa = new User(90912, "Fake TA", "FakeTA@studybuddy.com");
        List<User> students = new ArrayList<>();
        students.add(fakeStudent);
        List<User> tas = new ArrayList<>();
        students.add(fakeTa);
        List<Event> classEvents = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2020,1,2,3,0);;
        LocalDateTime end = LocalDateTime.of(2020,1,2,4,0);;
        Event fakeEvent = new Event(111111, "Fake Event", start, end, "Fake Description", students, "Gilman", false);
        classEvents.add(fakeEvent);
        ParticularCourse fakeCourse = new ParticularCourse(courseId, courseName, courseDescription, courseNumber, semester, section,
                location, credits, timeString, active, students, tas, instructor, classEvents);

        assertEquals(courseId, fakeCourse.getCourseId());
        assertEquals(courseName, fakeCourse.getCourseName());
        assertEquals(courseDescription, fakeCourse.getCourseDescription());
        assertEquals(courseNumber, fakeCourse.getCourseNumber());
        assertEquals(semester, fakeCourse.getSemester());
        assertEquals(tas, fakeCourse.getTas());
        assertEquals(instructor, fakeCourse.getInstructor());
        assertEquals(classEvents, fakeCourse.getClassEvents());
        assertEquals(section, fakeCourse.getSection());
        assertEquals(location, fakeCourse.getLocation());
        assertEquals(credits, fakeCourse.getCredits());
        assertEquals(timeString, fakeCourse.getTimeString());
        assertTrue(fakeCourse.isActive());
    }

    @Test
    void testSetters() {
        String courseId = "AS.999.999(01)Fa2018";
        String courseName = "Fake Particular Course";
        String courseDescription = "Fake Description";
        String courseNumber = "AS.999.999";
        String semester = "Fa2018";
        String section = "01";
        String location = "Gilman";
        String credits = "3.00";
        String timeString = "M 3:00 PM - 3:50 PM";
        boolean active = true;
        String instructor = "Fake Instructor";
        User fakeStudent = new User(90911, "Fake Student", "FakeStudent@studybuddy.com");
        User fakeTa = new User(90912, "Fake TA", "FakeTA@studybuddy.com");
        List<User> students = new ArrayList<>();
        students.add(fakeStudent);
        List<User> tas = new ArrayList<>();
        students.add(fakeTa);
        List<Event> classEvents = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2020,1,2,3,0);;
        LocalDateTime end = LocalDateTime.of(2020,1,2,4,0);;
        Event fakeEvent = new Event(111111, "Fake Event", start, end, "Fake Description", students, "Gilman", false);
        classEvents.add(fakeEvent);
        ParticularCourse fakeCourse = new ParticularCourse(courseId, courseName, courseDescription, courseNumber, semester, section,
                location, credits, timeString, active, students, tas, instructor, classEvents);

        String newCourseId = "AS.999.999(01)Fa2021";
        String newCourseName = "New Fake Particular Course";
        String newCourseDescription = "New Fake Description";
        String newCourseNumber = "AS.999.888";
        String newSemester = "Fa2021";
        String newSection = "02";
        String newLocation = "Shaffer";
        String newCredits = "4.00";
        String newTimeString = "MWF 3:00 PM - 3:50 PM";
        String newInstructor = "New Fake Instructor";
        User newFakeStudent = new User(909112, "Fake Student 2", "FakeStudent2@studybuddy.com");
        User newFakeTa = new User(909122, "Fake TA 2", "FakeTA2@studybuddy.com");
        List<User> newStudents = new ArrayList<>();
        newStudents.add(newFakeStudent);
        List<User> newTas = new ArrayList<>();
        students.add(newFakeTa);
        List<Event> newClassEvents = new ArrayList<>();
        LocalDateTime newStart = LocalDateTime.of(2020,1,2,3,0);;
        LocalDateTime newEnd = LocalDateTime.of(2020,1,2,4,0);;
        Event newFakeEvent = new Event(1111112, "Fake Event 2", newStart, newEnd, "Fake Description 2", students, "Shaffer", false);
        classEvents.add(newFakeEvent);

        fakeCourse.setActive(false);
        fakeCourse.setClassEvents(newClassEvents);
        fakeCourse.setCourseDescription(newCourseDescription);
        fakeCourse.setCourseId(newCourseId);
        fakeCourse.setCourseName(newCourseName);
        fakeCourse.setCredits(newCredits);
        fakeCourse.setInstructor(newInstructor);
        fakeCourse.setSemester(newSemester);
        fakeCourse.setLocation(newLocation);
        fakeCourse.setTimeString(newTimeString);
        fakeCourse.setSection(newSection);
        fakeCourse.setTas(newTas);
        fakeCourse.setStudents(newStudents);
        fakeCourse.setCourseNumber(newCourseNumber);

        assertEquals(newCourseId, fakeCourse.getCourseId());
        assertEquals(newCourseName, fakeCourse.getCourseName());
        assertEquals(newCourseDescription, fakeCourse.getCourseDescription());
        assertEquals(newCourseNumber, fakeCourse.getCourseNumber());
        assertEquals(newSemester, fakeCourse.getSemester());
        assertEquals(newTas, fakeCourse.getTas());
        assertEquals(newInstructor, fakeCourse.getInstructor());
        assertEquals(newClassEvents, fakeCourse.getClassEvents());
        assertEquals(newSection, fakeCourse.getSection());
        assertEquals(newLocation, fakeCourse.getLocation());
        assertEquals(newCredits, fakeCourse.getCredits());
        assertEquals(newTimeString, fakeCourse.getTimeString());
        assertEquals(newStudents, fakeCourse.getStudents());
        assertFalse(fakeCourse.isActive());
    }
}

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

public class CourseTests {

    @Test
    void testGetters() {

        String id = "AS.999.999(01)Fa2021";
        String courseNumber = "AS.999.999";
        User fakeAlum = new User(90909, "Fake Alumni", "FakeAlum@studybuddy.com");
        User fakeTutor = new User(90910, "Fake Tutor", "FakeTutor@studybuddy.com");
        User fakeStudent = new User(90911, "Fake Student", "FakeStudent@studybuddy.com");
        User fakeTa = new User(90912, "Fake TA", "FakeTA@studybuddy.com");
        List<User> alumni = new ArrayList<>();
        alumni.add(fakeAlum);
        List<User> potentialTutors = new ArrayList<>();
        potentialTutors.add(fakeTutor);

        List<User> students = new ArrayList<>();
        students.add(fakeStudent);
        List<User> tas = new ArrayList<>();
        students.add(fakeTa);
        List<Event> classEvents = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2020, 1, 2, 3, 0);
        ;
        LocalDateTime end = LocalDateTime.of(2020, 1, 2, 4, 0);
        ;
        Event fakeEvent = new Event(111111, "Fake Event", start, end, "Fake Description", students, "Gilman");
        classEvents.add(fakeEvent);
        ParticularCourse fakeSemester = new ParticularCourse("AS.999.999(01)Fa2018", "Fake Particular Course", "Fake Course",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                students, tas, "Fake Instructor", classEvents);
        ParticularCourse fakeActive = new ParticularCourse("AS.999.999(01)Fa2021", "Fake Active Particular Course", "Fake Course",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                students, tas, "Fake Instructor", classEvents);
        List<ParticularCourse> semesters = new ArrayList<>();
        semesters.add(fakeSemester);
        List<ParticularCourse> activeClasses = new ArrayList<>();
        activeClasses.add(fakeActive);
        Map<User, String> grades = new HashMap<>();
        grades.put(fakeStudent, "A");
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
        ;

        Course testCourse = new Course(id, courseNumber, alumni, potentialTutors, semesters, activeClasses, grades, time);

        assertEquals(id, testCourse.getId());
        assertEquals(courseNumber, testCourse.getCourseNumber());
        assertEquals(alumni, testCourse.getAlumni());
        assertEquals(potentialTutors, testCourse.getPotentialTutors());
        assertEquals(semesters, testCourse.getSemesters());
        assertEquals(activeClasses, testCourse.getActiveClasses());
        assertEquals(grades, testCourse.getGrades());
        assertEquals(time, testCourse.getTime());
    }

    @Test
    void testSetters() {
        String id = "AS.999.999(01)Fa2021";
        String courseNumber = "AS.999.999";
        User fakeAlum = new User(90909, "Fake Alumni", "FakeAlum@studybuddy.com");
        User fakeTutor = new User(90910, "Fake Tutor", "FakeTutor@studybuddy.com");
        User fakeStudent = new User(90911, "Fake Student", "FakeStudent@studybuddy.com");
        User fakeTa = new User(90912, "Fake TA", "FakeTA@studybuddy.com");
        List<User> alumni = new ArrayList<>();
        alumni.add(fakeAlum);
        List<User> potentialTutors = new ArrayList<>();
        potentialTutors.add(fakeTutor);

        List<User> students = new ArrayList<>();
        students.add(fakeStudent);
        List<User> tas = new ArrayList<>();
        students.add(fakeTa);
        List<Event> classEvents = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2020, 1, 2, 3, 0);
        ;
        LocalDateTime end = LocalDateTime.of(2020, 1, 2, 4, 0);
        ;
        Event fakeEvent = new Event(111111, "Fake Event", start, end, "Fake Description", students, "Gilman");
        classEvents.add(fakeEvent);
        ParticularCourse fakeSemester = new ParticularCourse("AS.999.999(01)Fa2018", "Fake Particular Course", "Fake Course",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                students, tas, "Fake Instructor", classEvents);
        ParticularCourse fakeActive = new ParticularCourse("AS.999.999(01)Fa2021", "Fake Active Particular Course", "Fake Course",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                students, tas, "Fake Instructor", classEvents);
        List<ParticularCourse> semesters = new ArrayList<>();
        semesters.add(fakeSemester);
        List<ParticularCourse> activeClasses = new ArrayList<>();
        activeClasses.add(fakeActive);
        Map<User, String> grades = new HashMap<>();
        grades.put(fakeStudent, "A");
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Course testCourse = new Course(id, courseNumber, alumni, potentialTutors, semesters, activeClasses, grades, time);

        String newId = "AS.999.888(01)Fa2022";
        String newCourseNumber = "AS.999.888";
        User newfakeAlum = new User(909092, "Fake Alumni 2", "FakeAlum2@studybuddy.com");
        User newfakeTutor = new User(909102, "Fake Tutor 2", "FakeTutor2@studybuddy.com");
        User newfakeStudent = new User(909112, "Fake Student 2", "FakeStudent2@studybuddy.com");
        User newfakeTa = new User(909122, "Fake TA 2", "FakeTA2@studybuddy.com");
        List<User> newalumni = new ArrayList<>();
        alumni.add(newfakeAlum);
        List<User> newpotentialTutors = new ArrayList<>();
        potentialTutors.add(newfakeTutor);

        List<User> newstudents = new ArrayList<>();
        students.add(newfakeStudent);
        List<User> newtas = new ArrayList<>();
        students.add(newfakeTa);
        List<Event> newclassEvents = new ArrayList<>();
        LocalDateTime newstart = LocalDateTime.of(2020, 1, 2, 4, 0);
        ;
        LocalDateTime newend = LocalDateTime.of(2020, 1, 2, 5, 0);
        ;
        Event newfakeEvent = new Event(1111112, "Fake Event 2", newstart, newend, "Fake Description 2", newstudents, "Mergenthaler");
        classEvents.add(newfakeEvent);
        ParticularCourse newfakeSemester = new ParticularCourse("AS.999.888(01)Fa2018", "Fake Particular Course 2", "Fake Course 2",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                newstudents, newtas, "Fake Instructor", newclassEvents);
        ParticularCourse newfakeActive = new ParticularCourse("AS.999.888(01)Fa2021", "Fake Active Particular Course 2", "Fake Course 2",
                "AS.999.999", "Fa2021", "01", "Gilman", "3.00", "M 3:00 PM - 3:50 PM", true,
                newstudents, newtas, "Fake Instructor", newclassEvents);
        List<ParticularCourse> newsemesters = new ArrayList<>();
        semesters.add(newfakeSemester);
        List<ParticularCourse> newactiveClasses = new ArrayList<>();
        activeClasses.add(newfakeActive);
        Map<User, String> newgrades = new HashMap<>();
        grades.put(newfakeStudent, "B");
        LocalDateTime newtime = LocalDateTime.of(2020, 1, 1, 2, 0);

        testCourse.setId(newId);
        testCourse.setActiveClasses(newactiveClasses);
        testCourse.setAlumni(newalumni);
        testCourse.setGrades(newgrades);
        testCourse.setCourseNumber(newCourseNumber);
        testCourse.setSemesters(newsemesters);
        testCourse.setPotentialTutors(newpotentialTutors);
        testCourse.setTime(newtime);

        assertEquals(newId, testCourse.getId());
        assertEquals(newactiveClasses, testCourse.getActiveClasses());
        assertEquals(newalumni, testCourse.getAlumni());
        assertEquals(newgrades, testCourse.getGrades());
        assertEquals(newCourseNumber, testCourse.getCourseNumber());
        assertEquals(newsemesters, testCourse.getSemesters());
        assertEquals(newtime, testCourse.getTime());
        assertEquals(newpotentialTutors, testCourse.getPotentialTutors());
    }
}

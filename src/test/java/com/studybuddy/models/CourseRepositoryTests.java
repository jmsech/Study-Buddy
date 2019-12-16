package com.studybuddy.models;

import com.studybuddy.repositories.AuthenticationRepository;
import com.studybuddy.repositories.CourseRepository;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CourseRepositoryTests {

    static java.sql.Connection connection;

    @BeforeAll
    public static void createConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");
    }

    @Test
    void testCreateClassInDB() throws SQLException {

        String courseId = "AS.999.999(01)Fa2021";
        String courseNum = "AS.999.999";
        String courseDescription = "Test Course Description";
        String courseSectionNum = "01";
        String courseName = "Test Course";
        String instructorName = "Buddy, Study";
        String semester = "Fa2021";
        String location = "Gilman";
        String credits = "3.00";
        String timeString = "M 9:00 AM - 9:50 AM";
        boolean isActive = true;

        boolean success = CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription,
                courseSectionNum, courseName, instructorName, semester, location,
                credits, timeString, isActive);
        assertTrue(success);

        var statement = connection.prepareStatement("SELECT courseNum, courseDescription, courseSectionNum, courseName," +
                " instructorName, semester, location, credits, timeString FROM courses WHERE courseId = 'AS.999.999(01)Fa2021'");
        var result = statement.executeQuery();
        String checkCourseNum = result.getString("courseNum");
        String checkCourseDescription = result.getString("courseDescription");
        String checkCourseSectionNum = result.getString("courseSectionNum");
        String checkCourseName = result.getString("courseName");
        String checkInstructorName = result.getString("instructorName");
        String checkSemester = result.getString("semester");
        String checkLocation = result.getString("location");
        String checkCredits = result.getString("credits");
        String checkTimeString = result.getString("timeString");
        statement.close();

        assertEquals(courseNum, checkCourseNum);
        assertEquals(courseDescription, checkCourseDescription);
        assertEquals(courseSectionNum, checkCourseSectionNum);
        assertEquals(courseName, checkCourseName);
        assertEquals(instructorName, checkInstructorName);
        assertEquals(semester, checkSemester);
        assertEquals(location, checkLocation);
        assertEquals(credits, checkCredits);
        assertEquals(timeString, checkTimeString);

    }

    @AfterAll
    public static void cleanDatabase() throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM courses WHERE courseId = 'AS.999.999(01)Fa2021'");
        statement.executeUpdate();
        statement.close();
    }

}

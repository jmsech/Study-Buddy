package com.studybuddy.repositories;

import com.studybuddy.models.ParticularCourse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public static List<ParticularCourse> getCoursesForUser(int userId, Connection connection) throws SQLException {

        List<String> courseIDs = CourseRepository.getCourseIdListFromUserId(connection, userId);

        List<ParticularCourse> courses = CourseRepository.getCourseListFromCourseIdList(connection, courseIDs);

        return courses;
    }

    public static List<String> getCourseIdListFromUserId(Connection connection, int userId) throws SQLException {

        // TODO:
        //  1) Pull list of courseIDs from associative database. The CODE BELOW DOES NOT work but it might be a good
        //  template from EventRepository

        // FIXME Fix this table call
        var statement = connection.prepareStatement("SELECT e.id, e.title, e.startTime, e.endTime, e.description, e.location, e.isGoogleEvent " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = false");

        statement.setInt(1, userId);
        var result = statement.executeQuery();
        var courseIDs = new ArrayList<String>();

        while (result.next()) {
            courseIDs.add(result.getString("courseId"));
        }
        statement.close();

        return courseIDs;
    }

    public static List<ParticularCourse> getCourseListFromCourseIdList(Connection connection, List<String> courseIDs) throws SQLException {

        var courses = new ArrayList<ParticularCourse>();
        ArrayList<PreparedStatement> statements = new ArrayList<>();

        for (String courseId : courseIDs) {
            var result = CourseRepository.loadCourseFields(connection, courseId, statements);

            var studentIdList = result.getString("students");
            var students = UserRepository.createUserListFromIdList(connection, studentIdList);

            var taIdList = result.getString("tas");
            var tas = UserRepository.createUserListFromIdList(connection, taIdList);

            var instructorIds = result.getString("tas");
            var instructors = UserRepository.createUserListFromIdList(connection, instructorIds);

            var courseEventIds = result.getString("tas");
            var courseEvents = UserRepository.createUserListFromIdList(connection, courseEventIds);

            courses.add(
                    new ParticularCourse(
                            courseId,
                            result.getString("courseNumber"),
                            result.getString("semester"),
                            result.getString("sectionNumber"),
                            result.getBoolean("active"),
                            students,
                            tas,
                            instructors,
                            courseEvents
                    )
            );
        }

        // TODO:
        //  1) Make course comparator

        for (var s : statements) {s.close();}
        courses.sort(new ParticularCourse.CourseComparator());

        return courses;
    }

    private static java.sql.ResultSet loadCourseFields(Connection connection, String courseId, List<PreparedStatement> statements) throws SQLException {
        // TODO:
        //  1) Pull course information from course database. Return a SQL.ResultSet please
        //  The CODE BELOW DOES NOT WORK but it is a good template from eventRepository

        // FIXME Fix this table call
        var statement = connection.prepareStatement("SELECT u.id, u.email, u.firstName, u.lastName FROM events_to_users_mapping AS etum " +
                "INNER JOIN users AS u ON etum.userId = u.id WHERE etum.eventId = ?");

        statement.setString(1, courseId);
        statements.add(statement);

        return statement.executeQuery();
    }

    public static int addCourseToUser(Connection connection, String courseId, int userId) throws SQLException {

        // TODO: (Update Functions below, this function is fine)
        //  1) Check to see if the course exists in the database (If course not in DB, return 1)
        //  2) Get the list of students in the the course
        //  3) Check if student is already on list (If student not on list, return 2)
        //  4) Add Student to list and update course roster in DB (return 0)

        List<Integer> rosterIDs = CourseRepository.getRosterIDs(connection, courseId);

        if (rosterIDs == null) { return 1; }

        if (rosterIDs.contains(userId)) {return 2; }

        rosterIDs.add(userId);

        CourseRepository.updateCourseRoster(connection, courseId, rosterIDs);

        return 0;
    }

    public static List<Integer> getRosterIDs(Connection connection, String courseId) {

        // TODO:
        //  1) Figure out how to pull data from DB

        return null; // Stub
    }

    public static void updateCourseRoster(Connection connection, String courseId, List<Integer> rosterIDs) {
        // TODO:
        //  1) Update roster of course corresponding to courseId to the new roster list
    }

    public static void archiveOldCourses(Connection connection) {
        // TODO:
        //  1) Figure out how to determine when the semester is over and remove all classes
        //  which are no longer in session
    }

    public static void removeCourse(Connection connection, int userId, String courseId) throws SQLException {
        var roster = CourseRepository.getRosterFromCourseId(connection, courseId);
        roster.remove(userId);
        CourseRepository.updateCourseRoster(connection, courseId, roster);
    }

    public static List<Integer> getRosterFromCourseId(Connection connection, String courseId) throws SQLException {
        var statements = new ArrayList<PreparedStatement>();
        var result = CourseRepository.loadCourseFields(connection, courseId, statements);

        var studentIdList = result.getString("students");
        return IdRepository.createIdListFromInviteList(connection, studentIdList);
    }
}

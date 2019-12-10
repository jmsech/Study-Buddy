package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.ParticularCourse;
import com.studybuddy.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public static boolean createCourseInDB(Connection connection, String courseId, String courseNum, String courseDescription,
                                 String courseSectionNum, String courseName, String instructorName, String semester, String location,
                                 String credits, boolean isActive) throws SQLException {

        // FIXME: There has to be a better way to check but this will do for now
        var check_statement = connection.prepareStatement("SELECT 1 FROM  courses WHERE courseId = ?");
        check_statement.setString(1, courseId);
        var check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            return false;
        }
        check.close();

        var statement = connection.prepareStatement("INSERT INTO courses (courseId, courseNum, " +
                "courseDescription, courseSectionNum, courseName, instructorName, semester, location, credits, " +
                "isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        statement.setString(1, courseId);
        statement.setString(2, courseNum);
        statement.setString(3, courseDescription);
        statement.setString(4, courseSectionNum);
        statement.setString(5, courseName);
        statement.setString(6, instructorName);
        statement.setString(7, semester);
        statement.setString(8, location);
        statement.setString(9, credits);
        statement.setBoolean(10, isActive);
        statement.executeUpdate();
        statement.close();

        return true;
    }

    public static List<ParticularCourse> getCoursesForUser(int userId, Connection connection) throws SQLException {

        List<String> courseIDs = CourseRepository.getCourseIdListFromUserId(connection, userId);

        List<ParticularCourse> courses = CourseRepository.getCourseListFromCourseIdList(connection, courseIDs);

        return courses;
    }

    public static List<String> getCourseIdListFromUserId(Connection connection, int userId) throws SQLException {

        // TODO: <COMPLETED>
        //  1) Pull list of courseIDs from associative database.
        var statement = connection.prepareStatement("SELECT c.courseId, c.courseNum, c.courseDescription," +
                " c.courseSectionNum, c.courseName, c.semester, c.instructorName, c.location, c.credits, c.isActive FROM courses " +
                "AS c INNER JOIN courses_to_users_mapping AS ctum ON c.courseId = ctum.courseId INNER JOIN users " +
                "AS u ON ctum.userId = u.id WHERE u.id = ?");
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

//            var studentIdList = result.getString("studentIds");
//            var students = UserRepository.createUserListFromIdList(connection, studentIdList);
            var students = new ArrayList<User>();

            //var taIdList = result.getString("taIds");
            //var tas = UserRepository.createUserListFromIdList(connection, taIdList);
            var tas = new ArrayList<User>();

            //var instructorIds = result.getString("instructor");
            //var instructors = UserRepository.createUserListFromIdList(connection, instructorIds);
            var instructor = result.getString("instructorName");

            //var courseEventIds = result.getString("courseIds");
           // var courseEvents = UserRepository.createUserListFromIdList(connection, courseEventIds);
            var courseEvents  = new ArrayList<Event>();

            courses.add(
                    new ParticularCourse(
                            courseId,
                            result.getString("courseName"),
                            result.getString("courseDescription"),
                            result.getString("courseNum"),
                            result.getString("semester"),
                            result.getString("courseSectionNum"),
                            result.getString("location"),
                            result.getString("credits"),
                            result.getBoolean("isActive"),
                            students,
                            tas,
                            instructor,
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
        // TODO: <COMPLETED>
        //  1) Pull course information from course database. Return a SQL.ResultSet please

        var statement = connection.prepareStatement("SELECT c.courseNum, c.courseDescription, c.courseSectionNum, " +
                "c.courseName, c.semester, c.instructorName, c.location, c.credits, c.isActive FROM courses AS c " +
                "WHERE c.courseId = ? ");

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

        CourseRepository.updateCourseRoster(connection, courseId, userId);

        return 0;
    }

    public static List<Integer> getRosterIDs(Connection connection, String courseId) throws SQLException {

        // TODO: <COMPLETED>
        //  1) Figure out how to pull data from DB

        var statement = connection.prepareStatement("SELECT ctum.userid FROM courses_to_users_mapping AS ctum WHERE ctum.courseId = ?");

        statement.setString(1, courseId);
        var result = statement.executeQuery();

        var rosterIds = new ArrayList<Integer>();
        while (result.next()) {
            rosterIds.add(result.getInt("userId"));
        }

        return rosterIds;
    }

    public static void updateCourseRoster(Connection connection, String courseId, int userId) throws SQLException {
        // TODO: <COMPLETED>
        //  1) Update roster of course corresponding to courseId to the new roster list
        var statement = connection.prepareStatement("INSERT INTO courses_to_users_mapping(courseId, userId) VALUES (?, ?)");

        statement.setString(1, courseId);
        statement.setInt(2, userId);
        statement.executeUpdate();
        statement.close();
    }

    public static void archiveOldCourses(Connection connection) {
        // TODO:
        //  1) Figure out how to determine when the semester is over and remove all classes
        //  which are no longer in session
    }

    public static void removeCourse(Connection connection, int userId, String courseId) throws SQLException {
        var roster = CourseRepository.getRosterFromCourseId(connection, courseId);
        roster.remove(userId);
        var statement = connection.prepareStatement("DELETE FROM courses_to_users_mapping WHERE courseID = ? AND userId = ?");
        statement.setString(1, courseId);
        statement.setInt(2, userId);

        statement.executeQuery();
        statement.close();
    }

    public static List<Integer> getRosterFromCourseId(Connection connection, String courseId) throws SQLException {
        var statements = new ArrayList<PreparedStatement>();
        var result = CourseRepository.loadCourseFields(connection, courseId, statements);

        var studentIdList = result.getString("students");
        return IdRepository.createIdListFromInviteList(connection, studentIdList);
    }
}

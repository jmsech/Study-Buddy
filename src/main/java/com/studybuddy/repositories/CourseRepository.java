package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.ParticularCourse;
import com.studybuddy.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public static boolean createCourseInDB(Connection connection, String courseId, String courseNum, String courseDescription,
                                 String courseSectionNum, String courseName, String instructorName, String semester, String location,
                                 String credits, String timeString, boolean isActive) throws SQLException {

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
                "courseDescription, courseSectionNum, courseName, instructorName, semester, location, credits, timeString, " +
                "isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        statement.setString(1, courseId);
        statement.setString(2, courseNum);
        statement.setString(3, courseDescription);
        statement.setString(4, courseSectionNum);
        statement.setString(5, courseName);
        statement.setString(6, instructorName);
        statement.setString(7, semester);
        statement.setString(8, location);
        statement.setString(9, credits);
        statement.setString(10, timeString);
        statement.setBoolean(11, isActive);
        statement.executeUpdate();
        statement.close();

        add_course_lectures_to_DB(connection, timeString, semester, courseName, location, courseId);

        return true;
    }

    public static List<ParticularCourse> getCoursesForUser(Connection connection, int userId) throws SQLException {

        List<String> courseIDs = CourseRepository.getActiveCourseIdListFromUserId(connection, userId);

        List<ParticularCourse> courses = CourseRepository.getCourseListFromCourseIdList(connection, courseIDs);

        return courses;
    }

    public static List<String> getActiveCourseIdListFromUserId(Connection connection, int userId) throws SQLException {

        // TODO: <COMPLETED>
        //  1) Pull list of courseIDs from associative database.
        var statement = connection.prepareStatement("SELECT c.courseId FROM courses " +
                "AS c INNER JOIN courses_to_users_mapping AS ctum ON c.courseId = ctum.courseId INNER JOIN users " +
                "AS u ON ctum.userId = u.id WHERE u.id = ? AND c.isActive = 1");
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

            //var studentIdList = result.getString("studentIds");
            //var students = UserRepository.createUserListFromIdList(connection, studentIdList);
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
                            result.getString("timeString"),
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

    public static int addCourseToUser(Connection connection, String courseId, int userId) throws SQLException {

        // TODO: (Update Functions below, this function is fine)
        //  1) Check to see if the course exists in the database (If course not in DB, return 1)
        //  2) Get the list of students in the the course
        //  3) Check if student is already on list (If student not on list, return 2)
        //  4) Add Student to list and update course roster in DB (return 0)

        var statement = connection.prepareStatement("SELECT eventId FROM courses_to_events_mapping WHERE courseId = ?");
        statement.setString(1, courseId);
        var result = statement.executeQuery();
        while (result.next()) {
            int eventId = result.getInt("eventId");
            var statement2 = connection.prepareStatement("INSERT INTO events_to_users_mapping(userId, eventId) VALUES (?, ?)");
            statement2.setInt(1, userId);
            statement2.setInt(2, eventId);
            statement2.executeUpdate();
            statement2.close();
        }
        statement.close();

        List<Integer> rosterIDs = CourseRepository.getRosterIDs(connection, courseId);
        if (rosterIDs == null) { return 1; }
        if (rosterIDs.contains(userId)) {return 2; }
        CourseRepository.updateCourseRoster(connection, courseId, userId);

        return 0;
    }

    public static List<Integer> getRosterIDs(Connection connection, String courseId) throws SQLException {

        // TODO: <COMPLETED>
        //  1) Figure out how to pull data from DB

        var statement = connection.prepareStatement("SELECT ctum.userId FROM courses_to_users_mapping AS ctum WHERE ctum.courseId = ?");
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static java.sql.ResultSet loadCourseFields(Connection connection, String courseId, List<PreparedStatement> statements) throws SQLException {
        // TODO: <COMPLETED>
        //  1) Pull course information from course database. Return a SQL.ResultSet please

        var statement = connection.prepareStatement("SELECT c.courseNum, c.courseDescription, c.courseSectionNum, " +
                "c.courseName, c.semester, c.instructorName, c.location, c.credits, c.timeString, c.isActive FROM courses AS c " +
                "WHERE c.courseId = ? ");

        statement.setString(1, courseId);
        statements.add(statement);

        return statement.executeQuery();
    }

    private static long getMillis(String time, String ampm) {
        try {
            if (time.substring(0, 2).equals("12")) { time = "0" + time.substring(2); }
            long sec = 0;
            sec += 60 * Integer.parseInt(time.substring(time.length() - 1));
            sec += 60 * 10 * Integer.parseInt(time.substring(time.length() - 2, time.length() - 1));
            sec += 60 * 60 * Integer.parseInt(time.substring(time.length() - 4, time.length() - 3));
            try { sec += 60 * 60 * 10 * Integer.parseInt(time.substring(time.length() - 5, time.length() - 4)); } catch (Exception e) { /* Do Nothing*/ }
            if (ampm.equals("PM")) { sec += 60*60*12; }
            return sec;
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }

    private static void add_12_weeks_of_courses_to_DB(Connection connection, LocalDateTime day, long startSec,
                                                      long endSec, String courseName, String location, String courseId) throws SQLException {
        // FIXME: Change to 12 later
        for (int j = 0; j < 1; j++) {
            LocalDateTime startOfClass = LocalDateTime.ofEpochSecond(day.toEpochSecond(ZoneOffset.UTC) + startSec + j*60*60*24*7, 0, ZoneOffset.ofHours(0));
            LocalDateTime endOfClass = LocalDateTime.ofEpochSecond(day.toEpochSecond(ZoneOffset.UTC) + endSec + j*60*60*24*7, 0, ZoneOffset.ofHours(0));
            java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startOfClass);
            java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endOfClass);
            int eventId = EventRepository.createEventInDB(connection, new ArrayList<>(), courseName + " Lecture", sqlStartDate, sqlEndDate, "", location, courseId.hashCode());
            var statement = connection.prepareStatement("INSERT INTO courses_to_events_mapping(courseId, eventId) VALUES (?, ?)");
            statement.setString(1, courseId);
            statement.setInt(2, eventId);
            statement.executeUpdate();
            statement.close();
        }
    }

    private static boolean add_course_lectures_to_DB(Connection connection, String timeString, String semester,
                                                     String courseName, String location, String courseId) throws SQLException {
        String s = timeString.replace('\n', ' ');
        String[] split = s.split(" ");
        int num_dates = (int) (split.length/6.0);

        if (split.length %6 != 0 || split.length == 0) {return false; }

        String year = semester.substring(semester.length() - 4);
        int y;
        try { y = Integer.parseInt(year); } catch (NumberFormatException e) { return false; }
        LocalDateTime firstFullWeek = LocalDateTime.of(y,6,1,0,0,0);
        // FIXME: We have changed the semester start temporarily so that you can see classes that have ended
        if (semester.contains("Fa")) { firstFullWeek = LocalDateTime.of(y,12,10,0,0,0); }
        if (semester.contains("Sp")) { firstFullWeek = LocalDateTime.of(y,2,1,0,0,0); }

        for (int i = 0; i < num_dates; i++) {
            String daysOfWeek = split[6*i];
            String startTime = split[6*i+1];
            String startAMPM = split[6*i+2];
            long startSec = getMillis(startTime, startAMPM);
            String endTime = split[6*i + 4];
            String endAMPM = split[6*i + 5];
            long endSec = getMillis(endTime, endAMPM);
            if (daysOfWeek.contains("M")) {
                LocalDateTime monday = firstFullWeek.with(DayOfWeek.MONDAY);
                add_12_weeks_of_courses_to_DB(connection, monday, startSec, endSec, courseName, location, courseId);
            }
            if (daysOfWeek.contains("TT") || daysOfWeek.contains("TW") || daysOfWeek.contains("TF") ||
                    (daysOfWeek.contains("T") && ! (daysOfWeek.contains("Th")))) {
                LocalDateTime tuesday = firstFullWeek.with(DayOfWeek.TUESDAY);
                add_12_weeks_of_courses_to_DB(connection, tuesday, startSec, endSec, courseName, location, courseId);
            }
            if (daysOfWeek.contains("W")) {
                LocalDateTime wednesday = firstFullWeek.with(DayOfWeek.WEDNESDAY);
                add_12_weeks_of_courses_to_DB(connection, wednesday, startSec, endSec, courseName, location, courseId);
            }
            if (daysOfWeek.contains("Th")) {
                LocalDateTime thursday = firstFullWeek.with(DayOfWeek.THURSDAY);
                add_12_weeks_of_courses_to_DB(connection, thursday, startSec, endSec, courseName, location, courseId);
            }
            if (daysOfWeek.contains("F")) {
                LocalDateTime friday = firstFullWeek.with(DayOfWeek.FRIDAY);
                add_12_weeks_of_courses_to_DB(connection, friday, startSec, endSec, courseName, location, courseId);
            }
        }
        return true;
    }
}

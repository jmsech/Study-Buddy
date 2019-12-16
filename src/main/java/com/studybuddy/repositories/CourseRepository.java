package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.ParticularCourse;
import com.studybuddy.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public static boolean createCourseInDB(Connection connection, String courseId, String courseNum, String courseDescription,
                                 String courseSectionNum, String courseName, String instructorName, String semester, String location,
                                 String credits, String timeString, boolean isActive) throws SQLException {

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

    public static void addDeadlineToCourse(Connection connection, String courseId, String title, String description, Timestamp time) throws SQLException {
        var eventId = EventRepository.createEventInDB(connection, new ArrayList<>(), title, time, time, description, "", courseId.hashCode(), true);
        var statement = connection.prepareStatement("INSERT INTO courses_to_deadlines_mapping(courseId, eventId) VALUES (?, ?)");
        statement.setString(1, courseId);
        statement.setInt(2, eventId);
        statement.executeUpdate();
        statement.close();

        var students = CourseRepository.getRosterIDs(connection, courseId);
        for (var sid : students) {
            EventRepository.addEventToUser(connection, sid, eventId);
        }
    }

    public static void removeDeadlineFromCourse(Connection connection, int eventId, String courseId) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ? AND hostId = ?");
        statement.setInt(1, eventId);
        statement.setInt(1, courseId.hashCode());
        statement.executeUpdate();
        statement.close();
    }

    public static List<ParticularCourse> getCoursesForUser(Connection connection, int userId) throws SQLException {
        List<String> courseIDs = CourseRepository.getActiveCourseIdListFromUserId(connection, userId);
        return CourseRepository.getCourseListFromCourseIdList(connection, courseIDs);
    }

    public static List<ParticularCourse> getAllCourses(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT courseId, courseNum, courseDescription, courseSectionNum, " +
                "courseName, semester, instructorName, location, credits, timeString, isActive FROM courses WHERE isActive = TRUE");
        var courses = new ArrayList<ParticularCourse>();
        // Dummy variables for currently unused parameters
        var students = new ArrayList<User>();
        var tas = new ArrayList<User>();
        var courseEvents  = new ArrayList<Event>();

        // Iterate through result
        while (result.next()) {
            courses.add(
                    new ParticularCourse(
                            result.getString("courseId"),
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
                            result.getString("instructorName"),
                            courseEvents
                    )
            );
        }
        return courses;
    }

    public static ParticularCourse loadCourseFields(Connection connection, String courseId) throws SQLException {
        var statement = connection.prepareStatement("SELECT courseId, courseNum, courseDescription, courseSectionNum, " +
                "courseName, semester, instructorName, location, credits, timeString, isActive FROM courses WHERE isActive = TRUE AND courseId = ?");
        statement.setString(1, courseId);
        var result = statement.executeQuery();

        ParticularCourse course;
        var students = new ArrayList<User>();
        var tas = new ArrayList<User>();
        var courseEvents  = new ArrayList<Event>();

        if (result.isBeforeFirst()) {
            course = new ParticularCourse(
                    result.getString("courseId"),
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
                    result.getString("instructorName"),
                    courseEvents
            );
            return course;
        } else {
            return null;
        }
    }

    public static List<String> getActiveCourseIdListFromUserId(Connection connection, int userId) throws SQLException {
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

    private static List<ParticularCourse> getCourseListFromCourseIdList(Connection connection, List<String> courseIDs) throws SQLException {
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

        for (var s : statements) {s.close();}
        courses.sort(new ParticularCourse.CourseComparator());

        return courses;
    }

    public static int addCourseToUser(Connection connection, String courseId, int userId) throws SQLException {
        List<Integer> rosterIDs = CourseRepository.getRosterIDs(connection, courseId);
        if (rosterIDs.contains(userId)) {return 1; }
        CourseRepository.updateCourseRoster(connection, courseId, userId);
        return 0;
    }

    private static List<Integer> getRosterIDs(Connection connection, String courseId) throws SQLException {
        var statement = connection.prepareStatement("SELECT ctum.userId FROM courses_to_users_mapping AS ctum WHERE ctum.courseId = ?");
        statement.setString(1, courseId);
        var result = statement.executeQuery();

        var rosterIds = new ArrayList<Integer>();
        while (result.next()) {
            rosterIds.add(result.getInt("userId"));
        }

        return rosterIds;
    }

    private static void updateCourseRoster(Connection connection, String courseId, int userId) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO courses_to_users_mapping(courseId, userId) VALUES (?, ?)");
        statement.setString(1, courseId);
        statement.setInt(2, userId);
        statement.executeUpdate();
        statement.close();

        var courseEvents = EventRepository.getEventIDsFromCourseID(connection, courseId);
        for (var eid : courseEvents) {
            EventRepository.addEventToUser(connection, userId, eid);
        }
    }

    public static void archiveOldCourses(Connection connection) {
    }

    public static void removeCourseFromUser(Connection connection, int userId, String courseId) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM courses_to_users_mapping WHERE courseID = ? AND userId = ?");
        statement.setString(1, courseId);
        statement.setInt(2, userId);
        statement.executeUpdate();
        statement.close();

        var courseEvents = EventRepository.getEventIDsFromCourseID(connection, courseId);
        for (var eid : courseEvents) {
            EventRepository.deleteByAttendee(eid, userId, connection);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static java.sql.ResultSet loadCourseFields(Connection connection, String courseId, List<PreparedStatement> statements) throws SQLException {

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
            return 0;
        }
    }

    private static void add_12_weeks_of_courses_to_DB(Connection connection, LocalDateTime day, long startSec,
                                                      long endSec, String courseName, String location, String courseId) throws SQLException {

        // FIXME: Change to 12 later (this is also edited for Demo purposes)
        for (int j = 0; j < 1; j++) {
            LocalDateTime startOfClass = LocalDateTime.ofEpochSecond(day.toEpochSecond(ZoneOffset.UTC) + startSec + j*60*60*24*7, 0, ZoneOffset.ofHours(0));
            LocalDateTime endOfClass = LocalDateTime.ofEpochSecond(day.toEpochSecond(ZoneOffset.UTC) + endSec + j*60*60*24*7, 0, ZoneOffset.ofHours(0));
            java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startOfClass);
            java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endOfClass);
            int eventId = EventRepository.createEventInDB(connection, new ArrayList<>(), courseName + " Lecture", sqlStartDate, sqlEndDate, "", location, courseId.hashCode(), false);
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

        // FIXME: We have changed the semester start temporarily (for demo purposes) so that you can see classes that have ended
        if (semester.contains("Fa")) { firstFullWeek = LocalDateTime.of(y,12,18,0,0,0); }
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

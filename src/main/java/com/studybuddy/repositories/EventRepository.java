package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public static ArrayList<Event> getEventsForUser(int userId, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("SELECT e.id, e.title, e.startTime, e.endTime, e.description, e.location, e.isGoogleEvent, e.isDeadline " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = false");

        statement.setInt(1, userId);
        ArrayList<PreparedStatement> statements = new ArrayList<>();
        statements.add(statement);
        var result = statement.executeQuery();

        var events = new ArrayList<Event>();
        while (result.next()) {
            var eventId = result.getInt("id");

            var mappingResult = EventRepository.loadEventAttendees(eventId, connection, statements);

            List<User> inviteList = new ArrayList<>();
            while (mappingResult.next()) {
                String name = mappingResult.getString("firstName") + " " + mappingResult.getString("lastName");
                inviteList.add(
                        new User(
                                mappingResult.getInt("id"),
                                name,
                                mappingResult.getString("email")
                        )
                );
            }

            events.add(
                    new Event(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime(),
                            result.getString("description"),
                            inviteList,
                            result.getString("location"),
                            result.getBoolean("isDeadline")
                    )
            );
        }
        for (var s : statements) {s.close();}

        events = removeDuplicates(connection, events, userId);
        events = tagConflicts(events);
        events.sort(new Event.EventComparator());
        return events;
    }

    public static ArrayList<Event> tagConflicts(ArrayList<Event> events) {
        for (var e : events) {
            if (!e.getConflict()) {
                for (var event : events) {
                    if (Event.overlaps(e, event) && !e.equals(event)) {
                        e.setConflict(true);
                        event.setConflict(true);
                        break;
                    }
                }
            }
        }
        return events;
    }

    public static ArrayList<Event> removeDuplicates(Connection connection, List<Event> eventList, int userId) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        for (var e : eventList) {
            boolean contains = false;
            for (var event : events) {
                if (event.equals(e)) {
                    EventRepository.deleteEvent(connection, (int) e.getId(), userId);
                    contains = true;
                    break;
                }
            }
            if (!contains) { events.add(e); }
        }
        return events;
    }

    public static List<Event> getEventsFromUserCourses(Connection connection, int userId) throws SQLException {
        var events = new ArrayList<Event>();

        var courseIDs = CourseRepository.getActiveCourseIdListFromUserId(connection, userId);
        var eventIDs = new ArrayList<Integer>();
        for (var cid : courseIDs) { eventIDs.addAll(EventRepository.getEventIDsFromCourseID(connection, cid)); }
        for (var eid : eventIDs) {
            var event = EventRepository.loadEventFields(connection, eid, new ArrayList<>());
            if (event != null) {
                events.add(event);
            }
        }

        return events;
    }

    public static Event loadEventFields(Connection connection, int eventId, ArrayList<User> attendees) throws SQLException {
        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description, location, isGoogleEvent, isDeadline " +
                "FROM events WHERE id = ? AND expired = 0");
        statement.setInt(1, eventId);
        var result = statement.executeQuery();
        if (!result.isBeforeFirst()) { return null; }
        var event = new Event(
                result.getInt("id"),
                result.getString("title"),
                result.getTimestamp("startTime").toLocalDateTime(),
                result.getTimestamp("endTime").toLocalDateTime(),
                result.getString("description"),
                attendees,
                result.getString("location"),
                result.getBoolean("isDeadline")
        );
        statement.close();
        return event;
    }

    public static List<Integer> getEventIDsFromCourseID(Connection connection, String courseId) throws SQLException {
        var statement = connection.prepareStatement("SELECT eventId FROM courses_to_events_mapping WHERE courseId = ?");
        statement.setString(1, courseId);
        var result = statement.executeQuery();
        var eventIDs = new ArrayList<Integer>();
        while (result.next()) {
            eventIDs.add(result.getInt("eventId"));
        }
        statement.close();

        statement = connection.prepareStatement("SELECT eventId FROM courses_to_deadlines_mapping WHERE courseId = ?");
        statement.setString(1, courseId);
        result = statement.executeQuery();
        while (result.next()) {
            eventIDs.add(result.getInt("eventId"));
        }
        statement.close();

        return eventIDs;
    }

    private static java.sql.ResultSet loadEventAttendees(int eventId, Connection connection, List<PreparedStatement> statements) throws SQLException {
        var statement = connection.prepareStatement("SELECT u.id, u.email, u.firstName, u.lastName FROM events_to_users_mapping AS etum " +
                "INNER JOIN users AS u ON etum.userId = u.id WHERE etum.eventId = ?");
        statement.setInt(1, eventId);
        statements.add(statement);
        return statement.executeQuery();
    }

    public static int createEventInDB(Connection connection, List<Integer> idInviteList, String title, Timestamp sqlStartDate, Timestamp sqlEndDate, String description, String location, int userId, boolean isDeadline) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, location, hostId, isGoogleEvent, expired, isDeadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setString(5, location);
        statement.setInt(6, userId);
        statement.setBoolean(7, false);
        statement.setBoolean(8, false);
        statement.setBoolean(9, isDeadline);
        statement.executeUpdate();

        // Create event to users mapping
        // Get most recent event's id
        var eventId = getLastEventId(connection);
        updateEventInviteList(eventId, idInviteList, connection);
        statement.close();
        return eventId;
    }

    public static int updateEventInDB(Connection connection, String inviteListString, int userId, String title, Timestamp sqlStartDate, Timestamp sqlEndDate, String description, String location, int eventId) throws SQLException {
        var statement = connection.prepareStatement("UPDATE events SET title = ?, startTime = ?, endTime = ?, description = ?, location = ? WHERE id = ?");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setString(5, location);
        statement.setInt(6, eventId);
        statement.executeUpdate();

        var idInviteList = new ArrayList<Integer>();
        idInviteList.add(userId);
        idInviteList = IdRepository.createIdListFromInviteList(connection, inviteListString, idInviteList);
        if (idInviteList == null) { return 1; }

        statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ?");
        statement.setInt(1, eventId);
        statement.executeUpdate();

        updateEventInviteList(eventId, idInviteList, connection);
        statement.close();
        return 0;
    }

    public static void deleteEvent(Connection connection, int eventId, int callerId) throws SQLException {
        var statement = connection.prepareStatement("SELECT hostId FROM events where id = ?");
        statement.setInt(1, eventId);
        var result = statement.executeQuery();
        var hostId = result.getInt("hostId");
        // Actually delete event if the caller is host
        if (hostId == callerId) {
            deleteByHost(eventId, connection);
        } else { // Simply remove from the associative table
            deleteByAttendee(eventId, callerId, connection);
        }
        statement.close();
    }

    public static void deletePastEvents(Connection connection) throws SQLException {
        LocalDateTime cur = LocalDateTime.now();
        java.sql.Timestamp sqlCur = java.sql.Timestamp.valueOf(cur);
        var statement = connection.prepareStatement("UPDATE events SET expired = true WHERE endTime < ?");
        statement.setTimestamp(1,sqlCur);
        statement.executeUpdate();
        statement.close();
    }

    public static void addEventToUser(Connection connection, int userId, int eventId) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO events_to_users_mapping (eventId, userId) VALUES (?, ?)");
        statement.setInt(1, eventId);
        statement.setInt(2, userId);
        statement.executeUpdate();
        statement.close();
    }

    public static void deleteByAttendee(int eventId, int callerId, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ? AND userId = ?");
        statement.setInt(1, eventId);
        statement.setInt(2, callerId);
        statement.executeUpdate();
        statement.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void updateEventInviteList(int eventId, List inviteList, Connection connection) throws SQLException {
        if (inviteList == null) {return;}
        for (Object id : inviteList) {
            EventRepository.addEventToUser(connection, (int) id, eventId);
        }
    }

    private static int getLastEventId(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT last_insert_rowid() AS eventId FROM events");
        return result.getInt("eventId");
    }

    private static void deleteByHost(int eventId, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ?");
        statement.setInt(1, eventId);
        statement.executeUpdate();
        statement.close();
    }
}

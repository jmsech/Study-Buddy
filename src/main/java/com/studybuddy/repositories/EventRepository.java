package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public static ArrayList<Event> getEventsForUser(int userId, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("SELECT e.id, e.title, e.startTime, e.endTime, e.description, e.location, e.isGoogleEvent " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = false");
        //  AND e.expired = false //TODO: Add and remove this line in the query to care or not care for old events

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
                            result.getString("location")
                    )
            );
        }
        for (var s : statements) {s.close();}

        // TODO (maybe): Let courses be associated with events so that users can have course events automatically added to schedule

        events.sort(new Event.EventComparator());
        return events;
    }

    private static java.sql.ResultSet loadEventAttendees(int eventId, Connection connection, List<PreparedStatement> statements) throws SQLException {
        var statement = connection.prepareStatement("SELECT u.id, u.email, u.firstName, u.lastName FROM events_to_users_mapping AS etum " +
                "INNER JOIN users AS u ON etum.userId = u.id WHERE etum.eventId = ?");
        statement.setInt(1, eventId);
        statements.add(statement);
        return statement.executeQuery();
    }

    public static int createEventInDB(Connection connection, List<Integer> idInviteList, String title, Timestamp sqlStartDate, Timestamp sqlEndDate, String description, String location, int userId) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, location, hostId, isGoogleEvent, expired) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setString(5, location);
        statement.setInt(6, userId);
        statement.setBoolean(7, false);
        statement.setBoolean(8, false);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void updateEventInviteList(int eventId, List inviteList, Connection connection) throws SQLException {
        PreparedStatement statement;
        if (inviteList == null) {inviteList = new ArrayList<>(); }
        for (Object id : inviteList) {
            statement = connection.prepareStatement("INSERT INTO events_to_users_mapping (eventId, userId) VALUES (?, ?)");
            statement.setInt(1, eventId);
            statement.setInt(2, (int) id);
            statement.executeUpdate();
            statement.close(); //FIXME: Brandon moved this
        }
//        statement.close() //FIXME: It was here
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

    private static void deleteByAttendee(int eventId, int callerId, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ? AND userId = ?");
        statement.setInt(1, eventId);
        statement.setInt(2, callerId);
        statement.executeUpdate();
        statement.close();
    }
}

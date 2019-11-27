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
        statement.setInt(1, userId);

        ArrayList<PreparedStatement> statements = new ArrayList<>();
        statements.add(statement);

        var result = statement.executeQuery();

        var events = new ArrayList<Event>();
        while (result.next()) {
            var eventId = result.getInt("id");

            var mappingResult = EventRepository.loadEventFields(eventId, connection, statements);

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

        return events;
    }

    public static java.sql.ResultSet loadEventFields(int eventId, Connection connection, List<PreparedStatement> statements) throws SQLException {
        var statement = connection.prepareStatement("SELECT u.id, u.email, u.firstName, u.lastName FROM events_to_users_mapping AS etum " +
                "INNER JOIN users AS u ON etum.userId = u.id WHERE etum.eventId = ?");
        statement.setInt(1, eventId);
        statements.add(statement);
        return statement.executeQuery();
    }

    public static java.sql.ResultSet getIdFromEmail(String email, Connection connection, List<PreparedStatement> statements) throws SQLException {
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
        statement.setString(1, email);
        statements.add(statement);
        return statement.executeQuery();
    }

    public static void addUsersToEventListInDB(Connection connection, List<Integer> idInviteList, String title, Timestamp sqlStartDate, Timestamp sqlEndDate, String description, String location, int userId) throws SQLException {
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

        idInviteList = removeDuplicates(idInviteList);
        insertInviteList(eventId, idInviteList, connection);

        statement.close();
    }

    public static List<Integer> createIdListFromUserIdAndInviteList(Connection connection, String inviteListString, int userId) throws SQLException {
        List<Integer> idInviteList = new ArrayList<>();
        ArrayList<PreparedStatement> statements = new ArrayList<>();
        idInviteList.add(userId);
        // Get ids from email invite list
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                var result = getIdFromEmail(email, connection, statements);
                if (result.next()) {
                    idInviteList.add(result.getInt("id"));
                } else {
                    return null;
                }
            }
        }
        for (var s : statements) {s.close();}
        return idInviteList;
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

        List<Integer> idInviteList = new ArrayList<>();
        idInviteList.add(userId);

        // Get ids from email invite list
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                statement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
                statement.setString(1, email);
                var result = statement.executeQuery();
                if (result.next()) {
                    idInviteList.add(result.getInt("id"));
                } else {
                    return 1;
                }
            }
        }

        statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ?");
        statement.setInt(1, eventId);
        statement.executeUpdate();
        idInviteList = removeDuplicates(idInviteList);
        insertInviteList(eventId, idInviteList, connection);
        statement.close();
        return 0;
    }

    private static void insertInviteList(int eventId, List inviteList, Connection connection) throws SQLException {
        PreparedStatement statement = null;
        assert inviteList != null;
        for (Object id : inviteList) {
            statement = connection.prepareStatement("INSERT INTO events_to_users_mapping (eventId, userId) VALUES (?, ?)");
            statement.setInt(1, eventId);
            statement.setInt(2, (int) id);
            statement.executeUpdate();
        }
        assert statement != null;
        statement.close();
    }

    public static List<Integer> removeDuplicates(List<Integer> list) {
        List<Integer> resultList = new ArrayList<>();
        for (Integer item : list) {
            if (!resultList.contains(item)) {
                resultList.add(item);
            }
        }
        return resultList;
    }

    public static int getLastEventId(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT last_insert_rowid() AS eventId FROM events");
        return result.getInt("eventId");
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

    public static void deletePastEvents(Connection connection) throws SQLException{
        LocalDateTime cur = LocalDateTime.now();
        java.sql.Timestamp sqlCur = java.sql.Timestamp.valueOf(cur);
        var statement = connection.prepareStatement("UPDATE events SET expired = true WHERE endTime < ?");
        statement.setTimestamp(1,sqlCur);
        statement.executeUpdate();
        statement.close();
    }
}

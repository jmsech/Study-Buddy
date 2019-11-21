package com.studybuddy.controllers;
import com.studybuddy.models.Event;
import com.studybuddy.models.User;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class EventsController {
    private Connection connection;

    EventsController(Connection connection) {
        this.connection = connection;
    }

    void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var userId = ctx.pathParam("userId", Integer.class).get();

        var statement = this.connection.prepareStatement("SELECT e.id, e.title, e.startTime, e.endTime, e.description, e.location, e.isGoogleEvent " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = false");
        statement.setInt(1, userId);
        var result = statement.executeQuery();
        ArrayList<User> stu = new ArrayList<>();
        while (result.next()) {
            var eventId = result.getInt("id");
            var mappingStatement = this.connection.prepareStatement("SELECT u.id, u.email, u.firstName, u.lastName FROM events_to_users_mapping AS etum " +
                    "INNER JOIN users AS u ON etum.userId = u.id WHERE etum.eventId = ?");
            mappingStatement.setInt(1, eventId);
            var mappingResult = mappingStatement.executeQuery();
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
        result.close();
        statement.close();
        ctx.json(events);
    }

    void createEvent(Context ctx) throws SQLException {
        var title = ctx.formParam("title", String.class).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        // Ensure that startTime is before endTime
        if (!endTime.isAfter(startTime)) {
            ctx.json("EventPeriodError");
            return;
        }

        var description = ctx.formParam("description", String.class).getOrNull();
        // Set description to empty string if it is null
        if (description == null) {
            description = "";
        }

        var location = ctx.formParam("location", String.class).getOrNull();
        // Set location to empty string if it is null
        if (location == null) {
            location = "";
        }

        // Create list of attendees by id
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();
        List<Integer> idInviteList = new ArrayList<>();
        idInviteList.add(userId);
        // Get ids from email invite list
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                var statement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
                statement.setString(1, email);
                var result = statement.executeQuery();
                if (result.next()) {
                    idInviteList.add(result.getInt("id"));
                } else {
                    ctx.json("InviteListError");
                    return;
                }
            }
        }

        // Create event and insert into events table
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
        statement.close();

        // Create event to users mapping
        // Get most recent event's id
        var lastIdStatement = connection.createStatement();
        var result = lastIdStatement.executeQuery("SELECT last_insert_rowid() AS eventId FROM events");
        var eventId = result.getInt("eventId");
        idInviteList = this.removeDuplicates(idInviteList);
        insertInviteList(eventId, idInviteList);
        ctx.json("Success");
        ctx.status(201);
    }

    void deleteEvent(Context ctx) throws SQLException {
        var eventId = Integer.parseInt(ctx.pathParam("eventId"));
        var statement = connection.prepareStatement("SELECT hostId FROM events where id = ?");
        statement.setInt(1, eventId);
        var result = statement.executeQuery();
        var hostId = result.getInt("hostId");
        var callerId = Integer.parseInt(ctx.pathParam("userId"));
        // Actually delete event if the caller is host
        if (hostId == callerId) {
            this.deleteByHost(eventId);
        } else { // Simply remove from the associative table
            this.deleteByAttendee(eventId, callerId);
        }
        statement.close();
        ctx.status(200);
    }

    private void deleteByHost(int eventId) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ?");
        statement.setInt(1, eventId);
        statement.executeUpdate();
        statement.close();
    }

    private void deleteByAttendee(int eventId, int callerId) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ? AND userId = ?");
        statement.setInt(1, eventId);
        statement.setInt(2, callerId);
        statement.executeUpdate();
        statement.close();
    }

    void editEvent(Context ctx) throws SQLException {
        var eventId = Integer.parseInt(ctx.pathParam("eventId"));
        var title = ctx.formParam("title", String.class).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        // Ensure that startTime is before endTime
        if (!endTime.isAfter(startTime)) {
            ctx.json("EventPeriodError");
            ctx.status(417);
            return;
        }

        var description = ctx.formParam("description", String.class).getOrNull();
        // Set description to empty string if it is null
        if (description == null) {
            description = "";
        }

        var location = ctx.formParam("location", String.class).getOrNull();
        // Set location to empty string if it is null
        if (location == null) {
            location = "";
        }

        // Make potential updates to the event
        var statement = connection.prepareStatement("UPDATE events SET title = ?, startTime = ?, endTime = ?, description = ?, location = ? WHERE id = ?");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setString(5, location);
        statement.setInt(6, eventId);
        statement.executeUpdate();

        // Create list of attendees by id
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();
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
                    ctx.json("InviteListError");
                    return;
                }
            }
        }

        // Delete associations from table and reinsert the new list
        statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ?");
        statement.setInt(1, eventId);
        statement.executeUpdate();
        idInviteList = this.removeDuplicates(idInviteList);
        insertInviteList(eventId, idInviteList);
        ctx.json("Success");
        ctx.status(201);
    }

    private List<Integer> removeDuplicates(List<Integer> list) {
        List<Integer> resultList = new ArrayList<>();
        for (Integer item : list) {
            if (!resultList.contains(item)) {
                resultList.add(item);
            }
        }
        return resultList;
    }

    private void insertInviteList(int eventId, List inviteList) throws SQLException {
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

    void deletePastEvents(Context ctx) throws SQLException {
        LocalDateTime cur = LocalDateTime.now();
        java.sql.Timestamp sqlCur = java.sql.Timestamp.valueOf(cur);
        var statement = connection.prepareStatement("UPDATE events SET expired = true WHERE endTime < ?");
        statement.setTimestamp(1,sqlCur);
        statement.executeUpdate();
        statement.close();
        ctx.status(200);
    }
}

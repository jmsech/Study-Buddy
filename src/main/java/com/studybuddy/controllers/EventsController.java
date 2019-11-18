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

public class EventsController {
    private Connection connection;

    EventsController(Connection connection) throws SQLException {
        this.connection = connection;
    }

    void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var userId = ctx.pathParam("userId", Integer.class).get();

        var statement = this.connection.prepareStatement("SELECT e.id, e.title, e.startTime, e.endTime, e.description, e.location " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ?");
        statement.setInt(1, userId);
        var result = statement.executeQuery();
        ArrayList<User> stu = new ArrayList<>();
        while (result.next()) {
            events.add(
                    new Event(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime(),
                            result.getString("description"),
                            stu
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

        List inviteList = ctx.formParam("inviteList", List.class).getOrNull();
        var userID = ctx.formParam("userID", Integer.class).get();

        // Create event and insert into events table
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, location, hostId) VALUES (?, ?, ?, ?, ?, ?);");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setString(5, location);
        statement.setInt(6, userID);
        statement.executeUpdate();

        // Create event to users mapping
        // Get most recent event's id
        var result = statement.executeQuery("SELECT last_insert_rowid() AS eventId FROM events");
        var eventId = result.getInt("eventId");
        insertInviteList(ctx, eventId, statement, inviteList);
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

        // Make updates to invite list
        List inviteList = ctx.formParam("inviteList", List.class).getOrNull();

        // Delete associations from table and reinsert the new list
        statement = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE eventId = ?");
        statement.setInt(1, eventId);
        assert inviteList != null;
        insertInviteList(ctx, eventId, statement, inviteList);
    }

    private void insertInviteList(Context ctx, int eventId, PreparedStatement statement, List inviteList) throws SQLException {
        assert inviteList != null;
        for (Object id : inviteList) {
            statement = connection.prepareStatement("INSERT INTO events_to_users_mapping (eventId, userId) VALUES (?, ?)");
            statement.setInt(1, eventId);
            statement.setInt(2, (int) id);
            statement.executeUpdate();
        }
        statement.close();
        ctx.status(201);
    }
}

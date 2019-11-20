package com.studybuddy.controllers;
import com.studybuddy.models.Event;
import com.studybuddy.models.User;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventsController {
    private Connection connection;

    public EventsController(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var userid = ctx.pathParam("userID", Integer.class).get();

        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description FROM events WHERE userID = ? AND expired = false");
        statement.setInt(1, userid);
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

    public void createEvent(Context ctx) throws SQLException {
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

        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", String.class).getOrNull();
        // Set description to empty string if it is null
        if (description == null) {
            description = "";
        }
        var userID = ctx.formParam("userID", Integer.class).get();

        // TODO change call to consider inviteList and importance
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, userID, isGoogleEvent, expired) VALUES (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setInt(5, userID);
        statement.setBoolean(6, false);
        statement.setBoolean(7, false);
        statement.executeUpdate();
        statement.close();
        ctx.status(201);
        ctx.json("Success");
    }

    public void deleteEvent(Context ctx) throws SQLException {
        var id = ctx.pathParam("id");
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ?");
        statement.setInt(1, Integer.parseInt(id));
        statement.executeUpdate();
        statement.close();
        ctx.status(200);
    }

    public void editEvent(Context ctx) throws SQLException {
        var id = ctx.pathParam("id");
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

        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", String.class).getOrNull();
        // Set description to empty string if it is null
        if (description == null) {
            description = "";
        }
        var userID = ctx.formParam("userID", Integer.class).get();

        // TODO change call to consider inviteList and importance
        var statement = connection.prepareStatement("UPDATE events SET title = ?, startTime = ?, endTime = ?, description = ?, userID = ? WHERE id = ?");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setInt(5, userID);
        statement.setInt(6, Integer.parseInt(id));
        statement.executeUpdate();
        statement.close();
        ctx.status(201);
        ctx.json("Success");
    }

    public void deletePastEvents(Context ctx) throws SQLException {
        LocalDateTime cur = LocalDateTime.now();
        java.sql.Timestamp sqlCur = java.sql.Timestamp.valueOf(cur);
        var statement = connection.prepareStatement("UPDATE events SET expired = true WHERE endTime < ?");
        statement.setTimestamp(1,sqlCur);
        statement.executeUpdate();
        statement.close();
        ctx.status(200);
    }
}

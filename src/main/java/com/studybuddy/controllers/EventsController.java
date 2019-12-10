package com.studybuddy.controllers;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.IdRepository;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class EventsController {

    private Connection connection;

    EventsController(Connection connection) {
        this.connection = connection;
    }

    void getEvents(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var events = EventRepository.getEventsForUser(userId, this.connection);
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

        // Set description to empty string if it is null
        var description = ctx.formParam("description", String.class).getOrNull();
        if (description == null) { description = ""; }

        // Set location to empty string if it is null
        var location = ctx.formParam("location", String.class).getOrNull();
        if (location == null) { location = ""; }

        // Create list of attendees by id
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();

        // Get list of userId's attending event
        List<Integer> idInviteList = IdRepository.createIdListFromInviteList(connection, inviteListString, null);
        if (idInviteList == null) { ctx.json("InviteListError"); return; }
        if (!idInviteList.contains(userId)) {idInviteList.add(userId);}

        // Create event and insert into events table
        EventRepository.createEventInDB(connection, idInviteList, title, sqlStartDate, sqlEndDate, description, location, userId);

        ctx.json("Success");
        ctx.status(201);
    }

    void deleteEvent(Context ctx) throws SQLException {
        var eventId = Integer.parseInt(ctx.pathParam("eventId"));
        var callerId = Integer.parseInt(ctx.pathParam("userId"));
        EventRepository.deleteEvent(connection, eventId, callerId);
        ctx.status(200);
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

        // Set description to empty string if it is null
        var description = ctx.formParam("description", String.class).getOrNull();
        if (description == null) { description = ""; }

        // Set location to empty string if it is null
        var location = ctx.formParam("location", String.class).getOrNull();
        if (location == null) { location = ""; }

        // Make potential updates to the event
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();
        int error = EventRepository.updateEventInDB(connection, inviteListString, userId, title, sqlStartDate, sqlEndDate, description, location, eventId);
        if (error == 1) {
            ctx.json("InviteListError");
            return;
        }

        ctx.json("Success");
        ctx.status(201);
    }


    void deletePastEvents(Context ctx) throws SQLException {
        EventRepository.deletePastEvents(connection);
        ctx.status(200);
    }
}

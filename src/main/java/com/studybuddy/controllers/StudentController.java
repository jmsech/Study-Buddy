package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.Student;
import com.studybuddy.models.User;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StudentController {
    private Student student;
    private Connection connection;

    public StudentController(Student student, Connection connection) throws SQLException {
        this.student = student;
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, hosts INTEGER, userID INTEGER)");
        statement.close();
    }

    public void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var userid = ctx.pathParam("userID", Integer.class).get();
        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description FROM events WHERE userID = ?");
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
        var title = ctx.formParam("title", ctx.formParam("title", String.class).get());
        // convert form data of format yyyy-mm-ddT00:00 to LocalDateTime
        // DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        //java.sql.Date sqlStartDate = java.sql.Date.valueOf(startTime.toLocalDate());
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        //java.sql.Date sqlEndDate = java.sql.Date.valueOf(endTime.toLocalDate());
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", ctx.formParam("description", String.class).get());

        // TODO change call to consider inviteList and importance
        // Note (Justin): i changed the invite list parameter to emptyList() instead of null, we were getting a null pointer exception
        // student.createStudyEvent(title, startTime, endTime, location, description, Collections.emptyList(), 1);
        //TODO add actual values to the insert
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, userID) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setInt(5, 1);
        statement.executeUpdate();
        statement.close();
        ctx.status(201);
    }

    public void deleteEvent(Context ctx) throws SQLException {
        var id = ctx.pathParam("id");
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ?");
        statement.setInt(1, Integer.parseInt(id));
        statement.executeUpdate();
        statement.close();
        ctx.status(200);
    }
}

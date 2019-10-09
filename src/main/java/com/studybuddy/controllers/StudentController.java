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
import java.util.Collections;

public class StudentController {
    private Student student;
    private Connection connection;

    public StudentController(Student student, Connection connection) throws SQLException {
        this.student = student;
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT INTEGER, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, hosts INTEGER)");
        statement.close();
    }

    public void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT title, startTime, endTime, description FROM events");
        ArrayList<User> stu = new ArrayList<>();
        while (result.next()) {
            events.add(
                    new Event(
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
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", ctx.formParam("description", String.class).get());
        // TODO change call to consider inviteList and importance
        // Note (Justin): i changed the invite list parameter to emptyList() instead of null, we were getting a null pointer exception
        student.createStudyEvent(title, startTime, endTime, location, description, Collections.emptyList(), 1);
        //TODO add actual values to the insert
        var statement = connection.createStatement();
        statement.execute("INSERT INTO events (title,startTime,endTime,description,hosts) VALUES (title,startTime, endTime, description, 1");
        statement.close();
        ctx.status(201);
    }
}

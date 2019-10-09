package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.Student;
import com.studybuddy.models.User;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentController {
    private Student student;
    private Connection connection;

    public StudentController(Student student, Connection connection) throws SQLException {
        this.student = student;
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT,startTime DATETIME, endTime DATETIME, description TEXT, hosts TEXT)");
        statement.close();
    }

    public void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT id, startTime, endTime, description, hosts FROM events");
        ArrayList<User> stu = new ArrayList<>();
        while (result.next()) {
            events.add(
                    new Event(
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
        var statement = connection.createStatement();
        statement.execute("INSERT INTO events (startTime, endTime, description, hosts) VALUES (CURRENT_TIMESTAMP , CURRENT_TIMESTAMP ,\"\", \"\")");
        statement.close();
        // TODO change call to consider inviteList and importance
        ctx.status(201);
    }
}

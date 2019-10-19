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
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT, firstName TEXT, lastName TEXT)");
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

    public void createUser(Context ctx) throws SQLException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        var firstName = ctx.formParam("firstName");
        var lastName = ctx.formParam("lastName");
        // TODO - Check if user already exists (email)
        var statement = connection.prepareStatement("INSERT INTO users (email, password, firstName, lastName) VALUES (?, ?, ?, ?)");
        statement.setString(1, email);
        statement.setString(2, password);
        statement.setString(3, firstName);
        statement.setString(4, lastName);
        statement.executeUpdate();
        statement.close();
        ctx.status(201);
    }

    public void authenticateUser(Context ctx) throws SQLException {
        var email = ctx.pathParam("email");
        var password = ctx.pathParam("password");
        // Search for the user based on their email
        var sqlString = "SELECT id, password FROM users WHERE email = ?";
        var statement = connection.prepareStatement(sqlString);
        statement.setString(1, email);
        var result = statement.executeQuery();
        boolean userFound = false;
        String storedPassword = "";
        var id = 0;
        while (result.next()) {
            storedPassword = result.getString("password");
            id = result.getInt("id");
            userFound = true;
        }
        // If user is not found or password doesn't match, return 0 (indicates no user)
        if (!userFound || (!password.equals(storedPassword))) {
            ctx.json(0);
        } else {
            // Return the user's id to access their events
            ctx.json(id);
        }
        result.close();
        statement.close();
    }
}

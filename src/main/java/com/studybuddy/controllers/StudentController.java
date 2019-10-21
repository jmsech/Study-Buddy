package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.Student;
import com.studybuddy.models.User;
import io.javalin.http.Context;

// Password security classes
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

// Database handling
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentController {
    private static int hashingIterationCount = 65536;
    private static int hashingKeyLength = 128;
    private Student student;
    private Connection connection;

    public StudentController(Student student, Connection connection) throws SQLException {
        this.student = student;
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
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
        var userID = ctx.formParam("userID", Integer.class).get();

        // TODO change call to consider inviteList and importance
        // Note (Justin): i changed the invite list parameter to emptyList() instead of null, we were getting a null pointer exception
        // student.createStudyEvent(title, startTime, endTime, location, description, Collections.emptyList(), 1);
        //TODO add actual values to the insert
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, userID) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setInt(5, userID);
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

    public void createUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        var firstName = ctx.formParam("firstName");
        var lastName = ctx.formParam("lastName");
        // Check if user already exists (email)
        var checkStatement = connection.prepareStatement("SELECT 1 FROM users WHERE email = ?");
        checkStatement.setString(1, email);
        var result = checkStatement.executeQuery();
        if (result.next()) { // If the query returns something, the user already exists
            ctx.json(false);
        } else { // If user doesn't exist, add to the database
            // Hash password using PBKDF2 before storing
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            assert password != null;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, StudentController.hashingIterationCount, StudentController.hashingKeyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
            String hashedPasswordStr = new String(hashedPassword);
            String hashSaltStr = new String(salt);
            // Insert into database
            var statement = connection.prepareStatement("INSERT INTO users (email, hashedPassword, hashSalt, firstName, lastName) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, email);
            statement.setString(2, hashedPasswordStr);
            statement.setString(3, hashSaltStr);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.executeUpdate();
            statement.close();
            ctx.json(true);
        }
        ctx.status(201);
    }

    public void authenticateUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.pathParam("email");
        var password = ctx.pathParam("password");
        // Search for the user based on their email
        var statement = connection.prepareStatement("SELECT id, hashedPassword, hashSalt FROM users WHERE email = ?");
        statement.setString(1, email);
        var result = statement.executeQuery();
        boolean userFound = false;
        String storedHashedPassword = "";
        String hashedPassword = "";
        var id = 0;
        while (result.next()) {
            storedHashedPassword = result.getString("hashedPassword");
            var salt = result.getString("hashSalt").getBytes();
            // Hash password an compare to stored value
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, StudentController.hashingIterationCount, StudentController.hashingKeyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            hashedPassword = new String(hash);
            id = result.getInt("id");
            userFound = true;
        }
        // If user is not found or password doesn't match, return 0 (indicates no user)
        if (!userFound || (!hashedPassword.equals(storedHashedPassword))) {
            ctx.json(0);
        } else {
            // Return the user's id to access their events
            ctx.json(id);
        }
        result.close();
        statement.close();
    }

    public void getRec(Context ctx) throws SQLException {

        //right now this is hardcoded to just look for common free times between user '1' and '2'; later we'd make it loop over list of users
        //or something like that
        var user1events = new ArrayList<Event>();
        var user2events = new ArrayList<Event>();

        //var user1id = ctx.pathParam("userID", Integer.class).get();
        //again, temporarily hardcoded
        var user1id = 1;
        var user2id = 2;
        //get events with user id 1
        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description FROM events WHERE userID = ?");
        statement.setInt(1, user1id);

        var result1 = statement.executeQuery();
        ArrayList<User> stu1 = new ArrayList<>();
        while (result1.next()) {
            user1events.add(
                    new Event(
                            result1.getInt("id"),
                            result1.getString("title"),
                            result1.getTimestamp("startTime").toLocalDateTime(),
                            result1.getTimestamp("endTime").toLocalDateTime(),
                            result1.getString("description"),
                            stu1
                    )
            );
        }

        //get events with user id 2
        statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description FROM events WHERE userID = ?");
        statement.setInt(1, user2id);

        var result2 = statement.executeQuery();
        ArrayList<User> stu2 = new ArrayList<>();
        while (result1.next()) {
            user2events.add(
                    new Event(
                            result2.getInt("id"),
                            result2.getString("title"),
                            result2.getTimestamp("startTime").toLocalDateTime(),
                            result2.getTimestamp("endTime").toLocalDateTime(),
                            result2.getString("description"),
                            stu2
                    )
            );
        }

        result1.close();
        result2.close();
        statement.close();
        ctx.json(user1events);
        ctx.json(user2events);
    }
}

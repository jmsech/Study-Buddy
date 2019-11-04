package com.studybuddy.controllers;
import io.javalin.http.Context;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;


public class ApplicationController {
    private Connection connection;
    private EventsController eventsController;
    private StudentController studentController;
    private UserController userController;

    public ApplicationController(Connection connection) throws SQLException {
        this.connection = connection;
        this.eventsController = new EventsController(connection);
        this.studentController = new StudentController(connection);
        this.userController = new UserController(connection);

        var statement = this.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, hosts INTEGER, userID INTEGER)");
        statement.close();
    }

    public void getRec(Context ctx) throws SQLException {
        studentController.getRec(ctx);
    }

    public void getEvents(Context ctx) throws SQLException {
        eventsController.getEvents(ctx);
    }

    public void createEvent(Context ctx) throws SQLException {
        eventsController.createEvent(ctx);
    }

    public void deleteEvent(Context ctx) throws SQLException {
        eventsController.deleteEvent(ctx);
    }

    public void createUser(Context ctx) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        userController.createUser(ctx);
    }

    public void authenticateUser(Context ctx) throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
        userController.authenticateUser(ctx);
    }

    public void collectGoogleEvents(Context ctx) throws GeneralSecurityException, IOException, SQLException {
        studentController.collectGoogleEvents(ctx);
    }

}

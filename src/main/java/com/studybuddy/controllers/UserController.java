package com.studybuddy.controllers;

import com.studybuddy.CalendarQuickstart;
import com.studybuddy.models.User;
import com.studybuddy.repositories.AuthenticationRepository;
import com.studybuddy.repositories.UserRepository;
import io.javalin.http.Context;

import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;

class UserController {

    private Connection connection;

    UserController(Connection connection) {
        this.connection = connection;
    }

    void getUser(Context ctx) throws SQLException {
        var id = ctx.pathParam("userId", Integer.class).get();
        User user = UserRepository.getUser(connection, id);
        if (user != null) {
            ctx.json(user);
            ctx.status(200);
        } else {
            // User not found
            ctx.status(404);
        }
    }

    void createUser(Context ctx) throws SQLException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        var firstName = ctx.formParam("firstName");
        var lastName = ctx.formParam("lastName");

        boolean status = UserRepository.createUser(connection, email, password, firstName, lastName);
        ctx.json(status);
        ctx.status(201);
    }

    void authenticateUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");

        int status = AuthenticationRepository.authenticateUser(connection, email, password);
        ctx.json(status);
    }

    void collectGoogleEvents(Context ctx) throws GeneralSecurityException, IOException, SQLException {
        CalendarQuickstart.collectEvents(this.connection, ctx);
    }
}

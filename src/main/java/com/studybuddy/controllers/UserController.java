package com.studybuddy.controllers;

import com.studybuddy.CalendarQuickstart;
import com.studybuddy.models.User;
import com.studybuddy.repositories.AuthenticationRepository;
import com.studybuddy.repositories.IdRepository;
import com.studybuddy.repositories.UserRepository;
import io.javalin.http.Context;

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

    void currentUser(Context ctx) {
        var user = (User) ctx.sessionAttribute("user");
        if (user != null) {
            ctx.json(user);
            ctx.status(200);
        } else {
            ctx.json(0);
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

    void getAllUsers(Context ctx) throws SQLException {
        var users = UserRepository.getAllUsers(connection);
        ctx.json(users);
    }

    void authenticateUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        // Our little easter egg
        assert email != null;
        assert password != null;
        if (email.equals("oose@is.cool") && password.equals("Surprise")) {
            ctx.json(-1);
            return;
        }

        int status = AuthenticationRepository.authenticateUser(connection, email, password);
        if (status != 0) { // If user was found, status represents its id
            var user = UserRepository.getUser(connection, status);
            ctx.sessionAttribute("user", user);
            ctx.status(200);
        }
        ctx.json(status);
    }

    void logOut(Context ctx) {
        ctx.sessionAttribute("user", null);
    }

    void collectGoogleEvents(Context ctx) throws GeneralSecurityException, IOException, SQLException {
        CalendarQuickstart.collectEvents(this.connection, ctx);
    }

    void addFriend(Context ctx) throws SQLException {
        var userId = ctx.formParam("userId", Integer.class).get();
        var buddyId = ctx.formParam("buddyId", String.class).get();
        try {
            String email = buddyId.substring(1, buddyId.length() - 1);
            int id = IdRepository.getIdFromEmail(email, connection);
            UserRepository.addFriend(connection, userId, id);
        } catch (Exception ignored) {
        }
    }

    void removeFriend(Context ctx) throws SQLException {
        var userId = ctx.formParam("userId", Integer.class).get();
        var buddyId = ctx.formParam("buddyId", String.class).get();
        try {
            String email = buddyId.substring(1, buddyId.length() - 1);
            int id = IdRepository.getIdFromEmail(email, connection);
            UserRepository.removeFriend(connection, userId, id);
        } catch (Exception ignored) {
        }
    }

    void getPendingFromUserId(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var users = UserRepository.getPendingFromUserId(connection, userId);
        ctx.json(users);
    }

    void getAwaitingFromUserId(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var users = UserRepository.getAwaitingFromUserId(connection, userId);
        ctx.json(users);
    }

    void getFriendsFromUserId(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var users = UserRepository.getFriendsFromUserId(connection, userId);
        ctx.json(users);
    }
}

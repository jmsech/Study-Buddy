package com.studybuddy;
import com.studybuddy.controllers.ApplicationController;
import io.javalin.Javalin;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");

        var ApplicationController = new ApplicationController(connection);
        Javalin.create(config -> { config.addStaticFiles("/public"); })
                .events(event -> {
                    event.serverStopped(() -> { connection.close(); });
                })
                .routes(() -> {
                    // TODO - Postman documentation and tests for everything
                    path(":userId/recs", () -> {
                        post(ApplicationController::getRec);
                    });
                    path(":userID/events", () -> {
                        get(ApplicationController::getEvents);
                        post(ApplicationController::createEvent);
                        path(":id", () -> {
                            delete(ApplicationController::deleteEvent);
                        });
                    });
                    path(":userID", () -> {
                        post(ApplicationController::collectGoogleEvents);
                    });
                    path("users", () -> {
                        post(ApplicationController::createUser);
                        path("authenticate/", () -> {
                            post(ApplicationController::authenticateUser);
                        });
                    });
                })
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}

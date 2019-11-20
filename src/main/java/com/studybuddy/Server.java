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
                    path("users", () -> {
                        post(ApplicationController::createUser);
                        path("authenticate/", () -> {
                            post(ApplicationController::authenticateUser);
                        });
                    });
                    path(":userID", () -> {
                        post(ApplicationController::collectGoogleEvents);
                        delete(ApplicationController::logOut);
                        path("recs", () -> {
                            post(ApplicationController::getRec);
                        });
                        path("events", () -> {
                            get(ApplicationController::getEvents);
                            post(ApplicationController::createEvent);
                            put(ApplicationController::deletePastEvents);
                            path(":id", () -> {
                                delete(ApplicationController::deleteEvent);
                                put(ApplicationController::editEvent);
                            });
                        });
                    });
                })
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}

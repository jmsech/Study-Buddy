package com.studybuddy;
import com.studybuddy.controllers.ApplicationController;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    public static void main(String[] args) throws SQLException, IOException {
        var connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");

        var ApplicationController = new ApplicationController(connection);
        Javalin.create(config -> { config.addStaticFiles("/public"); })
                .events(event -> {
                    event.serverStopped(connection::close);
                })
                .routes(() -> {
                    path("users", () -> {
                        post(ApplicationController::createUser);
                        get(ApplicationController::getAllUsers);
                        path("authenticate/", () -> {
                            post(ApplicationController::authenticateUser);
                            delete(ApplicationController::logOut);
                        });
                        path("current/", () -> {
                           get(ApplicationController::currentUser);
                        });
                        path(":userId", () -> {
                           get(ApplicationController::getUser);
                        });
                    });
                    path("courses", () -> {
                       get(ApplicationController::getAllCourses); 
                    });
                    path(":userId", () -> { 
                        post(ApplicationController::collectGoogleEvents);
                        path("recs", () -> {
                            post(ApplicationController::getRec);
                        });
                        path("courseLinkRecs", () -> {
                            post(ApplicationController::getCourseLinkRec);
                        });
                        path("events", () -> {
                            get(ApplicationController::getEvents);
                            post(ApplicationController::createEvent);
                            put(ApplicationController::deletePastEvents);
                            path(":eventId", () -> {
                                delete(ApplicationController::deleteEvent);
                                put(ApplicationController::editEvent);
                            });
                        });
                        path("courses", () -> {
                            get(ApplicationController::getCourses);
                            post(ApplicationController::addCourse);
                            put(ApplicationController::archiveOldCourses);
                            delete(ApplicationController::removeCourse);
                            path(":courseId", () -> {
                                put(ApplicationController::updateCourseStatus);
                            });
                        });
                        path("addCourse", () -> {
                            post(ApplicationController::addCourseToUser);
                        });
                        path("deadline", () -> {
                            post(ApplicationController::addDeadlineToCourse);
                            delete(ApplicationController::removeDeadlineFromCourse);
                        });
                        path("friends", () -> {
                            get(ApplicationController::getFriendsFromUserId);
                            post(ApplicationController::addFriend);
                            put(ApplicationController::removeFriend);
                        });
                        path("followers", () -> {
                            get(ApplicationController::getPendingFromUserId);
                            post(ApplicationController::getAwaitingFromUserId);
                        });
                    });
                })
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}

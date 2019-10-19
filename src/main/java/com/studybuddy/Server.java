package com.studybuddy;

import com.studybuddy.controllers.StudentController;
import com.studybuddy.models.Student;
import io.javalin.Javalin;

import java.sql.DriverManager;
import java.sql.SQLException;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");
//        var userCon = DriverManager.getConnection("jdbc:sqlite:Users.db");
        int id = 0;
        String studentId = "ABC123";
        String studentName = "Leandro Facchinetti";
        var student = new Student(id, studentName, studentId, null);
        var StudentController = new StudentController(student, connection);
        Javalin.create(config -> { config.addStaticFiles("/public"); })
                .events(event -> {
                    event.serverStopped(() -> { connection.close(); });
                })
                .routes(() -> {
                    path(":userID/events", () -> {
                        get(StudentController::getEvents);
                        post(StudentController::createEvent);
                        path(":id", () -> {
                            delete(StudentController::deleteEvent);
                        });
                    });
                    path("users", () -> {
                        post(StudentController::createUser);
                        path("authenticate", () -> {
                            get(StudentController::authenticateUser);
                        });
                    });
                })
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}

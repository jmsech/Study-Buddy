package com.studybuddy;

import com.studybuddy.controllers.StudentController;
import com.studybuddy.models.Student;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    public static void main(String[] args) {
        int id = 0;
        String studentId = "ABC123";
        String studentName = "Leandro Facchinetti";
        var student = new Student(id, studentName, studentId, null);
        var StudentController = new StudentController(student);
        Javalin.create(/* ~~TODO put this back when we have frontend~~ config -> { config.addStaticFiles("/public"); }*/)
                .routes(() -> {
                    path(":id/events", () -> {
                        get(StudentController::getEvents);
                        post(StudentController::createEvent);
                    });
                })
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}

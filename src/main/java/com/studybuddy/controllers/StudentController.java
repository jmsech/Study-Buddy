package com.studybuddy.controllers;

import com.studybuddy.models.Student;
import io.javalin.http.Context;

import java.time.LocalDateTime;

public class StudentController {
    private Student student;

    public StudentController(Student student) {
        this.student = student;
    }

    public void getEvents(Context ctx) {
        ctx.json(student.getEvents());
    }

    public void createEvent(Context ctx) {
        var title = ctx.formParam("title", "");
        LocalDateTime startTime = ctx.formParam("startTime", LocalDateTime.class).get();
        LocalDateTime endTime = ctx.formParam("endTime", LocalDateTime.class).get();
        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", "");
        // TODO change call to consider inviteList and importance
        student.createStudyEvent(title, startTime, endTime, location, description, null, 1);
        ctx.status(201);
    }
}

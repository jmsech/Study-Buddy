package com.studybuddy.controllers;

import com.studybuddy.models.Student;
import io.javalin.http.Context;

import java.time.LocalDate;
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
        var description = ctx.formParam("description", "");
        LocalDateTime startTime = ctx.formParam("startTime", LocalDateTime.class).get();
        LocalDateTime endTime = ctx.formParam("endTime", LocalDateTime.class).get();
        student.createStudyEvent(description, startTime, endTime, null);
        ctx.status(201);
    }
}

package com.studybuddy.controllers;

import com.studybuddy.repositories.CourseRepository;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CourseController {

    private Connection connection;

    CourseController(Connection connection) {
        this.connection = connection;
    }

    public void getCourses(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var courses = CourseRepository.getCoursesForUser(this.connection, userId);
        ctx.json(courses);
    }

    public void addCourse(Context ctx) throws SQLException {
        // get courseID
        var courseId = ctx.formParam("courseId", String.class).get();

        // Get userID
        var userId = ctx.formParam("userId", Integer.class).get();

        // Create event and insert into events table
        int status = CourseRepository.addCourseToUser(connection, courseId, userId);

        // TODO:
        //  1) What should we do if status is 1 or 2?

        ctx.json("Success");
        ctx.status(201);
    }

    public void archiveOldCourses(Context ctx) throws SQLException {
        CourseRepository.archiveOldCourses(connection);
        ctx.status(200);
    }

    public void removeCourse(Context ctx) throws SQLException {
        String courseId = ctx.pathParam("courseId");
        var userId = Integer.parseInt(ctx.pathParam("userId"));
        CourseRepository.removeCourse(connection, userId, courseId);
        ctx.status(200);
    }

    public void addCourseToUser(Context ctx) throws SQLException {
        // String courseId = ctx.pathParam("courseId");
        System.out.println("Hello");
        var courseId = ctx.formParam("courseId", String.class).get();
        var userId = Integer.parseInt(ctx.pathParam("userId"));
        CourseRepository.addCourseToUser(connection, courseId, userId);
        ctx.status(200);
    }

    public void addDeadlineToCourse(Context ctx) throws SQLException {
        var title = ctx.formParam("title", String.class).get();
        var courseId = ctx.formParam("courseID", String.class).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime date = LocalDateTime.parse(ctx.formParam("dueDate", String.class).get(), formatter);
        java.sql.Timestamp time = java.sql.Timestamp.valueOf(date);

        var description = ctx.formParam("description", String.class).get();
        CourseRepository.addDeadlineToCourse(connection, courseId, title, description, time);
    }

    public void removeDeadlineFromCourse(Context ctx) throws SQLException {
        var courseId = ctx.formParam("courseID", String.class).get();
        var eventId = ctx.formParam("eventID", Integer.class).get();
        CourseRepository.removeDeadlineFromCourse(connection, eventId, courseId);
    }

    public void updateCourseStatus(Context ctx) {
        // TODO:
        //  Should a student be able to update a course? Maybe for a discussion board or something, idk.
        //  We can leave this alone for now.
    }
}

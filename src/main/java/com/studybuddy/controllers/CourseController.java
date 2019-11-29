package com.studybuddy.controllers;

import com.studybuddy.repositories.CourseRepository;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.IdRepository;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CourseController {

    private Connection connection;

    CourseController(Connection connection) {
        this.connection = connection;
    }

    public void getCourses(Context ctx) throws SQLException {
        var userId = ctx.pathParam("userId", Integer.class).get();
        var courses = CourseRepository.getCoursesForUser(userId, this.connection);
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

    public void updateCourseStatus(Context ctx) {
        // TODO:
        //  Should a student be able to update a course? Maybe for a discussion board or something, idk.
        //  We can leave this alone for now.
    }
}

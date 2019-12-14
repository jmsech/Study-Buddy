package com.studybuddy.controllers;
import com.studybuddy.repositories.InitializationRepository;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;


public class ApplicationController {
    private EventsController eventsController;
    private UserController userController;
    private RecsController recsController;
    private CourseController courseController;
    private CourseLinkRecsController courseLinkRecsController;

    public ApplicationController(Connection connection) throws SQLException, IOException {
        this.eventsController = new EventsController(connection);
        this.userController = new UserController(connection);
        this.recsController = new RecsController(connection);
        this.courseController = new CourseController(connection);
        this.courseLinkRecsController = new CourseLinkRecsController(connection);

        InitializationRepository.initializeTables(connection);
    }

    public void getRec(Context ctx) throws SQLException {
        recsController.getRec(ctx);
    }

    public void getCourseLinkRec(Context ctx) throws SQLException {
        courseLinkRecsController.getRec(ctx);
    }

    public void getCourses(Context ctx) throws SQLException {
        courseController.getCourses(ctx);
    }

    public void getAllCourses(Context ctx) throws SQLException {
        courseController.getAllCourses(ctx);
    }

    public void addCourse(Context ctx) throws SQLException {
        courseController.addCourse(ctx);
    }

    public void archiveOldCourses(Context ctx) throws SQLException {
        courseController.archiveOldCourses(ctx);
    }

    public void removeCourse(Context ctx) throws SQLException {
        courseController.removeCourseFromUser(ctx);
    }

    public void updateCourseStatus(Context ctx) throws SQLException {
        courseController.updateCourseStatus(ctx);
    }

    public void addDeadlineToCourse(Context ctx) throws SQLException {
        courseController.addDeadlineToCourse(ctx);
    }

    public void removeDeadlineFromCourse(Context ctx) throws SQLException {
        courseController.removeDeadlineFromCourse(ctx);
    }

    public void getEvents(Context ctx) throws SQLException {
        eventsController.getEvents(ctx);
    }

    public void createEvent(Context ctx) throws SQLException {
        eventsController.createEvent(ctx);
    }

    public void deleteEvent(Context ctx) throws SQLException {
        eventsController.deleteEvent(ctx);
    }

    public void deletePastEvents(Context ctx) throws SQLException {
        eventsController.deletePastEvents(ctx);
    }

    public void editEvent(Context ctx) throws SQLException {
        eventsController.editEvent(ctx);
    }

    public void getUser(Context ctx) throws SQLException {
        userController.getUser(ctx);
    }

    public void createUser(Context ctx) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        userController.createUser(ctx);
    }

    public void getAllUsers(Context ctx) throws SQLException {
        userController.getAllUsers(ctx);
    }

    public void authenticateUser(Context ctx) throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
        userController.authenticateUser(ctx);
    }

    public void collectGoogleEvents(Context ctx) throws GeneralSecurityException, IOException, SQLException {
        userController.collectGoogleEvents(ctx);
    }

    public void addCourseToUser(Context ctx) throws SQLException {
        courseController.addCourseToUser(ctx);
    }

    public void logOut(Context ctx) throws IOException {
        Files.deleteIfExists(Paths.get("tokens/StoredCredential"));
    }
}

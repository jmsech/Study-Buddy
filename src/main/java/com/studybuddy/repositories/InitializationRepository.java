package com.studybuddy.repositories;

import java.sql.Connection;
import java.sql.SQLException;

public class InitializationRepository {

    public static void initializeTables(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, location TEXT, hostId INTEGER, isGoogleEvent BOOLEAN, expired BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS events_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, eventId INTEGER, userId INTEGER, FOREIGN KEY (eventId) REFERENCES events (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.execute("CREATE TABLE IF NOT EXISTS courses (id INTEGER PRIMARY KEY AUTOINCREMENT, courseNum TEXT, courseSectionNum TEXT, courseName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS courses_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, courseId INTEGER, userId INTEGER, FOREIGN KEY (courseId) REFERENCES courses (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.close();
    }
}

package com.studybuddy.models;

import com.studybuddy.repositories.AuthenticationRepository;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationRepositoryTest {

    static java.sql.Connection connection;
    static String pass = "ThisPassWord!";
    static String email = "TestAuthentication@studybuddy.com";

    @BeforeAll
    public static void createConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");
        UserRepository.createUser(connection, email, pass, "study", "buddy");
    }

    @Test
    void testAuthentication() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = 'TestAuthentication@studybuddy.com'");
        var result = statement.executeQuery();
        int userId = result.getInt("id");
        int checkId = AuthenticationRepository.authenticateUser(connection, email, pass);
        statement.close();
        assertEquals(userId, checkId);
    }

    @Test
    void testBadAuthentication() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        int badPass = AuthenticationRepository.authenticateUser(connection, email, "wrongPass!");
        int badEmail = AuthenticationRepository.authenticateUser(connection, "fakeEmail@studybuddy,com", pass);

        assertEquals(0, badPass);
        assertEquals(0, badEmail);
    }

    @AfterAll
    public static void cleanDB() throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM users WHERE email = 'TestAuthentication@studybuddy.com'");
        statement.executeUpdate();
        statement.close();
    }
}

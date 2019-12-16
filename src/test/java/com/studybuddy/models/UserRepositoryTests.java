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

public class UserRepositoryTests {

    static java.sql.Connection connection;

    @BeforeAll
    public static void createConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");
        String email = "DuplicateUser@StudyBuddy.com";
        String firstName = "Duplicate";
        String lastName = "User";
        String password = "TestPassword!";
        UserRepository.createUser(connection, email, password, firstName, lastName);
    }

    @Test
    void testCreateUser() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String email = "TestUser@StudyBuddy.com";
        String firstName = "Test";
        String lastName = "User";
        String password = "TestPassword!";
        UserRepository.createUser(connection, email, password, firstName, lastName);

        var statement = connection.prepareStatement("SELECT id, firstName, lastName FROM users WHERE email = 'TestUser@StudyBuddy.com'");
        var result = statement.executeQuery();
        var userId = result.getInt("id");
        var checkFirst = result.getString("firstName");
        var checkLast = result.getString("lastName");
        statement.close();
        var authUserId = AuthenticationRepository.authenticateUser(connection, email, password);

        assertEquals(authUserId, userId);
        assertEquals(firstName, checkFirst);
        assertEquals(lastName, checkLast);
    }

    @Test
    void testDuplicateUser() throws SQLException {
        String email = "DuplicateUser@StudyBuddy.com";
        String firstName = "Duplicate";
        String lastName = "User";
        String password = "TestPassword!";
        boolean check = UserRepository.createUser(connection, email, password, firstName, lastName);

        assertFalse(check);
    }

    @AfterAll
    public static void cleanDatabase() throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM users WHERE email = 'TestUser@StudyBuddy.com' OR email = 'DuplicateUser@StudyBuddy.com'");
        statement.executeUpdate();
        statement.close();
    }

}

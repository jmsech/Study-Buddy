package com.studybuddy.models;

import com.google.api.services.calendar.model.Events;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    void testGetters() {
        long id = 2118232;
        String name = "Fake Name";
        String email = "FakeEmail@studybuddy.com";

        User fakeUser = new User(id, name, email);

        assertEquals(id, fakeUser.getId());
        assertEquals(name, fakeUser.getName());
        assertEquals(email, fakeUser.getEmail());
    }

    @Test
    void testSetters() {
        long id = 2118232;
        String name = "Fake Name";
        String email = "FakeEmail@studybuddy.com";

        User fakeUser = new User(id, name, email);

        long newId = 21182322;
        String newName = "New Name";
        String newEmail = "NewFakeEmail@studybuddy.com";

        fakeUser.setId(newId);
        fakeUser.setName(newName);
        fakeUser.setEmail(newEmail);

        assertEquals(newId, fakeUser.getId());
        assertEquals(newName, fakeUser.getName());
        assertEquals(newEmail, fakeUser.getEmail());
    }

}

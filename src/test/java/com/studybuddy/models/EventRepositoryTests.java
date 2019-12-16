package com.studybuddy.models;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventRepositoryTests {

    static int testId;
    static int testIdDel;
    static int testIdExp;
    static int testIdUp;
    static int testIdAtt;
    static java.sql.Connection connection;
    LocalDateTime jan_1_2020_4_00_PM = LocalDateTime.of(2020,1,1,16,0);
    LocalDateTime jan_1_2020_5_00_PM = LocalDateTime.of(2020,1,1,17,0);
    LocalDateTime jan_1_2020_7_00_PM = LocalDateTime.of(2020,1,1,19,0);
    LocalDateTime jan_1_2020_8_00_PM = LocalDateTime.of(2020,1,1,20,0);
    LocalDateTime jan_1_2019_8_00_PM = LocalDateTime.of(2019,1,1,20,0);
    LocalDateTime jan_1_2019_9_00_PM = LocalDateTime.of(2019,1,1,21,0);

    @BeforeAll
    public static void createConnection() throws SQLException {
       connection = DriverManager.getConnection("jdbc:sqlite:studyBuddy.db");
    }

    @Test
    void testCreate() throws SQLException {
        UserRepository.createUser(connection, "testCreate@studybuddy.com", "StudyBuddy!", "Study", "Buddy");
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = 'testCreate@studybuddy.com'");
        var result = statement.executeQuery();
        testId = result.getInt("id");
        List<Integer> idInviteList = new ArrayList<Integer>();
        idInviteList.add(testId);
        //idInviteList.add(1000);
        //idInviteList.add(1001);
        String title = "Test Event";
        LocalDateTime startTime = jan_1_2020_4_00_PM;
        LocalDateTime endTime = jan_1_2020_5_00_PM;
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        String description = "This is a test event.";
        String location = "Gilman 50";
        EventRepository.createEventInDB(connection, idInviteList, title, sqlStartDate, sqlEndDate, description, location, testId, false);
        List<Event> checkEvents = EventRepository.getEventsForUser(testId, connection);
        Event checkEvent = checkEvents.get(0);

        assertEquals(testId, checkEvent.getAttendees().get(0).getId());
        assertEquals(title, checkEvent.getTitle());
        assertEquals(startTime, checkEvent.getStartTime());
        assertEquals(endTime, checkEvent.getEndTime());
        assertEquals(description, checkEvent.getDescription());
        assertEquals(location, checkEvent.getLocation());
    }

    @Test
    void testDeleteEventbyHost() throws SQLException {
        // create user
        UserRepository.createUser(connection, "testDelete@studybuddy.com", "StudyBuddy!", "Study", "Buddy");
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = 'testDelete@studybuddy.com'");
        var result = statement.executeQuery();
        testIdDel = result.getInt("id");
        statement.close();

        // Create Event to Delete by Host
        List<Integer> idInviteList = new ArrayList<Integer>();
        idInviteList.add(testIdDel);
        String title = "Delete Event";
        LocalDateTime startTime = jan_1_2020_4_00_PM;
        LocalDateTime endTime = jan_1_2020_5_00_PM;
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        String description = "Delete This Event.";
        String location = "Gilman 50";
        int eventId = EventRepository.createEventInDB(connection, idInviteList, title, sqlStartDate, sqlEndDate, description, location, testIdDel, false);
        EventRepository.deleteEvent(connection, eventId, testIdDel);
        List<Event> checkEvents = EventRepository.getEventsForUser(testIdDel, connection);
        assertTrue(checkEvents.isEmpty());

        // Create Event to Delete by Guest
        List<Integer> idInviteList2 = new ArrayList<Integer>();
        idInviteList2.add(testIdDel);
        idInviteList2.add(1000);
        String title2 = "Delete Event by Guest";
        LocalDateTime startTime2 = jan_1_2020_7_00_PM;
        LocalDateTime endTime2 = jan_1_2020_8_00_PM;
        java.sql.Timestamp sqlStartDate2 = java.sql.Timestamp.valueOf(startTime2);
        java.sql.Timestamp sqlEndDate2 = java.sql.Timestamp.valueOf(endTime2);
        String description2 = "Delete This Event.";
        String location2 = "Gilman 50";
        eventId = EventRepository.createEventInDB(connection, idInviteList2, title2, sqlStartDate2, sqlEndDate2, description2, location2, testIdDel, false);
        EventRepository.deleteEvent(connection, eventId, 1000);
        List<Event> checkEvents2 = EventRepository.getEventsForUser(testIdDel, connection);
        assertFalse(checkEvents2.isEmpty());
    }

    @Test
    void testUpdateEvent() throws SQLException {
        UserRepository.createUser(connection, "testUpdate@studybuddy.com", "StudyBuddy!", "Study", "Buddy");
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = 'testUpdate@studybuddy.com'");
        var result = statement.executeQuery();
        testIdUp = result.getInt("id");
        statement.close();

        List<Integer> idInviteList = new ArrayList<Integer>();
        idInviteList.add(testIdUp);
        String title = "Update Event";
        LocalDateTime startTime = jan_1_2020_7_00_PM;
        LocalDateTime endTime = jan_1_2020_8_00_PM;
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        String description = "This is a test event.";
        String location = "Gilman 50";
        int eventId = EventRepository.createEventInDB(connection, idInviteList, title, sqlStartDate, sqlEndDate, description, location, testIdUp, false);

        String inviteListString = "";
        title = "Event is now Updated.";
        startTime = jan_1_2020_4_00_PM;
        endTime = jan_1_2020_5_00_PM;
        sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        description = "Updated Description";
        location = "Shaffer 3";
        EventRepository.updateEventInDB(connection, inviteListString, testIdUp, title, sqlStartDate, sqlEndDate, description, location, eventId);

        List<Event> checkEvents = EventRepository.getEventsForUser(testIdUp, connection);
        Event checkEvent = checkEvents.get(0);

        assertEquals(testIdUp, checkEvent.getAttendees().get(0).getId());
        assertEquals(title, checkEvent.getTitle());
        assertEquals(startTime, checkEvent.getStartTime());
        assertEquals(endTime, checkEvent.getEndTime());
        assertEquals(description, checkEvent.getDescription());
        assertEquals(location, checkEvent.getLocation());
    }

    @Test
    void testDeletePastEvent() throws SQLException {

        UserRepository.createUser(connection, "testExpire@studybuddy.com", "StudyBuddy!", "Study", "Buddy");
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = 'testExpire@studybuddy.com'");
        var result = statement.executeQuery();
        testIdExp = result.getInt("id");
        statement.close();

        List<Integer> idInviteList = new ArrayList<Integer>();
        idInviteList.add(testIdExp);
        String title = "Expired Event";
        LocalDateTime startTime = jan_1_2019_8_00_PM;
        LocalDateTime endTime = jan_1_2019_9_00_PM;
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        String description = "This is a test event.";
        String location = "Gilman 50";
        EventRepository.createEventInDB(connection, idInviteList, title, sqlStartDate, sqlEndDate, description, location, testIdExp, false);
        EventRepository.deletePastEvents(connection);

        var statement2 = connection.prepareStatement("SELECT expired FROM events WHERE hostId = ?");
        statement2.setInt(1, testIdExp);
        var result2 = statement2.executeQuery();
        boolean checkExpire = result2.getBoolean("expired");
        statement2.close();

        assertTrue(checkExpire);
    }

    @AfterAll
    public static void cleanDatabase() throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM events WHERE hostId = ? OR hostId = ? OR hostID = ? OR hostId = ?");
        statement.setInt(1, testId);
        statement.setInt(2, testIdExp);
        statement.setInt(3, testIdUp);
        statement.setInt(4, testIdDel);
        statement.executeUpdate();
        statement.close();
        var statement1 = connection.prepareStatement("DELETE FROM events_to_users_mapping WHERE userId = ? OR userId = ? OR userId = ? OR userId = ?");
        statement1.setInt(1, testId);
        statement1.setInt(2, testIdExp);
        statement1.setInt(3, testIdUp);
        statement1.setInt(4, testIdDel);
        statement1.executeUpdate();
        statement1.close();
        var statement2 = connection.prepareStatement("DELETE FROM users WHERE id = ? OR id = ? OR id = ? OR id = ?");
        statement2.setInt(1, testId);
        statement2.setInt(2, testIdExp);
        statement2.setInt(3, testIdUp);
        statement2.setInt(4, testIdDel);
        statement2.executeUpdate();
        statement2.close();
    }
}


package com.studybuddy.models;

import org.junit.Before;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EventTests {

    @Before
    void init() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        User host1 = new User(1, "John Smith");
        List<User> testHosts = new ArrayList<User>();
        testHosts.add(host1);

        LocalDateTime newStartTime = startTime.plusHours(4);
        LocalDateTime newEndTime = endTime.plusHours(4);
        User host2 = new User(2, "Joe Johnson");
    }


    @Test
    void testGetters() {
        int id = 1;
        String eventTitle = "Calc Study Session";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        String eventDescritpion = "This is a description";
        User host1 = new User(1, "John Smith");
        List<User> testHosts = new ArrayList<User>();
        testHosts.add(host1);

        var event = new Event(id, eventTitle, startTime, endTime, eventDescritpion, testHosts);
        assertEquals(id, event.getId());
        assertEquals(eventTitle, event.getTitle());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals(eventDescritpion, event.getDescription());
        assertEquals(testHosts, event.getHosts());
    }

    @Test
    void testSetters() {
        int id = 1;
        String eventTitle = "Calc Study Session";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        String eventDescritpion = "This is a description";
        User host1 = new User(1, "John Smith");
        List<User> testHosts = new ArrayList<User>();
        testHosts.add(host1);

        LocalDateTime newStartTime = startTime.plusHours(4);
        LocalDateTime newEndTime = endTime.plusHours(4);
        User host2 = new User(2, "Joe Johnson");


        var event = new Event(id, eventTitle, startTime, endTime, eventDescritpion, testHosts);

        event.setStartTime(newStartTime);
        event.setEndTime(newEndTime);
        event.setDescription("Calc Review Session");
        testHosts.add(host2);
        event.setHosts(testHosts);

        assertEquals(newStartTime, event.getStartTime());
        assertEquals(newEndTime, event.getEndTime());
        assertEquals("Calc Review Session", event.getDescription());
        assertEquals(testHosts, event.getHosts());
    }
}

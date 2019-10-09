package com.studybuddy.models;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
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
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        User host1 = new User(1, "John Smith");
        List<User> testHosts = new ArrayList<User>();
        testHosts.add(host1);

        var event = new Event(startTime, endTime, "Calc Study Session", testHosts);
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals("Calc Study Session", event.getDescription());
        assertEquals(testHosts, event.getHosts());
    }

    @Test
    void testSetters() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        User host1 = new User(1, "John Smith");
        List<User> testHosts = new ArrayList<User>();
        testHosts.add(host1);

        LocalDateTime newStartTime = startTime.plusHours(4);
        LocalDateTime newEndTime = endTime.plusHours(4);
        User host2 = new User(2, "Joe Johnson");


        var event = new Event(startTime, endTime, "Calc Study Session", testHosts);

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

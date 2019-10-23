package com.studybuddy.models;

import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeChunkTests {


    @Test
    void testGetters() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(3);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        //test getters
        assertEquals(startTime1, tc1.getStartTime());
        assertEquals(endTime1, tc1.getEndTime());

    }

    @Test
    void testSetters() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(3);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        //test setters
        LocalDateTime newStart = startTime1.plusHours(2);
        tc1.setStartTime(newStart);
        assertEquals(newStart, tc1.getStartTime());

        LocalDateTime newEnd = endTime1.plusHours(1);
        tc1.setEndTime(newEnd);
        assertEquals(newEnd, tc1.getEndTime());

    }

    @Test
    void testOverlapping() {

        //these two overlap for the first hour, same start time
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(3);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        LocalDateTime endTime2 = startTime1.plusHours(1);
        TimeChunk tc2 = new TimeChunk(startTime1, endTime2);

        assertTrue(tc2.isOverlapping(tc1));

        //these two overlap in the middle
        LocalDateTime startTime3 = startTime1.plusHours(1);
        LocalDateTime endTime3 = startTime1.plusHours(2);
        TimeChunk tc3 = new TimeChunk(startTime3, endTime3);

        assertTrue(tc3.isOverlapping(tc1));

        //these two overlap for the last hour, same end time
        LocalDateTime startTime4 = startTime1.plusHours(2);
        TimeChunk tc4 = new TimeChunk(startTime4, endTime1);
        assertTrue(tc4.isOverlapping(tc1));
    }

    @Test
    void testNotOverlapping() {
        //don't overlap at all
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(2);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        LocalDateTime startTime2 = startTime1.plusHours(3);
        LocalDateTime endTime2 = startTime1.plusHours(4);
        TimeChunk tc2 = new TimeChunk(startTime2, endTime2);

        assertTrue(!tc2.isOverlapping(tc1));
    }

    @Test
    void mergeTestNoOverlaps() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(2);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        LocalDateTime startTime2 = startTime1.plusHours(3);
        LocalDateTime endTime2 = startTime1.plusHours(4);
        TimeChunk tc2 = new TimeChunk(startTime2, endTime2);

        tc1.merge(tc2);

        assertEquals(startTime1, tc1.getStartTime());
        assertEquals(endTime2, tc1.getEndTime());
    }

    @Test
    void mergeTestWithOverlaps() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime endTime1 = startTime1.plusHours(1);
        TimeChunk tc1 = new TimeChunk(startTime1, endTime1);

        LocalDateTime endTime2 = startTime1.plusHours(3);
        TimeChunk tc2 = new TimeChunk(startTime1, endTime2);

        tc1.merge(tc2);

        assertEquals(startTime1, tc1.getStartTime());
        assertEquals(endTime2, tc1.getEndTime());

        LocalDateTime startTime3 = startTime1.plusHours(1);
        TimeChunk tc3 = new TimeChunk(startTime3, endTime2);

        tc1.merge(tc3);

        assertEquals(startTime1, tc1.getStartTime());
        assertEquals(endTime2, tc1.getEndTime());
    }



}

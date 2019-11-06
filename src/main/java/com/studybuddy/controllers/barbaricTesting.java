package com.studybuddy.controllers;

import com.studybuddy.models.TimeChunk;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class barbaricTesting {

    private int COMPRESSION = 60; //seconds/60 = minutes

    public static void main(String[] args) {
        ArrayList<TimeChunk> list = new ArrayList<>();

        list.add(new TimeChunk(
                LocalDateTime.of(2020,1,1,0,0),
                LocalDateTime.of(2020,1,1,12,0))); // 01/01/2020 12am-12pm
        list.add(new TimeChunk(
                LocalDateTime.of(2020,1,1,17,0),
                LocalDateTime.of(2020,1,1,20,0))); // 01/01/2020 5pm-8pm
        list.add(new TimeChunk(
                LocalDateTime.of(2020,1,2,14,0),
                LocalDateTime.of(2020,1,2,20,0))); // 01/02/2020 2pm-8pm
        list.add(new TimeChunk(
                LocalDateTime.of(2020,1,2,18,0),
                LocalDateTime.of(2020,1,2,22,0))); // 01/02/2020 6pm-10pm
        list.add(new TimeChunk(
                LocalDateTime.of(2020,1,7,18,0),
                LocalDateTime.of(2020,1,8,2,0))); // 01/07/2020 6pm - 01/08/2020 2am
        LocalDateTime start = LocalDateTime.of(2020,1,1,0,0); // 01/01/2020 12am
        LocalDateTime end = LocalDateTime.of(2020,1,7,23,59); // 01/07/2020 11:59pm
        ArrayList<TimeChunk> chunks = makeRecommendation(start, end, list, 3);
//        for (var chunk : chunks) {
//            System.out.println(chunk.getStartTime() + " to " + chunk.getEndTime());
//        }
        System.out.println(chunks.size());
        System.out.println("Hello, World!");
    }

    private static LocalDateTime makeTime(long t) {
        return LocalDateTime.ofEpochSecond(t,0,ZoneOffset.ofHours(0));
    }

    private static ArrayList<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, ArrayList<TimeChunk> unavailable, double fraction) {

        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/60;
        int[] timeArray = new int[lengthInMinutes];

        for (TimeChunk t : unavailable) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/60;
            int f = (int) (trueF - startSec)/60;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes - 1;}
            if (f >= lengthInMinutes) {f = lengthInMinutes - 1;}

            for (int i = s; i <= f; i++) { timeArray[i]++; }
        }

        long SECONDS_PER_DAY = 86400;
        long MINUTES_PER_DAY = 1440;
        long MINUTES_OF_SLEEP = 480;
        long sleepStart = (LocalDateTime.of(2020,1,1,0,0)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / 60;

        int len = timeArray.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                timeArray[i] = -1;
            }
        }

        int count = 0;
        for (var i : timeArray) {
            if (i == -1) {
                System.out.print("z ");
            } else {
                System.out.print(i + " ");
            }
            count++;
            if (count >= MINUTES_PER_DAY) {System.out.println(); count = 0;}
        }

        return findStudyTimes(timeArray, startSec, fraction);
    }

    private static ArrayList<TimeChunk> findStudyTimes(int[] timeArray, long startSec, double fraction) {

        int length = timeArray.length;
        int[] freeTime = new int[length];

        for (int i = 0; i < length; i++) {
            if (timeArray[i] == 0) { freeTime[i] = 1; }
            else { freeTime[i] = 0; }
        }

        System.out.println();
        System.out.println("Hello, World!");
        int count = 0;
        for (var i : freeTime) {
            System.out.print(i + " ");
            count++;
            if (count >= 1440) {System.out.println(); count = 0;}
        }

        ArrayList<TimeChunk> chunks = new ArrayList<>();
        int state = -1;
        for (int i=0; i < length; i++) {
            if (freeTime[i] == 1 && state == -1) {
                state = i;
            } else if (freeTime[i] == 0 && state != -1)  {
                chunks.add(new TimeChunk(makeTime(startSec + (state)*60), makeTime(startSec + (i)*60)));
                state = -1;
            }
        }
        if (state != -1) { chunks.add(new TimeChunk(makeTime(startSec + state*60), makeTime(startSec + (length - 1)*60))); }

//        return chunks;
        return createStudyChunks(chunks, fraction);
    }

    private static ArrayList<TimeChunk> createStudyChunks(ArrayList<TimeChunk> chunks, double fraction) {

        ArrayList<TimeChunk> studyChunks = new ArrayList<>();
        int studyLength = (int) (fraction*60);

        for (var chunk: chunks) {
            long start = chunk.getStartTime().toEpochSecond(ZoneOffset.UTC)/60;
            long end = chunk.getEndTime().toEpochSecond(ZoneOffset.UTC)/60;
            int chunkLength = (int) (end - start);
            if (studyLength <= chunkLength) {
                double fractionSlots = (chunkLength + 5) * (1.0) / studyLength;
                int numSlots = (int) fractionSlots;
                System.out.println();
                System.out.println(fractionSlots);
                System.out.println(numSlots);
                if (fractionSlots - numSlots >= 0.5) { numSlots++;}
                System.out.println(numSlots);
                for (int i = 0; i < numSlots/2; i++) {
                    long forwardBegin = start + i*studyLength;
                    long forwardEnd = start + (i+1)*studyLength;
                    TimeChunk c = new TimeChunk(makeTime((forwardBegin-1)*60),makeTime((forwardEnd-1)*60));
                    studyChunks.add(c);
                    System.out.println(c.getStartTime() + " " + c.getEndTime());

                    long reverseEnd = end - i*studyLength;
                    long reverseBegin = end - (i+1)*studyLength;
                    c = new TimeChunk(makeTime(reverseBegin*60), makeTime(reverseEnd*60));
                    studyChunks.add(c);
                    System.out.println(c.getStartTime() + " " + c.getEndTime());
                }
                if (numSlots % 2 == 1) {
                    int i = numSlots/2+1;
                    long forwardBegin = start + i*studyLength;
                    long forwardEnd = start + (i+1)*studyLength;
                    studyChunks.add(new TimeChunk(makeTime((forwardBegin-1)*60),makeTime((forwardEnd-1)*60)));
                }
            }
        }

        return studyChunks;
    }
}

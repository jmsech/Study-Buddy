package com.studybuddy.controllers;

import com.studybuddy.models.TimeChunk;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;


public class barbaricTesting {

    public static final int SECONDS_PER_MINUTE = 60; //seconds/60 = minutes
    public static final long SECONDS_PER_DAY = 86400;
    public static final long SECONDS_OF_SLEEP = 28800;
    public static final int MINUTES_PER_HOUR = 60;

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
        for (var chunk : chunks) {
            System.out.println(chunk.getStartTime() + " " + chunk.getEndTime());
        }
    }

    private static LocalDateTime makeTime(long t) {
        return LocalDateTime.ofEpochSecond(t,0,ZoneOffset.ofHours(0));
    }

    private static ArrayList<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, ArrayList<TimeChunk> unavailable, double fraction) {

        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/SECONDS_PER_MINUTE;
        int[] timeArray = new int[lengthInMinutes];

        for (TimeChunk t : unavailable) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes - 1;}
            if (f >= lengthInMinutes) {f = lengthInMinutes - 1;}

            for (int i = s; i <= f; i++) { timeArray[i]++; }
        }

        long MINUTES_PER_DAY = SECONDS_PER_DAY/SECONDS_PER_MINUTE;
        long MINUTES_OF_SLEEP = SECONDS_OF_SLEEP/SECONDS_PER_DAY;
        long sleepStart = (LocalDateTime.of(2020,1,1,0,0)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / SECONDS_PER_MINUTE;

        int len = timeArray.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                timeArray[i] = -1;
            }
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

        ArrayList<TimeChunk> chunks = new ArrayList<>();
        int state = -1;
        for (int i=0; i < length; i++) {
            if (freeTime[i] == 1 && state == -1) {
                state = i;
            } else if (freeTime[i] == 0 && state != -1)  {
                chunks.add(new TimeChunk(
                        makeTime(startSec + (state)*SECONDS_PER_MINUTE),
                        makeTime(startSec + (i)*SECONDS_PER_MINUTE)));
                state = -1;
            }
        }
        if (state != -1) { chunks.add(new TimeChunk(
                makeTime(startSec + state*SECONDS_PER_MINUTE),
                makeTime(startSec + (length - 1)*SECONDS_PER_MINUTE))); }

        return createStudyChunks(chunks, fraction);
    }

    private static ArrayList<TimeChunk> createStudyChunks(ArrayList<TimeChunk> chunks, double fraction) {

        ArrayList<TimeChunk> studyChunks = new ArrayList<>();
        int studyLength = (int) (fraction*MINUTES_PER_HOUR);

        for (var chunk: chunks) {
            long start = chunk.getStartTime().toEpochSecond(ZoneOffset.UTC)/SECONDS_PER_MINUTE;
            long end = chunk.getEndTime().toEpochSecond(ZoneOffset.UTC)/SECONDS_PER_MINUTE;
            int chunkLength = (int) (end - start);
            if (studyLength <= chunkLength) {
                double fractionSlots = (chunkLength + 5) * (1.0) / studyLength;
                int numSlots = (int) fractionSlots;
                if (fractionSlots - numSlots >= 0.5) { numSlots++;}
                for (int i = 0; i < numSlots/2; i++) {
                    long forwardBegin = start + i*studyLength;
                    long forwardEnd = start + (i+1)*studyLength;
                    TimeChunk c = new TimeChunk(
                            makeTime((forwardBegin-1)*SECONDS_PER_MINUTE),
                            makeTime((forwardEnd-1)*SECONDS_PER_MINUTE));
                    studyChunks.add(c);

                    long reverseEnd = end - i*studyLength;
                    long reverseBegin = end - (i+1)*studyLength;
                    c = new TimeChunk(
                            makeTime(reverseBegin*SECONDS_PER_MINUTE),
                            makeTime(reverseEnd*SECONDS_PER_MINUTE));
                    studyChunks.add(c);
                }
                if (numSlots % 2 == 1) {
                    int i = numSlots/2;
                    long forwardBegin = start + i*studyLength;
                    long forwardEnd = start + (i+1)*studyLength;
                    TimeChunk c = new TimeChunk(
                            makeTime((forwardBegin-1)*SECONDS_PER_MINUTE),
                            makeTime((forwardEnd-1)*SECONDS_PER_MINUTE));
                    studyChunks.add(c);
                }
            }
        }

        class TimeChunkComparator implements Comparator<TimeChunk>{
            @Override
            public int compare(TimeChunk t1, TimeChunk t2) {
                long start1 = t1.getStartTime().toEpochSecond(ZoneOffset.UTC);
                long start2 = t2.getStartTime().toEpochSecond(ZoneOffset.UTC);
                if (start1 < start2) { return -1; }
                else if (start1 == start2) {return 0; }
                else {return 1; }
            }
        }

        studyChunks.sort(new TimeChunkComparator());

        return studyChunks;
    }
}

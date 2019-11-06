package com.studybuddy.controllers;
import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecsController {
    private Connection connection;

    public RecsController(Connection connection) throws SQLException {
        this.connection = connection;
    }

    //from studentController
    public void getRec(Context ctx) throws SQLException {

        //get all the buddies the user requested
        List<Integer> buddyIDs = new ArrayList<>();
        final int maxBuddies = 5;
        for (int i = 1; i <= maxBuddies; i++) {
            var key = "user" + i;
            var buddyEmail = ctx.formParam(key, String.class).getOrNull();
            if (buddyEmail != null) {
                var idStatement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
                idStatement.setString(1, buddyEmail);
                var idResult = idStatement.executeQuery();
                if (!idResult.isClosed()) {
                    buddyIDs.add(idResult.getInt("id"));
                }
            }
        }

        //add user requesting rec to the buddy list
        buddyIDs.add(Integer.parseInt(ctx.pathParam("userId")));

        //get session length
        var sessionLen = ctx.formParam("sessionLength", Integer.class).get();

        //get start and end times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        // Ensure that startTime is before endTime
        if (!endTime.isAfter(startTime)) {
            ctx.json("EventPeriodError");
            return;
        }

        //make a list of when everyone is busy
        ArrayList<TimeChunk> busyTimes = new ArrayList<>();

        for (Integer buddyID : buddyIDs) {
            var busyStatement = connection.prepareStatement("SELECT startTime, endTime FROM events WHERE userID = ?");
            busyStatement.setInt(1, buddyID);
            var buddyResult = busyStatement.executeQuery();

            while (buddyResult.next()) {
                busyTimes.add(
                        new TimeChunk(
                                buddyResult.getTimestamp("startTime").toLocalDateTime(),
                                buddyResult.getTimestamp("endTime").toLocalDateTime()
                        )
                );
            }
        }

        //use algo to get a list of recommended times everyone is free
        List<TimeChunk> recTimes = makeRecommendation(startTime, endTime, busyTimes, sessionLen);

        //make the recs an event
        List<User> inviteList = new ArrayList<>();
        List<Event> recsToDisplay = new ArrayList<>();

        for (TimeChunk recommendation: recTimes) {
            recsToDisplay.add((new Event(100, "Suggested Study Time", recommendation.getStartTime(), recommendation.getEndTime(),"This would be a good time to study", inviteList)));
        }
        ctx.json(recsToDisplay);

        /*
        ArrayList<User> stu = new ArrayList<>();

        TimeChunk suggested = new TimeChunk(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        boolean found = false;
        boolean addTime;

        while(!found) {
            addTime = false;
            for (TimeChunk busyTime : busyTimes) {
                if (busyTime.isOverlapping(suggested) || (suggested.getStartTime().getHour() < 9 )) {
                    addTime = true;
                }
            }
            if (addTime) {
                suggested.setStartTime(suggested.getStartTime().plusMinutes(15));
                suggested.setEndTime(suggested.getEndTime().plusMinutes(15));
            } else {
                found = true;
            }
        }

        ctx.json(new Event(100, "Suggested Event", suggested.getStartTime(), suggested.getEndTime(),"This would be a good time to study", stu));
        */
    }

/*
    public List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, List<TimeChunk> unavailable, double sessionLen) throws SQLException {
        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);

        int lengthInMinutes = (int) (startSec-endSec)/60;

        int[] timeArray = new int[lengthInMinutes];

        // TODO Determine when people are sleeping and set those values to be negative

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

        return findStudyTimes(timeArray);
    }

    private ArrayList<TimeChunk> findStudyTimes(int[] timeArray) {

        return null;
    }
*/
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

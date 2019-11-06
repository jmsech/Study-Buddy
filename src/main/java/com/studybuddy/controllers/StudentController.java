package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.User;
import com.studybuddy.models.TimeChunk;
import io.javalin.http.Context;

// Password security classes
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import java.io.IOException;
import java.security.GeneralSecurityException;

// Database handling

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentController {
    private Connection connection;

    public StudentController(Connection connection) throws SQLException {
        this.connection = connection;
    }
    public void collectGoogleEvents(Context ctx) throws GeneralSecurityException, IOException, SQLException {
        CalendarQuickstart.collectEvents(this.connection, ctx);
    }

    public void getRec(Context ctx) throws SQLException {
        var userid = ctx.pathParam("userId");
        var statement = connection.prepareStatement("SELECT startTime, endTime FROM events WHERE userID = ?");
        statement.setInt(1, Integer.parseInt(userid));
        var result = statement.executeQuery();

        List<TimeChunk> busyTimes = new ArrayList<>();
        while(result.next()) {
            busyTimes.add(
                    new TimeChunk(
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime()
                    )
            );
        }

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
    }

    public ArrayList<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, ArrayList<TimeChunk> unavailable) throws SQLException {
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
}

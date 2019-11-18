package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
            ctx.json("RecPeriodError");
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
//        List<TimeChunk> recTimes = RecommendationAlgorithm.makeRecommendation(startTime, endTime, busyTimes, sessionLen);
        List<TimeChunk> recTimes = RecommendationAlgorithm.makeBetterRecommendation(startTime, endTime, busyTimes, sessionLen, 24);

        //make the recs an event
        List<User> inviteList = new ArrayList<>();
        List<Event> recsToDisplay = new ArrayList<>();

        for (int i = 0; i < recTimes.size(); i++) {
            var recommendation = recTimes.get(i);
            var id = i + 1;
            recsToDisplay.add((new Event(id, "Suggested Study Time", recommendation.getStartTime(), recommendation.getEndTime(), "This would be a good time to study", inviteList)));
        }

        if (recsToDisplay.isEmpty()) {
            ctx.json("NoRecsToDisplay");
        } else {
            ctx.json(recsToDisplay);
        }
    }
}

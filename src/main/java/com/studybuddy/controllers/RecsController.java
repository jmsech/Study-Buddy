package com.studybuddy.controllers;

import com.studybuddy.RecommendationAlgorithm;
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

class RecsController {
    private Connection connection;

    RecsController(Connection connection) throws SQLException {
        this.connection = connection;
    }

    void getRec(Context ctx) throws SQLException {
        //get all the buddies the user requested
        List inviteList = ctx.formParam("inviteList", List.class).getOrNull();

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

        assert inviteList != null;
        for (Object buddyID : inviteList) {
            var busyStatement = this.connection.prepareStatement("SELECT e.startTime, e.endTime " +
                    "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                    "INNER JOIN users as u ON etum.userId = u.id " +
                    "WHERE u.id = ?");
            busyStatement.setInt(1, (int) buddyID);
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
        List<TimeChunk> recTimes = RecommendationAlgorithm.makeRecommendation(startTime, endTime, busyTimes, sessionLen);

        //make the recs an event
        List<Event> recsToDisplay = new ArrayList<>();

        for (int i = 0; i < recTimes.size(); i++) {
            var recommendation = recTimes.get(i);
            var id = i + 1;
            //TODO:: change to list of strings (actual people's names yay)
            recsToDisplay.add((new Event(id, "Suggested Study Time", recommendation.getStartTime(), recommendation.getEndTime(), "This would be a good time to study", inviteList, "")));
        }

        if (recsToDisplay.isEmpty()) {
            ctx.json("NoRecsToDisplay");
        } else {
            ctx.json(recsToDisplay);
        }
    }
}

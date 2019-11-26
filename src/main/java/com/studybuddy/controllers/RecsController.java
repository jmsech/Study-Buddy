package com.studybuddy.controllers;

import com.studybuddy.RecommendationAlgorithm;
import com.studybuddy.WeightedRecommendationAlgorithm;
import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import com.studybuddy.repositories.EventRepository;
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
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();
        List<Integer> idInviteList = new ArrayList<>();
        List<User> inviteList = new ArrayList<>();
        idInviteList.add(userId);
        // Get ids from email invite list
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                var statement = connection.prepareStatement("SELECT id, email, firstName, lastName FROM users WHERE email = ?");
                statement.setString(1, email);
                var result = statement.executeQuery();
                if (result.next()) {
                    idInviteList.add(result.getInt("id"));
                    String name = result.getString("firstName") + " " + result.getString("lastName");
                    inviteList.add(
                            new User(
                                    result.getInt("id"),
                                    name,
                                    result.getString("email")
                            )
                    );
                } else {
                    ctx.json("InviteListError");
                    return;
                }
            }
        }

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

        idInviteList = EventRepository.removeDuplicates(idInviteList); // Remove potential duplicates
        for (Object buddyID : idInviteList) {
            var busyStatement = this.connection.prepareStatement("SELECT e.startTime, e.endTime " +
                    "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                    "INNER JOIN users as u ON etum.userId = u.id " +
                    "WHERE u.id = ? AND e.expired = FALSE");
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

        // Create list of busy times for the event host
        var hostBusyStatement = this.connection.prepareStatement("SELECT e.startTime, e.endTime " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = FALSE");
        hostBusyStatement.setInt(1, userId);
        var hostResult = hostBusyStatement.executeQuery();
        var hostBusyTimes = new ArrayList<TimeChunk>();

        while (hostResult.next()) {
            hostBusyTimes.add(
                    new TimeChunk(
                            hostResult.getTimestamp("startTime").toLocalDateTime(),
                            hostResult.getTimestamp("endTime").toLocalDateTime()
                    )
            );
        }

        //use algo to get a list of recommended times everyone is free
        List<TimeChunk> recTimes = WeightedRecommendationAlgorithm.makeRecommendation(startTime, endTime, busyTimes, hostBusyTimes, sessionLen, 10);

        //make the recs an event
        List<Event> recsToDisplay = new ArrayList<>();

        for (int i = 0; i < recTimes.size(); i++) {
            var recommendation = recTimes.get(i);
            var id = i + 1;
            recsToDisplay.add((new Event(id, "Suggested Study Time", recommendation.getStartTime(), recommendation.getEndTime(), "This would be a good time to study", inviteList, "")));
        }

        if (recsToDisplay.isEmpty()) {
            ctx.json("NoRecsToDisplay");
        } else {
            ctx.json(recsToDisplay);
        }
    }
}

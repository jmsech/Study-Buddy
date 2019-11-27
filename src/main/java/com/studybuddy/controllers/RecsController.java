package com.studybuddy.controllers;

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

    RecsController(Connection connection) {
        this.connection = connection;
    }

    void getRec(Context ctx) throws SQLException {
        //get all the buddies the user requested
        String inviteListString = ctx.formParam("inviteList", String.class).getOrNull();
        var userId = ctx.formParam("userId", Integer.class).get();


        var idInviteList = EventRepository.createIdListFromInviteList(connection, inviteListString);
        if (idInviteList == null) {ctx.json("InviteListError"); return;}
        idInviteList.add(userId);
        var inviteList = EventRepository.createUserListFromInviteList(connection, inviteListString);
        if (inviteList == null) {ctx.json("InviteListError"); return;}

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
        List<TimeChunk> busyTimes = new ArrayList<>();
        idInviteList = EventRepository.removeDuplicates(idInviteList); // Remove potential duplicates
        for (int buddyID : idInviteList) {
            busyTimes = EventRepository.getBusyTimesFromId(connection, buddyID, busyTimes);
        }

        // Create list of busy times for the event host
        List<TimeChunk> hostBusyTimes = EventRepository.getBusyTimesFromId(connection, userId, null);

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

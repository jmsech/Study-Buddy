package com.studybuddy.controllers;

import com.studybuddy.WeightedRecommendationAlgorithm;
import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import com.studybuddy.repositories.IdRepository;
import com.studybuddy.repositories.UserRepository;
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


        ArrayList<Integer> idInviteList = new ArrayList<>();
        idInviteList.add(userId);
        idInviteList = IdRepository.createIdListFromInviteList(connection, inviteListString, idInviteList);
        if (idInviteList == null) {ctx.json("InviteListError"); return;}
        var inviteList = UserRepository.createUserListFromIdList(connection, inviteListString);
        if (inviteList == null) {ctx.json("InviteListError"); return;}

        //get session length
        var sessionLen = ctx.formParam("sessionLength", Integer.class).get();

        //get start and end times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        // Ensure that startTime is before endTime
        if (!endTime.isAfter(startTime)) {
            ctx.json("RecPeriodError");
            return;
        }

        //make a list of when everyone is busy
        List<List<TimeChunk>> busyTimes = new ArrayList<>();
        List<Integer> alwaysFree = new ArrayList<>();
        for (int buddyID : idInviteList) {
            var busy = UserRepository.getUserBusyTimesFromId(connection, buddyID, null);
            if (busy == null) {
                alwaysFree.add(buddyID);
            } else {
                busyTimes.add(busy);
            }
        }

        // Create list of busy times for the event host
        List<TimeChunk> hostBusyTimes = UserRepository.getUserBusyTimesFromId(connection, userId, null);

        //use algo to get a list of recommended times everyone is free
        List<TimeChunk> recTimes = WeightedRecommendationAlgorithm.makeRecommendation(startTime, endTime, busyTimes, hostBusyTimes, alwaysFree, sessionLen, 10);

        //make the recs an event
        List<Event> recsToDisplay = new ArrayList<>();

        for (int i = 0; i < recTimes.size(); i++) {
            var recommendation = recTimes.get(i);
            var id = i + 1;
            List<User> users = UserRepository.createUserListFromIdList(connection, recommendation.getUserIDs());
            if (users == null) { users = new ArrayList<>(); }
            recsToDisplay.add((new Event(id, "Buddy Study Time", recommendation.getStartTime(), recommendation.getEndTime(), "Some of your buddies are free at this time!", users, "")));
        }

        if (recsToDisplay.isEmpty()) {
            ctx.json("NoRecsToDisplay");
        } else {
            ctx.json(recsToDisplay);
        }
    }
}

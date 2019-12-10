package com.studybuddy.controllers;

import com.studybuddy.WeightedRecommendationAlgorithm;
import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import com.studybuddy.repositories.CourseRepository;
import com.studybuddy.repositories.IdRepository;
import com.studybuddy.repositories.UserRepository;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseLinkRecsController {
    private Connection connection;

    CourseLinkRecsController(Connection connection) {
        this.connection = connection;
    }

    void getRec(Context ctx) throws SQLException {
        //get user id
//        var mainCourseId = ctx.formParam("courseId", String.class).get(); //FIXME: We have to make a new form
        String mainCourseId = "EN.601.421(01)Fa2019"; // FIXME: THis is hardcoded
        var userId = ctx.formParam("userId", Integer.class).get();
        HashMap<Integer, Double> user_to_weight = new HashMap<>();
        user_to_weight.put(userId, (double) 100); //FIXME: THis should be a constant somewhere

        List<String> userCourseIDs = CourseRepository.getActiveCourseIdListFromUserId(connection, userId);
        List<Integer> coursemateIds = IdRepository.getUserIdListFromCourseId(connection, mainCourseId);

        for (var id : coursemateIds) {
            double weight = 0;
            List<String> classes = CourseRepository.getActiveCourseIdListFromUserId(connection, id);
            for (var c : classes) {
                if (userCourseIDs.contains(c)) { weight += 1;}
            }
            if (user_to_weight.containsKey(id)) {
                user_to_weight.put(id, user_to_weight.get(id) + weight);
            } else {
                user_to_weight.put(id, weight);
            }
        }

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
        for (int buddyID : user_to_weight.keySet()) {
            var busy = UserRepository.getUserBusyTimesFromId(connection, buddyID, null, user_to_weight.get(buddyID));
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
            var id = i + 1; // FIXME Is this correct?
            List<User> users = UserRepository.createUserListFromIdList(connection, recommendation.getUserIDs());
            if (users == null) { users = new ArrayList<>(); }
            recsToDisplay.add((new Event(id, "Suggested Study Time", recommendation.getStartTime(), recommendation.getEndTime(), "This would be a good time to study", users, "")));
        }

        if (recsToDisplay.isEmpty()) {
            ctx.json("NoRecsToDisplay");
        } else {
            ctx.json(recsToDisplay);
        }
    }
}

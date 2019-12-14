package com.studybuddy.controllers;

import com.studybuddy.RecommendationAlgorithm;
import com.studybuddy.WeightedRecommendationAlgorithm;
import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BuddyRecsController {

    private Connection connection;

    BuddyRecsController(Connection connection) throws SQLException {
        this.connection = connection;
    }

    void getBuddyRec(Context ctx) throws SQLException {
        //we want to get all the users who are in the course the user requested
        String courseNum = ctx.formParam("courseNum", String.class).get();
        var userId = ctx.formParam("userId", Integer.class).get();

        List<Integer> idAllInCourseList = new ArrayList<>();
        List<User> allInCourseList = new ArrayList<>();

        //get id of user's specifed course
        var statement = connection.prepareStatement("SELECT id FROM courses WHERE courseNum = ?");
        statement.setString(1, courseNum);
        var result = statement.executeQuery();
        var courseId = result.getInt("id");
        statement.close();

        //now get all ID's of users who are listed for that course
        var idStatement = connection.prepareStatement("SELECT id FROM courses_to_users_mapping WHERE courseID = ?");
        idStatement.setInt(1, courseId);
        var idResult = idStatement.executeQuery();

        if (idResult.next()) {
            idAllInCourseList.add(result.getInt("id"));
        }

        //now it's as if we wanted a rec for these users, so call function to convert their ids to timechunks

        //now we can call rec algo

        //and then display
    }

}

package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.ParticularCourse;
import com.studybuddy.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IdRepository {

    public static ArrayList<Integer> createIdListFromInviteList(Connection connection, String inviteListString) throws SQLException {
        return createIdListFromInviteList(connection, inviteListString, null);
    }

    public static ArrayList<Integer> createIdListFromInviteList(Connection connection, String inviteListString, ArrayList<Integer> idInviteList) throws SQLException {
        if (idInviteList == null) { idInviteList = new ArrayList<>(); }
        ArrayList<PreparedStatement> statements = new ArrayList<>();
        // Get ids from email invite list
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                var result = getIdFromEmail(email, connection, statements);
                if (result.next()) {
                    idInviteList.add(result.getInt("id"));
                } else {
                    return null;
                }
            }
        }
        for (var s : statements) {s.close();}
        idInviteList = removeDuplicates(idInviteList);
        return idInviteList;
    }

    public static ArrayList<Integer> getUserIdListFromCourseId(Connection connection, String courseId) throws SQLException {
        var statement = connection.prepareStatement("SELECT userId FROM courses_to_users_mapping  WHERE courseId = ?");
        statement.setString(1, courseId);
        var result = statement.executeQuery();
        ArrayList<Integer> userIds = new ArrayList<>();
        while (result.next()) {
            userIds.add(result.getInt("userId"));
        }
        statement.close();
        return userIds;
    }

    public static ArrayList<Integer> getUserIdListFromAllSections(Connection connection, String courseId) throws SQLException {
        String root = courseId.substring(0, courseId.length() - 10);
        var statement = connection.prepareStatement("SELECT courseId FROM courses  WHERE CHARINDEX(?, courseId) > 0");
        statement.setString(1, root);
        var result = statement.executeQuery();
        ArrayList<String> courses = new ArrayList<>();
        while (result.next()) {
            courses.add(result.getString("courseId"));
        }
        statement.close();

        ArrayList<Integer> users = new ArrayList<>();
        for (var cid : courses) {
            users.addAll(IdRepository.getUserIdListFromCourseId(connection, cid));
        }

        return users;
    }

    public static int getIdFromEmail(String email, Connection connection) throws SQLException {
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
        statement.setString(1, email);
        var result = statement.executeQuery();
        if (result.isBeforeFirst()) {
            int id = result.getInt("id");
            statement.close();
            return id;
        }
        statement.close();
        return -1;
    }

    private static java.sql.ResultSet getIdFromEmail(String email, Connection connection, List<PreparedStatement> statements) throws SQLException {
        var statement = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
        statement.setString(1, email);
        statements.add(statement);
        return statement.executeQuery();
    }

    private static ArrayList<Integer> removeDuplicates(ArrayList<Integer> list) {
        ArrayList<Integer> resultList = new ArrayList<>();
        for (Integer item : list) {
            if (!resultList.contains(item)) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}

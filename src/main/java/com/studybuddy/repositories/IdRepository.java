package com.studybuddy.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
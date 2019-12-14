package com.studybuddy.repositories;

import com.studybuddy.models.Event;
import com.studybuddy.models.TimeChunk;
import com.studybuddy.models.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public static User getUser(Connection connection, int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT email, firstName, lastName FROM users WHERE id = ?");
        statement.setInt(1, id);
        var result = statement.executeQuery();
        if (result.next()) {
            String name = result.getString("firstName") + " " + result.getString("lastName");
            return new User(id, name, result.getString("email"));
        }
        return null;
    }

    public static List<User> getAwaitingFromUserId(Connection connection, int userId) throws SQLException {
        var statement = connection.prepareStatement("SELECT buddyId FROM friends WHERE userId = ? AND friend = ?");
        statement.setInt(1, userId);
        statement.setBoolean(2, false);
        var result = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (result.next()) {
            ids.add(result.getInt("userId"));
        }
        statement.close();
        return UserRepository.createUserListFromIdList(connection, ids);
    }

    public static List<User> getPendingFromUserId(Connection connection, int userId) throws SQLException {
        var statement = connection.prepareStatement("SELECT userId FROM friends WHERE buddyId = ? AND friend = ?");
        statement.setInt(1, userId);
        statement.setBoolean(2, false);
        var result = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (result.next()) {
            ids.add(result.getInt("userId"));
        }
        statement.close();
        return UserRepository.createUserListFromIdList(connection, ids);
    }

    public static List<User> getFriendsFromUserId(Connection connection, int userId) throws SQLException {
        var statement = connection.prepareStatement("SELECT buddyId FROM friends WHERE userId = ? AND friend = ?");
        statement.setInt(1, userId);
        statement.setBoolean(2, true);
        var result = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (result.next()) {
            ids.add(result.getInt("buddyId"));
        }
        statement.close();
        return UserRepository.createUserListFromIdList(connection, ids);
    }

    public static boolean addFriend(Connection connection, int userId, int buddyId) throws SQLException {
        var check_statement = connection.prepareStatement("SELECT 1 FROM  friends WHERE userId = ? LIMIT 1");
        check_statement.setInt(1, userId);
        var check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            return false;
        }
        check.close();

        boolean friends = false;
        check_statement = connection.prepareStatement("SELECT 1 FROM  friends WHERE userId = ? AND buddyId = ? LIMIT 1");
        check_statement.setInt(1, buddyId);
        check_statement.setInt(2, userId);
        check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            friends = true;
        }
        check.close();

        var statement = connection.prepareStatement("INSERT INTO friends(userId, buddyId, friends, weight) VALUES (?, ?, ?, ?)");
        statement.setInt(1, userId);
        statement.setInt(2, buddyId);
        statement.setBoolean(3, friends);
        statement.setDouble(4, 1.0);
        statement.executeUpdate();

        if (friends) {
            statement = connection.prepareStatement("UPDATE friends SET friends = ?, weight = ? WHERE userId = ? AND buddyId = ?");
            statement.setBoolean(1, friends);
            statement.setDouble(2, 1.0);
            statement.setInt(3, buddyId);
            statement.setInt(4, userId);
            statement.executeUpdate();
        }
        return true;
    }

    public static void removeFriend(Connection connection, int userId, int buddyId) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM friends WHERE userId = ? AND buddyId = ?");
        statement.setInt(1, userId);
        statement.setInt(2, buddyId);
        statement.executeUpdate();
        statement.close();

        statement = connection.prepareStatement("DELETE FROM friends WHERE userId = ? AND buddyId = ?");
        statement.setInt(1, buddyId);
        statement.setInt(2, userId);
        statement.executeUpdate();
        statement.close();
    }

    public static boolean createUser(Connection connection, String email, String password, String firstName, String lastName) throws SQLException {
        var checkStatement = connection.prepareStatement("SELECT 1 FROM users WHERE email = ?");
        checkStatement.setString(1, email);
        var result = checkStatement.executeQuery();
        if (result.next()) { // If the query returns something, the user already exists
            return false;
        } else { // If user doesn't exist, add to the database
            // Hash password using PBKDF2 before storing
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            assert password != null;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, AuthenticationRepository.HASHING_ITERATION_COUNT, AuthenticationRepository.HASHING_KEY_LENGTH);
            SecretKeyFactory factory; byte[] hashedPassword;
            try {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                hashedPassword = factory.generateSecret(spec).getEncoded();
            } catch (Exception e) {
                return false;
            }
            String hashedPasswordStr = AuthenticationRepository.bytesToHex(hashedPassword);
            String hashSaltStr = AuthenticationRepository.bytesToHex(salt);
            // Insert into database
            var statement = connection.prepareStatement("INSERT INTO users (email, hashedPassword, hashSalt, firstName, lastName) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, email);
            statement.setString(2, hashedPasswordStr);
            statement.setString(3, hashSaltStr);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.executeUpdate();
            statement.close();
            return true;
        }
    }

    public static List<User> getAllUsers(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT id, email, firstName, lastName FROM users");
        var users = new ArrayList<User>();

        // Iterate through result
        while (result.next()) {
            String name = result.getString("firstName") + " " + result.getString("lastName");
            users.add(
                    new User(
                            result.getInt("id"),
                            name,
                            result.getString("email")
                    )
            );
        }
        return users;
    }

    public static List<User> createUserListFromIdList(Connection connection, String inviteListString) throws SQLException {
        ArrayList<User> inviteList = new ArrayList<>();
        ArrayList<PreparedStatement> statements = new ArrayList<>();
        if (inviteListString != null) {
            var emailInviteList = inviteListString.split("\\s*,\\s*");
            for (String email : emailInviteList) {
                var statement = connection.prepareStatement("SELECT id, email, firstName, lastName FROM users WHERE email = ?");
                statement.setString(1, email);
                var result = statement.executeQuery();
                statements.add(statement);
                if (result.next()) {
                    String name = result.getString("firstName") + " " + result.getString("lastName");
                    inviteList.add(
                            new User(
                                    result.getInt("id"),
                                    name,
                                    result.getString("email")
                            )
                    );
                } else {
                    return null;
                }
            }
        }
        for (var s : statements) {s.close();}
        return inviteList;
    }

    public static List<User> createUserListFromIdList(Connection connection, List<Integer> inviteList) throws SQLException {
        List<User> userList = new ArrayList<>();
        ArrayList<PreparedStatement> statements = new ArrayList<>();
        for (int userId : inviteList) {
            var statement = connection.prepareStatement("SELECT id, email, firstName, lastName FROM users WHERE id = ?");
            statement.setInt(1, userId);
            var result = statement.executeQuery();
            statements.add(statement);
            if (result.next()) {
                String name = result.getString("firstName") + " " + result.getString("lastName");
                userList.add(
                        new User(
                                userId,
                                name,
                                result.getString("email")
                        )
                );
            } else {
                return null;
            }
        }
        for (var s : statements) {s.close();}
        return userList;
    }

    public static List<TimeChunk> getUserBusyTimesFromId(Connection connection, int id, List<TimeChunk> busyTimes) throws SQLException {
        return getUserBusyTimesFromId(connection, id, busyTimes, TimeChunk.DEFAULT_WEIGHT);
    }

    public static List<TimeChunk> getUserBusyTimesFromId(Connection connection, int id, List<TimeChunk> busyTimes, double weight) throws SQLException {
        if (busyTimes == null) { busyTimes = new ArrayList<TimeChunk>(); }
        var statement = connection.prepareStatement("SELECT e.startTime, e.endTime " +
                "FROM events AS e INNER JOIN events_to_users_mapping AS etum ON e.id = etum.eventId " +
                "INNER JOIN users as u ON etum.userId = u.id " +
                "WHERE u.id = ? AND e.expired = FALSE");
        statement.setInt(1, id);
        var result = statement.executeQuery();

        // User has no unavailable times
        if (!result.isBeforeFirst()) {
            return null;
        }

        while (result.next()) {
            busyTimes.add(
                    new TimeChunk(
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime(),
                            weight,
                            id
                    )
            );
        }
        statement.close();
        return busyTimes;
    }
}

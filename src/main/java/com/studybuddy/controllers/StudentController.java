package com.studybuddy.controllers;

import com.studybuddy.models.Event;
import com.studybuddy.models.Student;
import com.studybuddy.models.User;
import com.studybuddy.models.TimeChunk;
import io.javalin.http.Context;

// Password security classes
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

// Database handling
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentController {
    private static int hashingIterationCount = 65536;
    private static int hashingKeyLength = 128;
    private Student student;
    private Connection connection;

    public StudentController(Student student, Connection connection) throws SQLException {
        this.student = student;
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, hosts INTEGER, userID INTEGER)");
        statement.close();
    }

    public void getEvents(Context ctx) throws SQLException {
        var events = new ArrayList<Event>();
        var userid = ctx.pathParam("userID", Integer.class).get();
        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description FROM events WHERE userID = ?");
        statement.setInt(1, userid);
        var result = statement.executeQuery();
        ArrayList<User> stu = new ArrayList<>();
        while (result.next()) {
            events.add(
                    new Event(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime(),
                            result.getString("description"),
                            stu
                    )
            );
        }
        result.close();
        statement.close();
        ctx.json(events);
    }

    public void createEvent(Context ctx) throws SQLException {
        var title = ctx.formParam("title", ctx.formParam("title", String.class).get());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", ctx.formParam("description", String.class).get());
        var userID = ctx.formParam("userID", Integer.class).get();

        // TODO change call to consider inviteList and importance
        //TODO add actual values to the insert
        var statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, userID) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, title);
        statement.setTimestamp(2, sqlStartDate);
        statement.setTimestamp(3, sqlEndDate);
        statement.setString(4, description);
        statement.setInt(5, userID);
        statement.executeUpdate();
        statement.close();
        ctx.status(201);
    }

    public void deleteEvent(Context ctx) throws SQLException {
        var id = ctx.pathParam("id");
        var statement = connection.prepareStatement("DELETE FROM events WHERE id = ?");
        statement.setInt(1, Integer.parseInt(id));
        statement.executeUpdate();
        statement.close();
        ctx.status(200);
    }

    // Helper to convert individual byte to string
    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    // Helper function to convert byte array to a hex string
    private String bytesToHex(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    // Helper function to convert hex string to byte array
    private byte[] hexToBytes(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public void createUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        var firstName = ctx.formParam("firstName");
        var lastName = ctx.formParam("lastName");
        // Check if user already exists (email)
        var checkStatement = connection.prepareStatement("SELECT 1 FROM users WHERE email = ?");
        checkStatement.setString(1, email);
        var result = checkStatement.executeQuery();
        if (result.next()) { // If the query returns something, the user already exists
            ctx.json(false);
        } else { // If user doesn't exist, add to the database
            // Hash password using PBKDF2 before storing
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            assert password != null;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, StudentController.hashingIterationCount, StudentController.hashingKeyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
            String hashedPasswordStr = bytesToHex(hashedPassword);
            String hashSaltStr = bytesToHex(salt);
            // Insert into database
            var statement = connection.prepareStatement("INSERT INTO users (email, hashedPassword, hashSalt, firstName, lastName) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, email);
            statement.setString(2, hashedPasswordStr);
            statement.setString(3, hashSaltStr);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.executeUpdate();
            statement.close();
            ctx.json(true);
        }
        ctx.status(201);
    }

    public void authenticateUser(Context ctx) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");
        // Search for the user based on their email
        var statement = connection.prepareStatement("SELECT id, hashedPassword, hashSalt FROM users WHERE email = ?");
        statement.setString(1, email);
        var result = statement.executeQuery();
        boolean userFound = false;
        String storedHashedPassword = "";
        String hashedPassword = "";
        var id = 0;
        while (result.next()) {
            storedHashedPassword = result.getString("hashedPassword");
            var salt = this.hexToBytes(result.getString("hashSalt"));
            // Hash password an compare to stored value
            assert password != null;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, StudentController.hashingIterationCount, StudentController.hashingKeyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            hashedPassword = bytesToHex(hash);
            id = result.getInt("id");
            userFound = true;
        }
        // If user is not found or password doesn't match, return 0 (indicates no user)
        if (!userFound || (!hashedPassword.equals(storedHashedPassword))) {
            ctx.json(0);
        } else {
            // Return the user's id to access their events
            ctx.json(id);
        }
        result.close();
        statement.close();
    }

    public void getRec(Context ctx) throws SQLException {
        var userid = ctx.pathParam("userId");
        var statement = connection.prepareStatement("SELECT startTime, endTime FROM events WHERE userID = ?");
        statement.setInt(1, Integer.parseInt(userid));
        var result = statement.executeQuery();

        List<TimeChunk> busyTimes = new ArrayList<>();
        while(result.next()) {
            busyTimes.add (
                    new TimeChunk(
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime()
                    )
            );
        }

//        boolean done = false;
//        while (!done) {
//            done = true;
//            for (int i = 0; i < busyTimes.size(); i++) {
//                for (int j = 0; j < busyTimes.size(); j++) {
//                    if (busyTimes.get(i).isOverlapping(busyTimes.get(j))) {
//                        busyTimes.get(i).merge(busyTimes.get(j));
//                        busyTimes.remove(j);
//                        done = false;
//                    }
//                }
//            }
//        }

        ArrayList<User> stu = new ArrayList<>();
        LocalDateTime sleepTimeStart = LocalDateTime.of(2019, Month.OCTOBER, 22, 0, 0);
        LocalDateTime sleepTimeEnd = LocalDateTime.of(2019, Month.OCTOBER,22,9,0);
        TimeChunk sleepTimeChunk = new TimeChunk(sleepTimeStart, sleepTimeEnd);

        TimeChunk suggested = new TimeChunk(sleepTimeEnd, sleepTimeEnd.plusHours(1));
        boolean found = false;
        boolean addTime;

        while(!found) {
            addTime = false;
            for (TimeChunk busyTime : busyTimes) {
                if (suggested.isOverlapping(busyTime) || sleepTimeChunk.isOverlapping(suggested)) {
                    addTime = true;
                }
            }
            if (addTime) {
                suggested.setStartTime(suggested.getStartTime().plusMinutes(15));
                suggested.setEndTime(suggested.getEndTime().plusMinutes(15));
            } else {
                found = true;
            }
        }

        ctx.json(new Event(100, "Suggested Event", suggested.getStartTime(), suggested.getEndTime(),"I think that you should study during this time", stu));
    }
}

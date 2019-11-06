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
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
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
        var title = ctx.formParam("title", String.class).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startTime = LocalDateTime.parse(ctx.formParam("startTime", String.class).get(), formatter);
        java.sql.Timestamp sqlStartDate = java.sql.Timestamp.valueOf(startTime);
        LocalDateTime endTime = LocalDateTime.parse(ctx.formParam("endTime", String.class).get(), formatter);
        java.sql.Timestamp sqlEndDate = java.sql.Timestamp.valueOf(endTime);
        // Ensure that startTime is before endTime
        if (!endTime.isAfter(startTime)) {
            ctx.json("EventPeriodError");
            return;
        }

        var location = ctx.formParam("location", "");
        var description = ctx.formParam("description", String.class).getOrNull();
        // Set description to empty string if it is null
        if (description == null) {
            description = "";
        }
        var userID = ctx.formParam("userID", Integer.class).get();

        // TODO change call to consider inviteList and importance
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
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuilder.append(byteToHex(b));
        }
        return hexStringBuilder.toString();
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
            busyTimes.add(
                    new TimeChunk(
                            result.getTimestamp("startTime").toLocalDateTime(),
                            result.getTimestamp("endTime").toLocalDateTime()
                    )
            );
        }

        ArrayList<User> stu = new ArrayList<>();

        TimeChunk suggested = new TimeChunk(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        boolean found = false;
        boolean addTime;

        while(!found) {
            addTime = false;
            for (TimeChunk busyTime : busyTimes) {
                if (busyTime.isOverlapping(suggested) || (suggested.getStartTime().getHour() < 9 )) {
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

        ctx.json(new Event(100, "Suggested Event", suggested.getStartTime(), suggested.getEndTime(),"This would be a good time to study", stu));
    }

    public ArrayList<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, ArrayList<TimeChunk> unavailable) throws SQLException {
        int day = start.getDayOfYear();
        int hour = start.getHour();
        int minute = start.getMinute();
        int second = start.getSecond();

        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);

        int lengthInMinutes = (int) (startSec-endSec)/60;

        int[] timeArray = new int[lengthInMinutes];

        // TODO Determine when people are sleeping and set those values to be negative

        for (TimeChunk t : unavailable) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/60;
            int f = (int) (trueF - startSec)/60;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes - 1;}
            if (f >= lengthInMinutes) {f = lengthInMinutes - 1;}

            for (int i = s; i <= f; i++) { timeArray[i]++; }
        }

        long SECONDS_PER_DAY = 86400;
        long MINUTES_PER_DAY = 1440;
        long MINUTES_OF_SLEEP = 480;
        long sleepStart = (LocalDateTime.ofEpochSecond(1893474000,0,ZoneOffset.UTC)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / 60;

        int len = timeArray.length;
        for (int i = 0; i < len; i++) {
            if (i >= relativeSleepStart) {
                if ((i - relativeSleepStart) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                    timeArray[i] = -1;
                }
            }
        }

        for (var i : timeArray) { System.out.println(i);}
        
        return findStudyTimes(timeArray);
    }

    private ArrayList<TimeChunk> findStudyTimes(int[] timeArray) {

        return null;
    }
}

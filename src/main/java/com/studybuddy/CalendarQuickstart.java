package com.studybuddy;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import io.javalin.http.Context;

import java.io.*;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

public class CalendarQuickstart {
    private static final String APPLICATION_NAME = "StudyBuddy";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";//"/client_secret_761873610788-8pebe946darosar1fc3l2rtga5dkd4h0.apps.googleusercontent.com.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void collectEvents(Connection connection, Context ctx) throws IOException, GeneralSecurityException, SQLException {
        var userId = ctx.formParam("userID", Integer.class).get();
        var daysToCollect = ctx.formParam("daysToCollect", Integer.class).get();
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service;
        try {
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException e) {
            ctx.json("Access Denied");
            return;
        }

        /////// Delete Current Google Event////////////
        var statement = connection.prepareStatement("SELECT id, title, startTime, endTime, description, isGoogleEvent FROM events WHERE hostId = ?");
        statement.setInt(1, userId);
        var result = statement.executeQuery();
        while (result.next()) {
            //check if current event is a google event, delete from database
            if (result.getBoolean("isGoogleEvent")) {
                var curEvent = connection.prepareStatement("DELETE FROM events WHERE id = ?");
                curEvent.setInt(1, result.getInt("id"));
                curEvent.executeUpdate();
                curEvent.close();
            }
        }
        result.close();
        statement.close();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime stopDate = (new DateTime(System.currentTimeMillis() + daysToCollect * 24 * 3600 * 1000));
        Events events = service.events().list("primary")
//                .setMaxResults(10)
                .setTimeMin(now)
                .setTimeMax(stopDate)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            ctx.json("No Upcoming Events");
        } else {
            for (Event event : items) {
                DateTime startDate = event.getStart().getDateTime();
                if (startDate != null) {
                    Timestamp start = new Timestamp(startDate.getValue());
                    Timestamp end = new Timestamp(event.getEnd().getDateTime().getValue());
                    statement = connection.prepareStatement("INSERT INTO events (title, startTime, endTime, description, location, hostId, isGoogleEvent, expired) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    statement.setString(1, event.getSummary());
                    statement.setTimestamp(2, start);
                    statement.setTimestamp(3, end);
                    statement.setString(4, event.getDescription());
                    statement.setString(5, event.getLocation());
                    statement.setInt(6, userId);
                    statement.setBoolean(7,true);
                    statement.setBoolean(8, false);
                    statement.executeUpdate();
                    statement.close();

                    // Insert into user to event mapping database
                    var lastIdStatement = connection.createStatement();
                    var lastInsert = lastIdStatement.executeQuery("SELECT last_insert_rowid() AS eventId FROM events");
                    var eventId = lastInsert.getInt("eventId");
                    statement = connection.prepareStatement("INSERT INTO events_to_users_mapping (eventId, userId) VALUES (?, ?)");
                    statement.setInt(1, eventId);
                    statement.setInt(2, userId);
                    statement.executeUpdate();
                    statement.close();
                }
            }
            ctx.status(201);
            ctx.json("Calender Synced Successfully");
        }
    }
}
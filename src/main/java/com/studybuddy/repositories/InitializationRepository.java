package com.studybuddy.repositories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studybuddy.models.SISCourse;

public class InitializationRepository {

    public static void initializeTables(Connection connection) throws SQLException, IOException {
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, location TEXT, hostId INTEGER, isGoogleEvent BOOLEAN, expired BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS events_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, eventId INTEGER, userId INTEGER, FOREIGN KEY (eventId) REFERENCES events (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.execute("CREATE TABLE IF NOT EXISTS courses (courseId TEXT, courseNum TEXT, courseDescription TEXT, courseSectionNum TEXT, courseName TEXT, instructorName TEXT, semester TEXT, isActive BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS courses_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, courseId TEXT, userId INTEGER, FOREIGN KEY (courseId) REFERENCES courses (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.close();

        populateCoursesTable(connection);
    }

    private static void populateCoursesTable(Connection connection) throws IOException {
        String classesContent = obtainJHUClasses();

    }

    // This functions obtains JHU classes based on the SIS API
    // Most of the code structure used here comes from the following tutorial: https://www.baeldung.com/java-http-request
    private static String obtainJHUClasses() throws IOException {
        String whitingSchoolString = "Whiting School Of Engineering".replaceAll(" ", "%20");
        String kriegerSchoolString = "Krieger School of Arts and Sciences".replaceAll(" ", "%20");
        List<String> schools = new ArrayList<>();
        schools.add(whitingSchoolString);
        schools.add(kriegerSchoolString);
        for (String schoolString : schools) {
            String classesContent = obtainJHUClassesInSpecificSchool(schoolString);

            List courseList = jsonMapper(classesContent);
            //System.out.println(classesContent.substring(0, 500));
        }
        return null;
    }

    private static String obtainJHUClassesInSpecificSchool(String schoolString) throws IOException {
        // Call SIS API
        String sisApiKey = "hVoxoRv4oeyxBEdxcJ6X5sb9KNNTMOu4";
        String baseUrl = "https://sis.jhu.edu/api/classes/";
        String urlString = baseUrl + schoolString + "/current?key=" + sisApiKey;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // Get response
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    private static List jsonMapper(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<SISCourse> courseList = mapper.readValue(json, new TypeReference<List<SISCourse>>(){});
        // pretty print
        String course = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(courseList.get(0));
        System.out.println(course);
        System.out.println(courseList.size());

        return courseList;
    }
}

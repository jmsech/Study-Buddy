package com.studybuddy.repositories;

import java.sql.Connection;
import java.sql.SQLException;

public class InitializationRepository {

    public static void initializeTables(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, hashedPassword TEXT, hashSalt TEXT, firstName TEXT, lastName TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime DATETIME, endTime DATETIME, description TEXT, location TEXT, hostId INTEGER, isGoogleEvent BOOLEAN, expired BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS events_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, eventId INTEGER, userId INTEGER, FOREIGN KEY (eventId) REFERENCES events (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.execute("CREATE TABLE IF NOT EXISTS courses (courseId TEXT, courseNum TEXT, courseDescription TEXT, courseSectionNum TEXT, courseName TEXT, instructorName TEXT, semester TEXT, location TEXT, credits TEXT, timeString TEXT, isActive BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS courses_to_users_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, courseId TEXT, userId INTEGER, FOREIGN KEY (courseId) REFERENCES courses (id), FOREIGN KEY (userId) REFERENCES users (id))");
        statement.execute("CREATE TABLE IF NOT EXISTS courses_to_events_mapping (id INTEGER PRIMARY KEY AUTOINCREMENT, courseId TEXT, eventId INTEGER, FOREIGN KEY (courseId) REFERENCES courses (id), FOREIGN KEY (eventId) REFERENCES events (id))");
        add_test_users(connection);
        add_test_courses(connection);
        add_test_students(connection);
        statement.close();
    }

    public static void add_test_users(Connection connection) throws SQLException {
        var check_statement = connection.prepareStatement("SELECT 1 FROM  users LIMIT 1");
        var check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            return;
        }
        check.close();

        UserRepository.createUser(connection, "bfremin32@gmail.com", "Fremin1!", "Brandon",
                "Fremin");
        UserRepository.createUser(connection, "sweill1@jhu.edu", "Fremin1!", "Sara",
                "Weill");
        UserRepository.createUser(connection, "vlepca1@jhu.edu", "Fremin1!", "Vinicius",
                "Lepca");
        UserRepository.createUser(connection, "jsech1@jhu.edu", "Fremin1!", "Justin",
                "Sech");
        UserRepository.createUser(connection, "a@gmail.com", "a", "A", "a");
        UserRepository.createUser(connection, "b@gmail.com", "b", "B", "b");
        UserRepository.createUser(connection, "c@gmail.com", "c", "C", "c");
        UserRepository.createUser(connection, "d@gmail.com", "d", "D", "d");
    }

    public static void add_test_students(Connection connection) throws SQLException {
        var check_statement = connection.prepareStatement("SELECT 1 FROM  courses_to_users_mapping LIMIT 1");
        var check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            return;
        }
        check.close();

        CourseRepository.addCourseToUser(connection, "EN.530.254(03)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "EN.530.327(01)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "EN.530.352(01)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "EN.601.231(01)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "EN.601.421(01)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "EN.601.433(01)Fa2019", 1);
        CourseRepository.addCourseToUser(connection, "AS.050.360(01)Fa2019", 1);

        CourseRepository.addCourseToUser(connection, "EN.601.421(01)Fa2019", 2);
        CourseRepository.addCourseToUser(connection, "EN.601.421(01)Fa2019", 3);
        CourseRepository.addCourseToUser(connection, "EN.601.421(01)Fa2019", 4);
    }

    public static void add_test_courses(Connection connection) throws SQLException {
        var check_statement = connection.prepareStatement("SELECT 1 FROM  courses LIMIT 1");
        var check = check_statement.executeQuery();
        if (check.next()) {
            check.close();
            return;
        }
        check.close();

        String courseName = "Manufacturing Engineering";
        String instructor = "Ronzhes, Yury";
        String courseNum = "EN.530.254";
        String courseSectionNum = "03";
        String semester = "Fa2019";
        String location = "Shaffer 300";
        String credits = "3";
        String timeString = "T 2:30 PM - 5:30 PM\nTTh 1:30 PM - 2:20 PM";
        String courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        String courseDescription = "An introduction to the grand spectrum of the manufacturing processes and " +
                "technologies used to produce metal and nonmetal components. Topics include casting, forming and " +
                "shaping, and the various processes for material removal including computer-controlled machining. " +
                "Simple joining processes and surface preparation are discussed. Economic and production aspects are " +
                "considered throughout. Open only to Mechanical Engineering and Engineering Mechanics sophomore and " +
                "junior majors; other majors only by permission of instructor. Pre-requisite: students must have " +
                "completed basic shop training and 3D printer training with the WSE Manufacturing shop before class " +
                "begins. Visit https://wsemanufacturing.jhu.edu/selfservice/ to arrange training.";
        boolean isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Introduction to Fluid Mechanics";
        instructor = "Mittal, Rajat";
        courseNum = "EN.530.327";
        courseSectionNum = "01";
        semester = "Fa2019";
        location = "Hodson 213";
        credits = "3";
        timeString = "TTh 10:30 AM - 11:45 AM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "This course introduces the fundamental mathematical tools and physical insight" +
                " necessary to approach realistic fluid flow problems in engineering systems. The topics covered" +
                " include: fluid properties, fluid statics, control volumes and surfaces, kinematics of fluids," +
                " conservation of mass, linear momentum, Bernoulli's equation and applications, dimensional" +
                " analysis, the Navier-Stokes equations, laminar and turbulent viscous flows, internal and external" +
                " flows, and lift and drag. The emphasis is on mathematical formulation, engineering applications" +
                " and problem solving.";
        isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Materials Selection";
        instructor = "Hemker, Kevin";
        courseNum = "EN.530.352";
        courseSectionNum = "01";
        semester = "Fa2019";
        location = "Hodson 210";
        credits = "4";
        timeString = "MWF 11:00 AM - 11:50 AM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "An introduction to the properties and applications of a wide variety of materials:" +
                " metals, polymers, ceramics, and composites. Considerations include availability and cost," +
                " formability, rigidity, strength, and toughness. This course is designed to facilitate sensible" +
                " materials choices so as to avoid catastrophic failures leading to the loss of life and property.";
        isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Automata & Computation Theory";
        instructor = "Kosaraju, Rao";
        courseNum = "EN.601.231";
        courseSectionNum = "01";
        semester = "Fa2019";
        location = "Shaffer 303";
        credits = "4";
        timeString = "TTh 9:00 AM - 10:15 AM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "This course is an introduction to the theory of computing. topics include design of" +
                " finite state automata, pushdown automata, linear bounded automata, Turing machines and phrase" +
                " structure grammars; correspondence between automata and grammars; computable functions, decidable" +
                " and undecidable problems, P and NP problems, NP-completeness, and randomization. Students may not" +
                " receive credit for EN.601.231/EN.600.271 and EN.601.631/EN.600.471 for the same degree.";
        isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Object Oriented Software Engineering";
        instructor = "Facchinetti, Leandro";
        courseNum = "EN.601.421";
        courseSectionNum = "01";
        semester = "Fa2019";
        location = "Mergenthaler 111";
        credits = "4";
        timeString = "MW 1:30 PM - 2:45 PM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "This course covers object-oriented software construction methodologies and their" +
                " application. The main component of the course is a large team project on a topic of your choosing." +
                " Course topics covered include object-oriented analysis and design, UML, design patterns," +
                " refactoring, program testing, code repositories, team programming, and code reviews.";
        isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Introduction to Algorithms";
        instructor = "Dinitz, Michael";
        courseNum = "EN.601.433";
        courseSectionNum = "01";
        semester = "Fa2019";
        location = "Shaffer 3";
        credits = "4";
        timeString = "TTh 12:00 PM - 1:15 PM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "This course concentrates on the design of algorithms and the rigorous analysis of" +
                " their efficiency. topics include the basic definitions of algorithmic complexity (worst case," +
                " average case); basic tools such as dynamic programming, sorting, searching, and selection;" +
                " advanced data structures and their applications (such as union-find); graph algorithms and" +
                " searching techniques such as minimum spanning trees, depth-first search, shortest paths, design" +
                " of online algorithms and competitive analysis.";
        isActive = true;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);

        courseName = "Computational Psycholinguistics";
        instructor = "Linzen, Terez";
        courseNum = "AS.050.360";
        courseSectionNum = "01";
        semester = "Sp2019";
        location = "Gilman 56";
        credits = "3";
        timeString = "TTh 12:00 PM - 1:15 PM";
        courseId = courseNum + "(" + courseSectionNum + ")" + semester;
        courseDescription = "How do we understand and produce sentences in a language we speak? How do we acquire" +
                " the knowledge that underlies this ability? Computational psycholinguistics seeks to address these" +
                " questions using a combination of two approaches: computational models, which aim to replicate the" +
                " processes that take place in the human mind; and human experiments, which are designed to test" +
                " those models. The perspective we will take in this class is that the models and experimental" +
                " paradigms do not only advance our understanding of the cognitive science, but can also help us" +
                " advance artificial intelligence and language technologies. While computational psycholinguistics" +
                " spans all levels of linguistic structure, from speech to discourse, our focus in this class will" +
                " be at the level of the sentence (syntax and semantics). The course will assume familiarity with" +
                " programming and computational modeling frameworks in cognitive science, as covered by Introduction" +
                " to Computational Cognitive Science or equivalent. Also offered as AS.050.660. An optional," +
                " hands-on lab (AS.050.361) is offered to supplement this course. It is highly recommended that" +
                " students with less extensive computational and mathematical experience register for this lab.";
        isActive = false;
        CourseRepository.createCourseInDB(connection, courseId, courseNum, courseDescription, courseSectionNum,
                courseName, instructor, semester, location, credits, timeString, isActive);
    }
}

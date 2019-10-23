# This is our README :)

## Download

You can download StudyBuddy from our Github [here](https://github.com/jhu-oose/2019-group-jhuoosers).

## Architecture

StudyBuddy is a web application. It is divided into a client and a server. It uses the tools in out toolbox.

## Server

Server: The entry point of the Server where the main method is called once the Server starts. 
The Server class uses Javalin to start a web server, sets up routing for incoming HTTP requests, 
connects to the SQLite database, and so forth.

Event: A class used to define the events that the user creates. There are three subclasses of event:
StudyEvent, AssessmentEvent, and ProjectEvent that represent different types of events that the user
can create. Events created by users are stored in a database with a unique eventID and userID to determine
what event it is and who created it.

User: Class used to represent the users who sign up for StudyBuddy. There are two types of users:
Students and Professors who have different functionality.

StudentController: Class that includes a lot of the functionality of the app. Such as creating, deleting,
and retrieving events created by the user. It is also used to make recommended events based on users' 
availability. The creation and authentication of users also takes place here.

TimeChunk: Class used to represent chunks of time when the user is busy or free to aid the 
recommendation algorithm.

##Client

index.html: The part of the Client that sets up the stylesheets and the JavaScript.

styles.css: The stylesheets for the application.

application.js: The beginning of the JavaScript section of the client that mounts the React component.

login-page.js: Displays the login page and the form in which the user inputs their user info.

signup-page.js: Displays the sign up page and the form that users use to sign up for a StudyBuddy account

RecComponent.js: Handles when the user clicks on the button to get a recommendation.

CalendarComponent.js: Displays and handles the buttons and forms that the user uses to create and delete events.

UserComponent.js: Renders the relevant information for the user.

## API Documentation

Postman Collection: The Server and the Client communicate through an API. The Postman Collection has examples and
tests for the API

## Tests

Junit: Junit Tests are used to test the different models and functions in our application.

Postman Tests: Automated tests for the server that simulate what the browser does by communicating with the server through HTTPS.
You must import the Postman collection and run the server to use these tests.

## Auxiliary files

.gitignore: Specifies which files must not be versioned (tracked) for git, because they belong on the developers machine and not with the project. For example, the .DS_Store files generated by Finder in macOS.

.travis.yml: Configuration for Travis CI that specifies how to run the tests for StudyBuddy.

build.gradle: Configuration for Gradle, which lists the libraries in which StudyBuddy depends, specifies that Server is the class that runs when the Server starts, specifies how to run unit tests, and so forth.

gradlew and gradlew.bat: Wrappers for Gradle for macOS/Linux and Windows, respectively. These wrappers install and run Gradle, simplifying the setup process.

README.md: This documentation.

settings.gradle: Gradle configuration generated by IntelliJ.







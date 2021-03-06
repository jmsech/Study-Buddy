# Welcome to Study Buddy!

## Deployment

You can find StudyBuddy deployed [here](http://oose-study-buddy.herokuapp.com/)

NOTE: The Google calendar integration button works fully in the local version of the app but is currently dissabled in the heroku deployment, this is an issue to be resolved in the next iteration. To use StudyBuddy with Google Calendar integration download the project as described below. Also, Since uploading the entire catalogue from SIS on Heroku takes more time than its timeout permits we were forced to only load 500 courses from each school. If the full catalogue is needed please operate StudyBuddy from the master branch on the repository. 

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

ApplicationController: Main controller class, which calls all of the other controllers.

- EventsController: Contains the functions for creating, rendering, deleting, and editing events.

- RecsController: Contains the functions for asking for time recommendations, calling the recommendation algorithm, and displaying the possible recommended study events.

- UserController: Contains the functions for managing users signup, login, and authentication, as well as collecting events from the user's google cal.

- BuddyRecsController: Contains the functions for asking for buddy recommendations for a certain class, calling the recommendation algorithm, and displaying the possible recommended study events. 

- CourseLinkRecsController: Contains functions for linking users between courses, and calling the recommendation algorithm.

RecommendationAlgorithm: The main algorithm for this app. It takes in a list of busy times 
from all of the users it's generating the recommendation for, as well as their desired length
of study session time, and the dates between which they're looking for a recommendation (which it gets from the recommendation form, located in the RecController) and returns a list of possible study events for the user to choose from.

TimeChunk: Class used to represent chunks of time when the user is busy or free to aid the 
recommendation algorithm.

CalendarQuickstart: Integrates the Google Calendar API into the application. Allows for the user
to add their google calendar events into their events list, so they can factor those events into the recommendation algorithm. 

## Client

index.html: The part of the Client that sets up the stylesheets and the JavaScript.

styles.css: The stylesheets for the application.

application.js: The beginning of the JavaScript section of the client that mounts the React component.

login-page.js: Displays the login page and the form in which the user inputs their user info.

signup-page.js: Displays the sign up page and the form that users use to sign up for a StudyBuddy account.

profile-page.js: Displays the user's profile page. 

RecComponent.js: Displays and handles the buttons and forms the user uses to get a time recommendation.

BuddyRecComponents.js: Displays and handles the buttons and forms the user uses to get a buddy recommendation.

CalendarComponents.js: Displays and handles the buttons and forms that the user uses to create and delete events.

HeaderComponents.js: Displays and handles the buttons on the site header (both home page and profile page).

ProfileComponents.js: Displays and handles everything on the user's profile page: friends, courses, and personal info.

AddCourseComponents.js: Displays and handles the buttons and cards displaying the users courses on their profile.

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

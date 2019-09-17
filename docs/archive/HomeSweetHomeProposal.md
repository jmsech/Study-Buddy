# Project Proposal

# HomeSweetHome

# Elevator Pitch
Finding a place to live is often a very long and tedious process. There are many options, with even more different factors 
to consider before making a choice. HomeSweetHome understands your preferences when looking for housing and uses a clever algorithm to 
present you with the best options, so you can maximize your efficiency and make a more informed decision.

# Problem
There are too many factors and too many options involved when we search for a place to live. It is hard to effectively analyze 
all of these inputs and make an informed decision, and we might end up with a bad one.

## Introduction to Domain
This domain is sufficiently well known, so that no further discussion is necessary in this section at the moment.

# Solution
One of the main supporting theories for this solution is the Paradox of Choice, described by the psychologist Barry Schwartz. 
The main idea in this theory is that, by eliminating consumer choices, you can reduce their anxiety when buying.  
Building onto this idea, we can imagine how this effect is intensified for a big decision such as choosing a new home. This solution can 
help minimize the stress of choosing a new home by using algorithms to compile large amounts of information into a couple of 
high quality options, while also saving time for the user. 

## Architecture Overview

- Web app for the user side
- Web app for people/organizations to add their properties
- Web server

## Features

- Algorithm to select best fits from the database based on the users preferences
- Questionnaire that the user fills out to determine their preferences
- Map showing the available properties (user can toggle between all options and best options)
- Profiles for each property, structured in a standard way to show a concise set of data
- Ability for users to rate properties and look at other property ratings
- Ability for property owners to add their properties
- Potential idea: machine learning tools to predict what will be more valued by the user (for example, proximity to a shopping mall)

## Wireframes

**Questionnaire page**
![](hsh_questionnaire.png)

**Map and home information page**
![](hsh_map.png)

## User Stories
- As a person trying to find a new home, I want to fill out the questionnaire so that I get custom suggestions.
- As a person trying to find a new home, I want to search for properties on a map based on the filters I selected, as well 
as find relevant and consistent information for each of the properties.
- As a person who's used the platform, I want to leave a review of the property where I live so that other users can make 
more informed decisions.
- As a property owner, I want to upload my property to the platform so that it's more easily available to consumers.

# Viability

## Hardware
- No hardware necessary other than a computer to do thr programming. All materials available.

## APIs
- The Google Maps API can be used for the map in the platform. (further research is necessary to understand what the API offers)

## Tools

- IntelliJ - for general software development
- Google Chrome - browser of choice for web development
- HTML and CSS - frontend
- Java - server side language
- SQLite - database
- JavaScript - client  
(Further research needs to be done to determine exactly which tools will be needed)

## Proof of Concept
Currently, there is no clear proof of concept, apart from the other existing home searching platforms. 
What needs to proved is that we can create an effective algorithm based on the user's preferences, which will need further 
study to be determined.

# Difficulty
This problem is sufficiently difficult because it requires that we develop this new algorithm to pick the best home options, 
while also presenting this data in a clear way and making it easy for the user to close the deal quickly.

# Market Research

## Users
This is a product that can have a really broad user base, as moving into a new home is something that most people will go through 
during their lives. A specific market that can be interesting to analyze is the college students, as they might move more frequently during 
and shortly after college, for internships, full time jobs, roommate issues, etc.

## Competition
There are several platforms for home searching in the market. A few examples are Zillow, Trulia, Homefinder, etc.
Our main competitive advantage would be the optimization algorithm.

# Roadmap
https://github.com/jhu-oose/2019-group-jhuoosers/projects/1/

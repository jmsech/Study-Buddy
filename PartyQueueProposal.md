# Project Proposal

# Party Queue

# Elevator Pitch
People at social events are always fighting over what songs to play. Party Queue is a centralized "multi-player" 
playlist that controls the music selection of your party. One person plugs in their phone to a speaker and anyone can join
theParty can add their favorite songs to the queue. Everyone else can upvote or downvote any song so the song order 
is determined by a simple vote of the group.

# Problem

People are always trying to "hijack the aux chord" to play their own music in group settings. No one can agree what music
to play and everyone shames the person in control if the group thinks they have "bad taste". 
 
## Introduction to Domain

Sharing music has always been an essential way in how we socialize. Streaming services like Spotify and Apple Music have 
recently revolutionized this process and embarked on a new frontier in the world of consumer music. We are working in a 
domain that is vastly know and enjoyed by a broad audience.

To specify:
"AUX" - short for auxiliary input is a standard way of connecting one's phone directly to a speaker system to play music.
playlist / queue - used interchangeably in this proposal, an ordered compilation of songs that will play in order from
                   top to bottom. 


# Solution

A party's host will connect their phone to a speaker to play music and hit "play" on any song/pre-made playlist they want. 
Anyone at the event can use Party Queue to join the host's playlist and add as many songs as they like to the bottom of the queue. 
Everyone at the party will see a live-updating queue as people add their own songs, and everyone can vote (either up or down) 
on other songs in the queue. Party Queue will automatically reorder the songs so now the fan-favorites will play first and 
the constant debate over music selection is over once and for all.

## Architecture Overview

Mobile App
Integrating with the Spotify and Apple Music APIs

## Features

- Chose to "Start Party" or "Join Party"
- To join a party, you can either enter a Party Code or Find Friends
- Sign in to your Apple Music or Spotify account to access your own music library
- Search for any song and add it to the bottom of the party queue 
- View the current playlist and "upvote" or "downvote" any song in the queue
- The song at the top of the queue will play as soon as the current song finishes

## Wireframes

**Start Screen**

![](party_queue_start_screen.png)


**Main Queue**

![](party_queue_queue.png)

**Search/Add Song Tab**

![](party_queue_search.png)

## User Stories

As my friends' go-to DJ, I want to make playlists that everyone will enjoy so people will stop berating me with song requests.
Party Queue makes it easy! I can plug in my phone to my speaker and start playing music as normal. My friends can easily 
join my playlist and add any song they like, if others agree that it's a good song, they can upvote it so it plays sooner. 
If someone is opposed to any of the songs in the queue they can just as easily downvote it. Now the order of the songs is
entirely decided by the crowd and I no longer need to worry about trying to please everyone!


As an avid dance enthusiast I want my favorite songs to play at parties so I can jam out and have a great time. I love going
to parties and clubs that use Party Queue, it makes it so easy to request songs! Often times I can even make friends around 
me and get them to upvote my songs so we can all dance together!

# Viability

## Hardware

I have a Windows computer but an iPhone. This my be an issue if I want to develop a mobile app. 

## APIs

Integration with Spotify and/or Apple Music APIs.
Both Spotify and Apple Music API includes all necessary access for Party Queue as they provide access to both services'
full resources (searchable music library, creating playlists, etc.).

## Tools

- React Native
- Java

## Proof of Concept

Other than the potential Windows/iPhone issue, it is clear this project is technically viable because Spotify and Apple Music 
have built the foundation of what we will need (a music library) and the project is essentially a feature extension.

# Difficulty

I would like to evaluate this with Staff as I do not have a gauge on what is difficult and what is not. I would assume
the integration with Spotify/Apple Music APIs as well as connecting multiple users in one "party" and having live updates
 to the queue will prove the most difficult parts while the rest of the functionality is relatively straightforward.
 
 As far as technical tasks go we will have to access and organize the music data (artist, album, song info and cover art),
 build and maintain a mutable queue that can be accessed by anyone with the Party Code. We also must connect
 to an individual user's unique music library (so they can search their own songs) while simultaneously connecting them
 to the other users in the party for group listening. 

# Market Research

## Users
- Almost 250 million people use music Spotify and Apple Music on a regular basis. This is a great opportunity to introduce 
a new feature to help people more effectively share music in a group setting to a platform millions of people are very familiar with. 
- Party Queue is also very marketable to places like night clubs, college cafeterias, and any other public place that plays
music in the background. (How great would it be to go shopping and be able to have your own music play!)

## Competition
Spotify and Apple Music--two of the largest music streaming services in the world--do not currently have a cross-user 
collaborative feature like Party Queue. The main obstacle will be introducing Party Queue's novel feature to a 
user base that is already familiar with the concept of playlists and queues but trying to expand their perception of how
they can use playlists in a more social manner. Party Queue is different from these existing services because of this social
sharing concept that is becoming more and more prevalent with the rise of social media. 


# Roadmap

https://github.com/jhu-oose/2019-student-jmsech/projects/2

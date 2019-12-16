# Final Report

**⚠️  Remember to also fill in the individual final reports for each group member at `https://github.com/jhu-oose/2019-student-<identifier>/blob/master/final-report.md`.**

**⚠️  You don’t need to do anything special to submit this final report—it’ll be collected along with Iteration 6.**

# Revisiting the Project Proposal & Design

<!--
How did the Project Proposal & Design documents help you develop your project?

What changed in your project since you wrote the initial version of those documents?
--> 
A lot has changed since our initial design. Though we kept the big picture, which was a Time Scheduling tool for students, our initial plan was very broad: time scheduling, professor meeting, study group finding, document uploading...you name it. 
We consolidated our goals into two main ideas: find time to schedule a meeting with a group of people you know, and find a time to meet new friends with compatible schedules for a course you specify. 
Around these two goals we were able to consolidate the most important features: integrating with SIS and Google Calendar, adding courses to your profile, so the algorithm can match you with others in the course, adding friends to your profile to prioritize the people you like to work with, adding public events, important deadlines, and lecture meetings to the events page.
We decided not to implement specifying types of users, uploading course documents, and other features that weren't directly related to our goal of recommending study group times and people. By focusing our app on these two goals, we were able to deliver a finished product that was polished, focused, and easy to use!


# Challenges & Victories

<!--
In software engineering things rarely go as planned: tools don’t work as we expect, deadlines aren’t met, debugging sessions run longer than we hoped for, and so forth.

What were some of the biggest challenges you found when developing your project? How did you overcome them?
-->
There was a steep learning curve getting this project started. All of us had just taken Intermediate and Data Structures previously, so while we were well equipped on the backend, the frontend was a lot to get started, especially at the beginning. 
The hardest part of this project was continually just getting the frontend to work, dealing with the javascript and server calls.  

Another issue we had unfortunately was people pushing things to master that shouldn't have been pushed, which often set back the project by a few days and was really frustrating at times. 

# Experience with Tools

<!--
Which tools did you learn to like? Why?

Which tools did you learn to dislike? Why? And what other tools would you have replaced them with if you were to start all over again?
-->
We really liked using Materialze for CSS. It made our app look polished and user friendly, and was *usually* pretty easy to implement. 

We don't like javascript. We felt a little underprepared in terms of instruction on the topic, and found it was difficult to work with because of its lack of helpful error messages and its ability to allow you to do anything meant we never knew what was going wrong when things didn't work.
I'm not sure javascript is avoidable, but at this point we would probably be able to get a lot more done if we took this class all over again.

We also seemed to have a lot of problems getting intelliJ to configure properly with Gradle.
We constantly just had to reclone the repo whenever the configurations got messed up and everything stopped working.
We aren't sure if this is an intelliJ problem or a Gradle problem, but I'd love to know the reason behind this issue.

Once we figured out how the database SQL calls worked, we really enjoyed this. 
It was much easier than previous experiences in Intermediate or Data Structures where all your data has to be stored forever in fancy memory allocation structures.
Learning how to do relational databases was a massive gamechanger, and SQL has some pretty useful calls that make working with them super easy. 

# Iteration 7 & Beyond

<!--
Where would you take your project from here? What features would you add to make your application even more awesome? How would you prioritize that work?

Update the project board with tasks for a hypothetical Iteration 7.
-->
Some ideas we put on the backburner but would be cool for the future is having a specified user type of instructor, who could use the app to schedule TA office hours, staff meetings, and uniquely schedule deadlines and exams. 
We'd also like to implement having a distinction between public and privated events, where anyone who is in the associated course could see any public study event happening (currently, all events (besides lectures) are by invite only). 


# A Final Message to Your Advisor

<!--
What did you like in working with them?

What do you think they need to improve?

And anything else you’d like to say.
-->

Thank you for being generous and understanding on our weekly iterations, helping us to see where improvements could be made and giving us credit when we made those improvements from week to week. 

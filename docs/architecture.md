## Architecture
Most of the tools will be from the Toolbox.
We will use the Google Calendar API to view a user's current events and use that to schedule other events, 
such as study sessions. We wil need to use methods such as getting all the events, searching for busy times, creating and 
deleting events. We also need to integrate with SIS to create classes that can be joined by students. For that, we can use 
the SIS API.
When a user creates their profile, they can connect with their Google Calendar (done through the Calendar API). They can 
also pick classes that are added from the SIS database. The app can compare different schedules and suggest events, which
can then be added to the user's calendar.
package com.studybuddy.models;

import java.util.List;

public class User implements Cloneable{

    private Integer id;
    private String name;
    private Calendar cal;
    private Course[] courseList;
    private List<Event> pendingEvents;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.cal = new Calendar();
    }

    public void createEvent(int id, String descr, LocalDateTime StartTime, LocalDateTime EndTime, List<User> inviteList = new ArrayList()) {
        ArrayList<User> hosts = new ArrayList(this);
        // User can create a study event
        userEvent = new Event(StartTime, EndTime, descr, id, hosts);
        //add it to their own calendar
        this.attendEvent(userEvent);  //do we want to automatically attend events we create? // TODO (optional)
        //invite the list of other buddies
        this.inviteBuddies(userEvent, inviteList);
    }

    public void populateCalendar(List<Event> usersEvents) {
        cal.populate(usersEvents);
    }

    public void attendStudyEvent(StudyEvent e) {
        // they want to attend, so add to calendar
        this.cal.addEvent(e);
        //add user to the event's attending list
        e.addUser(this);
    }

    public void declineStudyEvent(StudyEvent e) {
        // just remove from personal pending list
        this.pendingEvents.remove(e);
        //remove from event's pending list
        e.removeFromPending(this);
    }

    public void unattendStudyEvent(StudyEvent e) {
        //if they change their mind & don't want to go anymore
        this.call.removeEvent(e);
        e.removeUser(this);
        //TODO : (optional) have  a 'trash' list of events you've unattended?
    }

    public List<Event> getRecommendations() {
        //  TODO big algo
        return null;
    }

    public void inviteBuddies(StudyEvent e, List<User> l) {
        ArrayList<User> hosts = new ArrayList(this);
        //invite everyone in the list
        for (var user:l) {
            e.invite(user, hosts); //invite adds to pending
        }
    }

    public void recieveInvite(Event e) {
        //if someone sent you an invite
        this.pendingEvents.append(e);
        //ask user if they want to go
        boolean going = messagePopUp("You've been invited to " e.getDescription() ", would you like to attend?");
        if (going) {
            this.attendEvent(e);
        }
        else {
            this.declineEvent(e);
        }
    }





    //getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getCal() {
        return cal;
    }

    public Course[] getCourseList() {
        return courseList;
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    public void setCourseList(Course[] courseList) {
        this.courseList = courseList;
    }
}
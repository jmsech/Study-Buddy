package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Cloneable {

    private long id;
    private String name;
    protected Calendar cal;
    private Course[] courseList;
    private List<Event> pendingEvents;

    /** Constructor for User
     *
     * @param id - Unique 12 character int which identifies student (11xxxxxxxxxxx)
     * @param name - String representation of user name
     */
    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.cal = new Calendar(this, new ArrayList<Event>());
    }

    /** Add events to Calendar
     *
     * @param usersEvents - events that user is adding to their calendar.
     */
    public void addToCalendar(List<Event> usersEvents) {
        // TODO
        // cal.add(usersEvents);
    }

    /** Remove events from Calendar
     *
     * @param usersEvents - Events to be removed from calendar
     */
    public void removeFromCalendar(List<Event> usersEvents) {
        // TODO
        // cal.remove(usersEvents);
    }

    /** Get list of events that user has on their calendar
     *
     * @return the events in the users calendar
     */
    public List<Event> getEvents() {
        return this.cal.getEvents();
    }

    /** Create a StudyEvent
     *
     * @param title - Title of the event
     * @param description - Description of event
     * @param StartTime - Time which the event starts. LocalDateTime will be set to EST
     * @param EndTime - Time which the event ends. LocalDateTime will be set to EST
     * @param inviteList - List of Users to invite to event.
     */
//    public void createStudyEvent(String title, LocalDateTime StartTime, LocalDateTime EndTime, String location, String description,
//                                 List<User> inviteList, double importance) {
//        // TODO - FIX THIS
//        // int id = generateID();
//        ArrayList<User> hosts = new ArrayList<>();
//        hosts.add(this);
//        // User can create a study event
//        var userEvent = new StudyEvent(id, title, StartTime, EndTime, location, description, hosts, inviteList, importance);
//        //add it to their own calendar
//        this.attendStudyEvent(userEvent);  //do we want to automatically attend events we create? // TODO (optional)
//        //invite the list of other buddies
//        this.inviteBuddies(userEvent, inviteList);
//    }

    /** User decides that they will attend a StudyEvent
     *
     * @param e - The event which the student will attend
     */
    public void attendStudyEvent(Event e) {
        // they want to attend, so add to calendar
        this.cal.addEvent(e);
        //add user to the event's attending list
        // TODO
        //e.addUser(this);
    }

    /** User decids that they will no longer be attending a StudyEvent
     *
     * @param e - The event which the student will no longer be attending
     */
    public void unattendStudyEvent(Event e) {
        //if they change their mind & don't want to go anymore
        this.cal.removeEvent(e);
        // TODO
        // e.removeUser(this);
        //TODO : (optional) have  a 'trash' list of events you've unattended?
    }

    /** User decides that he will not attend StudyEvent
     *
     * @param e -User decide to not attend event.
     */
    public void declineStudyEvent(Event e) {
        // just remove from personal pending list
        this.pendingEvents.remove(e);
        // TODO remove from event's pending list
        // e.removeFromPending(this);
    }

    /** Send user a message to ask if they will be attending the new event.
     *
      * @param e - Event which user may or may not attend
     */
    public void receiveInvite(Event e) {
        //if someone sent you an invite
        this.pendingEvents.add(e);
        //ask user if they want to go
        // TODO Fix messagePopUp to be able to add items to a pending list
//        boolean going = messagePopUp("You've been invited to " + e.getDescription() + ", would you like to attend?");
//        if (going) {
//            this.attendStudyEvent(e);
//        }
//        else {
//            this.declineStudyEvent(e);
//        }
    }

    /** Invite a list of users to StudyEvent
     *
     * @param e - Event which users are being invited to
     * @param l - list of users to invite event to
     */
    public void inviteBuddies(Event e, List<User> l) {
        ArrayList<User> hosts = new ArrayList<>();
        hosts.add(this);
        //invite everyone in the list
        for (var user : l) {
            // TODO
            // e.invite(user, hosts); //invite adds to pending
        }
    }

    // TODO Iron out kinks in functions below

    public List<Event> getRecommendations() {
        //  TODO big algo
        return null;
    }












    //getters
    public long getId() {
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
    public void setId(long id) {
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
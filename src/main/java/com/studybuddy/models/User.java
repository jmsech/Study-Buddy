package com.studybuddy.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Cloneable{

    private Integer id;
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
        this.cal = new Calendar(this, new HashSet<Event>());
    }

    /** Create a StudyEvent
     *
     * @param id - Unique 12 character int which identifies Event (22xxxxxxxxxx)
     * @param descr - Description of event
     * @param StartTime - Time which the event starts. LocalDateTime will be set to EST
     * @param EndTime - Time which the event ends. LocalDateTime will be set to EST
     * @param inviteList - List of Users to invite to event.
     */
    public void createStudyEvent(String descr, LocalDateTime StartTime, LocalDateTime EndTime, List<User> inviteList) {
        int id = generateID();
        ArrayList<User> hosts = new ArrayList<>();
        hosts.add(this);
        // User can create a study event
        var userEvent = new StudyEvent(StartTime, EndTime, descr, id, hosts);
        //add it to their own calendar
        this.attendStudyEvent(userEvent);  //do we want to automatically attend events we create? // TODO (optional)
        //invite the list of other buddies
        this.inviteBuddies(userEvent, inviteList);
    }

    /** User decides that they will attend a StudyEvent
     *
     * @param e - The event which the student will attend
     */
    public void attendStudyEvent(StudyEvent e) {
        // they want to attend, so add to calendar
        this.cal.addEvent(e);
        //add user to the event's attending list
        e.addUser(this);
    }

    /** User decids that they will no longer be attending a StudyEvent
     *
     * @param e - The event which the student will no longer be attending
     */
    public void unattendStudyEvent(StudyEvent e) {
        //if they change their mind & don't want to go anymore
        this.cal.removeEvent(e);
        e.removeUser(this);
        //TODO : (optional) have  a 'trash' list of events you've unattended?
    }

    /** User decides that he will not attend StudyEvent
     *
     * @param e -User decide to not attend event.
     */
    public void declineStudyEvent(StudyEvent e) {
        // just remove from personal pending list
        this.pendingEvents.remove(e);
        //remove from event's pending list
        e.removeFromPending(this);
    }

    /** Send user a message to ask if they will be attending the new event.
     *
      * @param e - Event which user may or may not attend
     */
    public void receiveInvite(StudyEvent e) {
        //if someone sent you an invite
        this.pendingEvents.add(e);
        //ask user if they want to go
        // TODO Fix messagePopUp to be able to add items to a pending list
        boolean going = messagePopUp("You've been invited to " + e.getDescription() + ", would you like to attend?");
        if (going) {
            this.attendStudyEvent(e);
        }
        else {
            this.declineStudyEvent(e);
        }
    }


    public void populateCalendar(List<Event> usersEvents) {
        cal.populate(usersEvents);
    }

    public List<Event> getRecommendations() {
        //  TODO big algo
        return null;
    }

    public void inviteBuddies(StudyEvent e, List<User> l) {
        ArrayList<User> hosts = new ArrayList<>();
        hosts.add(this);
        //invite everyone in the list
        for (var user:l) {
            e.invite(user, hosts); //invite adds to pending
        }
    }

    public Set<Event> getEvents() {
        return this.cal.getEvents();
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
package com.studybuddy.models;

import java.util.Set;

public class Calendar {

    // This calendar's owner
    private User owner;
    // The set of events in this calendar
    private Set<Event> events;

    /**
     * Constructor based on a set of events.
     * @param events the set of events to add to the calendar
     * @param user the owner of this calendar
     */
    public Calendar(User user, Set<Event> events) {
        this.owner = user;
        this.events = events;
    }

    /**
     * Returns the owner of the calendar.
     * @return owner of the calendar
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set an owner for the calendar.
     * @param owner owner of the calendar
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Get set of all events in the calendar.
     * @return the set of all events
     */
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Sets a set of events as the events in the calendar.
     * @param events the set of events for the calendar
     */
    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    /**
     * Add a specific event to this calendar.
     * @param event the event to be added
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }

    /**
     * Remove an event from the calendar
     * @param event the event to be removed
     * @return true if successful, false otherwise
     */
    public boolean removeEvent(Event event) {
        return this.events.remove(event);
    }
}

package com.studybuddy.models;

import java.time.LocalDateTime;

public class TimeChunk {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isOverlapping(TimeChunk comp) {
        return ((this.endTime.isAfter(comp.getStartTime()) || this.endTime.isEqual(comp.getStartTime())) && ( this.endTime.isBefore(comp.getEndTime())) || this.endTime.isEqual(comp.getEndTime()) ) ||
                ((this.startTime.isAfter(comp.getStartTime()) || this.startTime.isEqual(comp.getStartTime())) && (this.endTime.isEqual(comp.getStartTime()) || this.startTime.isBefore(comp.getEndTime())));
    }

    public void merge(TimeChunk newChunk) {
        if (this.endTime.isBefore(newChunk.getEndTime())) {
            this.endTime = newChunk.getEndTime();
        }
        if (this.startTime.isAfter(newChunk.getStartTime())) {
            this.startTime = newChunk.getStartTime();
        }
    }

}

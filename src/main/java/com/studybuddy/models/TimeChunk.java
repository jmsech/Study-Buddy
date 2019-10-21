package com.studybuddy.models;

import java.time.LocalDateTime;

public class TimeChunk {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

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

    boolean isOverlapping(TimeChunk comp) {
        if ((this.endTime.compareTo(comp.getStartTime()) >= 0 && this.endTime.compareTo(comp.getEndTime()) <= 0) ||
                (this.startTime.compareTo(comp.getStartTime()) >= 0 && this.startTime.compareTo(comp.getEndTime()) <= 0)) {
            return true;
        } else {
            return false;
        }
    }

    void merge(TimeChunk newChunk) {
        if (this.endTime.compareTo(newChunk.getEndTime()) < 0) {
            this.endTime = newChunk.getEndTime();
        }
        if (this.startTime.compareTo(newChunk.getStartTime()) > 0) {
            this.startTime = newChunk.getStartTime();
        }
    }

}

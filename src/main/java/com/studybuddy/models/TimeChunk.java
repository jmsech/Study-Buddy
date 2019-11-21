package com.studybuddy.models;

import java.time.LocalDateTime;

public class TimeChunk {

    public double DEFAULT_WEIGHT = 2;
    public double MIN_WEIGHT = 1;
    public double MAX_WEIGHT = 3;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double weight;

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weight = DEFAULT_WEIGHT;
    }

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime, double d) {
        this.startTime = startTime;
        this.endTime = endTime;
        if ((MIN_WEIGHT < d) && (MAX_WEIGHT > d)) {
            this.weight = d;
        } else {
            this.weight = DEFAULT_WEIGHT;
        }
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

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double d) {
        if ((MIN_WEIGHT < d) && (MAX_WEIGHT > d)) {
            this.weight = d;
        } else {
            this.weight = DEFAULT_WEIGHT;
        }
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

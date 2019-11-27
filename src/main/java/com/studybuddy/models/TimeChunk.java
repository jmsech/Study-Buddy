package com.studybuddy.models;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeChunk {

    public static double DEFAULT_WEIGHT = 2;
    public static double MIN_WEIGHT = 1;
    public static double MAX_WEIGHT = 3;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double weight;
    private List<Integer> userIDs;

    public TimeChunk(TimeChunk other) {
        this.startTime = other.getStartTime();
        this.endTime = other.getEndTime();
        this.weight = other.getWeight();
        this.userIDs = List.copyOf(other.userIDs);
    }

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weight = DEFAULT_WEIGHT;
        this.userIDs = new ArrayList<>();
    }

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime, double d) {
        this.startTime = startTime;
        this.endTime = endTime;
        if ((MIN_WEIGHT < d) && (MAX_WEIGHT > d)) {
            this.weight = d;
        } else {
            this.weight = DEFAULT_WEIGHT;
        }
        this.userIDs = new ArrayList<>();
    }

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime, double d, int id) {
        this.startTime = startTime;
        this.endTime = endTime;
        if ((MIN_WEIGHT < d) && (MAX_WEIGHT > d)) {
            this.weight = d;
        } else {
            this.weight = DEFAULT_WEIGHT;
        }
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        this.userIDs = ids;
    }

    public TimeChunk(LocalDateTime startTime, LocalDateTime endTime, double d, List<Integer> u) {
        this.startTime = startTime;
        this.endTime = endTime;
        if ((MIN_WEIGHT < d) && (MAX_WEIGHT > d)) {
            this.weight = d;
        } else {
            this.weight = DEFAULT_WEIGHT;
        }
        this.userIDs = u;
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

    public List<Integer> getUserIDs() {
        return this.userIDs;
    }

    public void setUserIDs(List<Integer> list) {
        this.userIDs = list;
    }

    public boolean isOverlapping(TimeChunk comp) {
        LocalDateTime s1 = this.startTime;
        LocalDateTime e1 = this.endTime;
        LocalDateTime s2 = comp.startTime;
        LocalDateTime e2 = comp.endTime;
        return ((s1.toEpochSecond(ZoneOffset.UTC) <= s2.toEpochSecond(ZoneOffset.UTC) &&
                        s2.toEpochSecond(ZoneOffset.UTC) <= e1.toEpochSecond(ZoneOffset.UTC)) ||
                (s1.toEpochSecond(ZoneOffset.UTC) <= e2.toEpochSecond(ZoneOffset.UTC) &&
                        e2.toEpochSecond(ZoneOffset.UTC) <= e1.toEpochSecond(ZoneOffset.UTC)));
    }

    public void merge(TimeChunk newChunk) {
        if (this.endTime.isBefore(newChunk.getEndTime())) {
            this.endTime = newChunk.getEndTime();
        }
        if (this.startTime.isAfter(newChunk.getStartTime())) {
            this.startTime = newChunk.getStartTime();
        }
    }

    public static TimeChunk nearest15(TimeChunk t, long FIFTEEN_MINUTES) {

        long startSec = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long endSec = t.getEndTime().toEpochSecond(ZoneOffset.UTC);
        long mod = startSec % (FIFTEEN_MINUTES);

        if (mod <= FIFTEEN_MINUTES / 2.0) {
            startSec = startSec - mod;
            endSec = endSec - mod;
        } else {
            startSec = startSec - mod + (FIFTEEN_MINUTES);
            endSec = endSec - mod + (FIFTEEN_MINUTES);
        }

        t.setStartTime(makeTime(startSec));
        t.setEndTime(makeTime(endSec));
        return t;
    }

    public static LocalDateTime makeTime(long t) {
        return LocalDateTime.ofEpochSecond(t,0,ZoneOffset.ofHours(0));
    }

    // FIXME Should we sort by weight or numPeopleAvailable or somethingElse?
    public static class TimeChunkComparator implements Comparator<TimeChunk> {
        @Override
        public int compare(TimeChunk t1, TimeChunk t2) {
            long start1 = t1.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long start2 = t2.getStartTime().toEpochSecond(ZoneOffset.UTC);
            if (start1 < start2) { return -1; }
            else if (start1 == start2) {return 0; }
            else {return 1; }
        }
    }

    public static class TimeChunkWeightComparator implements Comparator<TimeChunk>{
        @Override
        public int compare(TimeChunk t1, TimeChunk t2) {
            double start1 = t1.getWeight();
            double start2 = t2.getWeight();
            if (start1 < start2) { return 1; }
            else if (start1 == start2) {return 0; }
            else {return -1; }
        }
    }
}

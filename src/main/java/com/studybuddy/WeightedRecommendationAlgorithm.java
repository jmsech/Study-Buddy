package com.studybuddy;

import com.studybuddy.models.TimeChunk;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class WeightedRecommendationAlgorithm {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int COMPRESSION_FACTOR = 5;
    private static final int SECONDS_PER_MINUTE = 60 * COMPRESSION_FACTOR; //seconds/60 = minutes
    private static final long SECONDS_PER_DAY = 86400;
    private static final long SECONDS_OF_SLEEP = 28800;
    private static final int MINUTES_PER_HOUR = (int) (SECONDS_PER_DAY/SECONDS_PER_MINUTE/24);
    private static final long MINUTES_PER_DAY = SECONDS_PER_DAY/SECONDS_PER_MINUTE;
    private static final long MINUTES_OF_SLEEP = SECONDS_OF_SLEEP/SECONDS_PER_MINUTE;
    private static final long FIFTEEN_MINUTES = SECONDS_PER_MINUTE * MINUTES_PER_HOUR / 4;

    private static final double INITIAL_PROXIMITY_WEIGHT = 1;
    private static final double PROXIMITY_RATIO = 0.5;
    private static final double BASELINE_VALUE = 0.1;
    private static final int OFF_BY_ONE = 1; // Accounting for off-by-one errors

    private static final double SLEEP_WEIGHT = -20; //FIXME?
    private static final double HOST_UNAVAILABLE_WEIGHT = -1000;  //FIXME?

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RECOMMENDATION ALGORITHM ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, List<List<TimeChunk>> busyTimes, double fraction, int numRecs) {
        ArrayList<TimeChunk> arr = new ArrayList<>();
        return makeRecommendation(start,end,busyTimes,arr,new ArrayList<>(),fraction,numRecs);
    }

    public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, List<List<TimeChunk>> busyTimes, List<TimeChunk> host, List<Integer> alwaysFree, double fraction, int numRecs) {

        // Initialize array of doubles to hold availability of individuals
        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/SECONDS_PER_MINUTE;
        double[] available = new double[lengthInMinutes];

        for (int i = 0; i < lengthInMinutes; i++) { available[i] = BASELINE_VALUE; }

        // Time of interval shorter than length of studying time
        if (lengthInMinutes < (int) (fraction * MINUTES_PER_HOUR)) { return new ArrayList<>();}
        if (lengthInMinutes == (int) (fraction * MINUTES_PER_HOUR)) {
            ArrayList<TimeChunk> arr = new ArrayList<>();
            arr.add(TimeChunk.nearest15(new TimeChunk(
                    TimeChunk.makeTime(startSec),
                    TimeChunk.makeTime(endSec)),
                    FIFTEEN_MINUTES
            ));
            return arr;
        }

        // Make one cumulative list of when people are unavailable
        List<TimeChunk> unavailable = new ArrayList<>();
        for (var userTimeChunkList : busyTimes) {
            unavailable.addAll(userTimeChunkList);
        }

        // Use time chunks to popoluate availability array
        for (TimeChunk t : unavailable) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes;}
            if (f >= lengthInMinutes) {f = lengthInMinutes;}

            for (int i = s; i < f; i++) { available[i] = available[i] - t.getWeight(); }
        }

        // Find weight of least available time
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < lengthInMinutes; i++) {
            if (available[i] < min) { min = available[i]; }
        }

        // Make all values positive
        if (min <= 0) {
            for (int i = 0; i < lengthInMinutes; i++) {
                available[i] += -min;
            }
        }

        // Initialize sleep start time // FIXME This should probably become a constant
        long sleepStart = (LocalDateTime.of(2020,1,1,0,0)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / SECONDS_PER_MINUTE;

        for (TimeChunk t : host) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes;}
            if (f >= lengthInMinutes) {f = lengthInMinutes;}

            for (int i = s; i < f; i++) { available[i] = HOST_UNAVAILABLE_WEIGHT; }

            // Add proximity weights to favor recommending events before host event
            int lower = s - MINUTES_PER_HOUR - 1;
            double proximityWeight = INITIAL_PROXIMITY_WEIGHT;
            for (int i = s - 1; i >= 0 && i >= lower; i--) {
                available[i] += proximityWeight;
                proximityWeight *= PROXIMITY_RATIO;
            }

            // Add proximity weights to favor recommending events after host event
            int upper = f + MINUTES_PER_HOUR;
            proximityWeight = INITIAL_PROXIMITY_WEIGHT;
            for (int i = f; i < lengthInMinutes && i <= upper; i++) {
                available[i] += proximityWeight;
                proximityWeight *= PROXIMITY_RATIO;
            }
        }

        // Give negative value to times when people are sleeping
        int len = available.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                available[i] = SLEEP_WEIGHT;
            }
        }

        ArrayList<TimeChunk> recommendations = new ArrayList<>();
        int lengthStudy = (int) (fraction*MINUTES_PER_HOUR) - OFF_BY_ONE; //Accounting for off by one errors
        double[] chunkValues;

        for (int n = 0; n < numRecs; n++) {
            // Initialize array of values of Studying Chunks
            chunkValues = new double[lengthInMinutes - lengthStudy + 1];

            // Calculate first value of first TimeChunk
            for (int i = 0; i < lengthStudy; i++) {
                chunkValues[0] += available[i];
            }

            // Calculate values of the rest of the TimeChunks
            for (int i = 1; i < chunkValues.length; i++) {
                chunkValues[i] = chunkValues[i-1] - available[i-1] + available[i+lengthStudy-1];
            }

            // Find most optimal study time of length
            double max = Double.NEGATIVE_INFINITY;
            int startIndex = 0;
            for (int i = 0; i < chunkValues.length; i++) {
                if (chunkValues[i] > max) {
                    max = chunkValues[i];
                    startIndex = i;
                }
            }

            if (max >= 0) {
                int endIndex = startIndex + lengthStudy + OFF_BY_ONE;
                // Add TimeChunk starting at this time
                TimeChunk chunk = new TimeChunk(
                        TimeChunk.makeTime(startSec + (startIndex) * SECONDS_PER_MINUTE),
                        TimeChunk.makeTime(startSec + (endIndex) * SECONDS_PER_MINUTE),
                        max
                );

                // Determine who can attend event entirety of
                List<Integer> attendees = new ArrayList<>();
                TimeChunk comparisonChunk = new TimeChunk(
                        TimeChunk.makeTime(startSec + (startIndex + OFF_BY_ONE) * SECONDS_PER_MINUTE),
                        TimeChunk.makeTime(startSec + (endIndex - OFF_BY_ONE) * SECONDS_PER_MINUTE),
                        max
                );
                for (var user : busyTimes) {
                    int id = user.get(0).getUserIDs().get(0);
                    for (var c : user) {
                        if (c.isOverlapping(comparisonChunk)) {
                            id = Integer.MIN_VALUE;
                            break;
                        }
                    }
                    if (id != Integer.MIN_VALUE) {
                        attendees.add(id);
                    }
                }
                attendees.addAll(alwaysFree);
                chunk.setUserIDs(attendees);

                // Round chunk to nearest 15 minute interval
                chunk = TimeChunk.nearest15(chunk, FIFTEEN_MINUTES);

                recommendations.add(chunk);
                startIndex = (int) ((chunk.getStartTime().toEpochSecond(ZoneOffset.UTC) - startSec)/SECONDS_PER_MINUTE);
                endIndex = startIndex + lengthStudy;
                if (startIndex < 0) {startIndex = 0;} // Don't access indices out of range.
                if (endIndex >= lengthInMinutes) {endIndex = lengthInMinutes - 1;}

                // set all values in available to NEGATIVE_INFINITY so that we don't have overlapping chunks
                for (int i = startIndex; i < endIndex; i++) {
                    available[i] = min - 1;
                }
            } else {
                break; // FIXME is breaking okay?
            }
        }

        // FIXME Should we sort by weight or numPeopleAvailable or somethingElse?
        class TimeChunkComparator implements Comparator<TimeChunk>{
            @Override
            public int compare(TimeChunk t1, TimeChunk t2) {
                long start1 = t1.getStartTime().toEpochSecond(ZoneOffset.UTC);
                long start2 = t2.getStartTime().toEpochSecond(ZoneOffset.UTC);
                if (start1 < start2) { return -1; }
                else if (start1 == start2) {return 0; }
                else {return 1; }
            }
        }

        class TimeChunkWeightComparator implements Comparator<TimeChunk>{
            @Override
            public int compare(TimeChunk t1, TimeChunk t2) {
                double start1 = t1.getWeight();
                double start2 = t2.getWeight();
                if (start1 < start2) { return 1; }
                else if (start1 == start2) {return 0; }
                else {return -1; }
            }
        }

        recommendations.sort(new TimeChunkComparator());

        return recommendations;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void printTimeChunks(List<TimeChunk> chunks) {
        for (var chunk : chunks) {
            System.out.println(chunk.getStartTime() + " " + chunk.getEndTime());
        }
    }

    private static void printTimeArray(int[] timeArray) {
        int count = 0;
        for (int i = 0; i < timeArray.length; i++) {
            if (timeArray[i] == -1) {
                System.out.print("z ");
            } else {
                System.out.print(timeArray[i] + " ");
            }
            count++;
            if (count >= MINUTES_PER_DAY) {
                System.out.println();
                count = 0;
            }
        }
        System.out.println();
        System.out.println();
    }

    private static void printFreeTimeChunks(int[] freeTime) {
        int count = 0;
        for (int i = 0; i < freeTime.length; i++) {
            System.out.print(freeTime[i] + " ");
            count++;
            if (count >= MINUTES_PER_DAY) {
                System.out.println();
                count = 0;
            }
        }
        System.out.println();
    }

    private static void printFreeTimeChunks(double[] freeTime) {
        int count = 0;
        for (int i = 0; i < freeTime.length; i++) {
            if (freeTime[i] < 0) {
                System.out.print("z ");
            } else {
                System.out.print(freeTime[i] + " ");
            }
            count++;
            if (count >= MINUTES_PER_DAY) {
                System.out.println();
                count = 0;
            }
        }
        System.out.println();
    }

    private static void printTimeChunkWithTag(TimeChunk c, String tag) {
        System.out.println(c.getStartTime() + " " + c.getEndTime() + " " + tag);
    }
}

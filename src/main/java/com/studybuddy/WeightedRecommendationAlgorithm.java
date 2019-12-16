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
    private static final int MINUTES_PER_HOUR = (int) (SECONDS_PER_DAY/SECONDS_PER_MINUTE / 24);
    private static final long MINUTES_PER_DAY = SECONDS_PER_DAY/SECONDS_PER_MINUTE;
    private static final long MINUTES_OF_SLEEP = SECONDS_OF_SLEEP/SECONDS_PER_MINUTE;
    private static final long FIFTEEN_MINUTES = SECONDS_PER_MINUTE * MINUTES_PER_HOUR / 4;

    private static final double FUDGE_FACTOR = 0.95;
    private static final double INITIAL_PROXIMITY_WEIGHT = 1;
    private static final double PROXIMITY_RATIO = 0.5;
    private static final double BASELINE_VALUE = 0.1;
    private static final int OFF_BY_ONE = 1; // Accounting for off-by-one errors

    private static final double SLEEP_WEIGHT = -20;
    private static final double HOST_UNAVAILABLE_WEIGHT = -1000;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RECOMMENDATION ALGORITHM ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end,
                                                     List<List<TimeChunk>> busyTimes, List<TimeChunk> host,
                                                     List<Integer> alwaysFree, double fraction, int numRecs) {

        // Determine beginning and end of time period as well as length of time period
        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/SECONDS_PER_MINUTE;

        // Initialize array of doubles to hold availability of individuals
        double[] available = new double[lengthInMinutes];
        for (int i = 0; i < lengthInMinutes; i++) { available[i] = BASELINE_VALUE; }

        // If the interval is about the same length as the studying time, just return the entire interval
        if (Math.abs(lengthInMinutes - (int) (fraction * MINUTES_PER_HOUR * FUDGE_FACTOR)) <= (int) (fraction * MINUTES_PER_HOUR * (1 - FUDGE_FACTOR))) {
            ArrayList<TimeChunk> arr = new ArrayList<>();
            arr.add(TimeChunk.nearest15(new TimeChunk(
                            TimeChunk.makeTime(startSec),
                            TimeChunk.makeTime(endSec)),
                    FIFTEEN_MINUTES
            ));
            return arr;
        }

        // Return empty list if time of interval shorter than length of studying time
        if (lengthInMinutes < (int) (fraction * MINUTES_PER_HOUR)) { return new ArrayList<>();}

        // Make one cumulative list of when people are unavailable
        List<TimeChunk> unavailable = new ArrayList<>();
        for (var userTimeChunkList : busyTimes) {
            unavailable.addAll(userTimeChunkList);
        }

        // Use time chunks to popoluate availability array
        // The availability array starts out equal to BASELINE_VALUE. Every time someone is unavailable,
        // the weight of that person is subtracted from that time interval. This produces negative values
        // in the availability array. The more negative an entry is, the fewer people are available at
        // that time
        for (TimeChunk t : unavailable) {
            // Get start and end second of timechunk
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            // Convert to minutes
            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            // Make sure we only consider time within given time interval
            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes;}
            if (f >= lengthInMinutes) {f = lengthInMinutes;}

            // Reduce availability array during this timechunk interval
            for (int i = s; i < f; i++) { available[i] = available[i] - t.getWeight(); }
        }

        // Find weight of least available time
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < lengthInMinutes; i++) {
            if (available[i] < min) { min = available[i]; }
        }

        // Add smallest value to every entry to make all values positive
        if (min <= 0) {
            for (int i = 0; i < lengthInMinutes; i++) { available[i] += -min; }
        }

        // Initialize sleep start time. This done by using a reference sleep time at some point in the future
        // and using modular arithmetic to determine when people are sleeping during other days. The relative
        // sleep start is the time where a person sleeps relative to the start of the time interval
        long sleepStart = (LocalDateTime.of(2020,1,1,0,0)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / SECONDS_PER_MINUTE;

        if (host == null) {host = new ArrayList<>(); }
        for (TimeChunk t : host) {
            // Get start and end second of timechunk
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            // Convert to minutes
            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            // Make sure we only consider time within given time interval
            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes;}
            if (f >= lengthInMinutes) {f = lengthInMinutes;}

            // Severely reduce availability during time where host is unavailable
            for (int i = s; i < f; i++) { available[i] = HOST_UNAVAILABLE_WEIGHT; }

            // Add proximity weights to favor recommending events before host event. Proximity weights just
            // marginally increase the value of the available time around when the host is available because
            // people often like their schedules to be contiguous
            int lower = s - MINUTES_PER_HOUR - 1;
            double proximityWeight = INITIAL_PROXIMITY_WEIGHT;
            for (int i = s - 1; i >= 0 && i >= lower; i--) {
                available[i] += proximityWeight;
                proximityWeight *= PROXIMITY_RATIO;
            }

            // Add proximity weights to favor recommending events after host event. Same reason as before
            int upper = f + MINUTES_PER_HOUR;
            proximityWeight = INITIAL_PROXIMITY_WEIGHT;
            for (int i = f; i < lengthInMinutes && i <= upper; i++) {
                available[i] += proximityWeight;
                proximityWeight *= PROXIMITY_RATIO;
            }
        }

        // Give negative value to times when people are sleeping. The sleep weight may eventually change person
        // by person.
        int len = available.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                available[i] = SLEEP_WEIGHT;
            }
        }

        // Initialize recommendation array
        ArrayList<TimeChunk> recommendations = new ArrayList<>();

        // Calculate study length accounting for off by one errors
        int lengthStudy = (int) (fraction*MINUTES_PER_HOUR) - OFF_BY_ONE;

        // Determine the weight of each chunk of time during the time interval. A chunk of time is a continuous
        // segment which is lengthStudy units long. Note that there are less "chunks" than entries in availability.
        // We will repeat this process until we generate enough recommendations as requested by the caller.
        double[] chunkValues;
        for (int n = 0; n < numRecs; n++) {
            // Initialize array of values of Studying Chunks
            chunkValues = new double[lengthInMinutes - lengthStudy + 1];

            // Calculate first value of first TimeChunk
            for (int i = 0; i < lengthStudy; i++) {
                chunkValues[0] += available[i];
            }

            // Calculate values of the rest of the TimeChunks (Dynamic Programming)
            for (int i = 1; i < chunkValues.length; i++) {
                chunkValues[i] = chunkValues[i-1] - available[i-1] + available[i+lengthStudy-1];
            }

            // Find most optimal study time by looking for max chunk weight
            double max = Double.NEGATIVE_INFINITY;
            int startIndex = 0;
            for (int i = 0; i < chunkValues.length; i++) {
                if (chunkValues[i] > max) {
                    max = chunkValues[i];
                    startIndex = i;
                }
            }

            // Only add chunk to reccomendations if more people are available than unavailable
            if (max >= 0) {
                // Factor back in the off-by-one error
                int endIndex = startIndex + lengthStudy + OFF_BY_ONE;
                // Add TimeChunk starting at the determined time
                TimeChunk chunk = new TimeChunk(
                        TimeChunk.makeTime(startSec + (startIndex) * SECONDS_PER_MINUTE),
                        TimeChunk.makeTime(startSec + (endIndex) * SECONDS_PER_MINUTE),
                        max
                );

                // Determine who can attend event entirety of the recommended event (before rounding)
                // Initialize attendees list
                List<Integer> attendees = new ArrayList<>();
                // We want to compare other peoples schedules to a slightly smaller chunk to account for off
                // by one errors
                TimeChunk comparisonChunk = new TimeChunk(
                        TimeChunk.makeTime(startSec + (startIndex + OFF_BY_ONE) * SECONDS_PER_MINUTE),
                        TimeChunk.makeTime(startSec + (endIndex - OFF_BY_ONE) * SECONDS_PER_MINUTE),
                        max
                );

                // Iterate through users with potentially conflicting schedules. If the user has an event which
                // conflicts with the recommended event, they are not added to the list of attendees
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
                // Add people with no conflicts on schedule to attendees list and then add to chunk's userIDs
                attendees.addAll(alwaysFree);
                chunk.setUserIDs(attendees);

                // Round chunk to nearest 15 minute interval and add to reccomendations
                chunk = TimeChunk.nearest15(chunk, FIFTEEN_MINUTES);
                recommendations.add(chunk);

                // Initialize indices for clearing time interval just recommended. We set all values in this
                // interval to be negative so that the algorithm doesn't select the same times repeatedly
                startIndex = (int) ((chunk.getStartTime().toEpochSecond(ZoneOffset.UTC) - startSec)/SECONDS_PER_MINUTE);
                endIndex = startIndex + lengthStudy;
                if (startIndex < 0) {startIndex = 0;} // Don't access indices out of range.
                if (endIndex >= lengthInMinutes) {endIndex = lengthInMinutes - 1;}

                // set all values in available to the smallest value - 1, so that we don't have overlapping chunks
                for (int i = startIndex; i < endIndex; i++) {
                    available[i] = min - 1;
                }
            } else {
                break;
            }
        }

        // Sort recommendations chronologically
        recommendations.sort(new TimeChunk.TimeChunkComparator());

        return recommendations;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end,
                                                     double fraction, int numRecs, ArrayList<TimeChunk> hostTimes) {
        return makeRecommendation(start, end, new ArrayList<>(), hostTimes, new ArrayList<>(), fraction, numRecs);
    }

        public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, List<List<TimeChunk>> busyTimes, double fraction, int numRecs) {
        ArrayList<TimeChunk> arr = new ArrayList<>();
        return makeRecommendation(start,end,busyTimes,arr,new ArrayList<>(),fraction,numRecs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEBUGGER FUNCTIONS //////////////////////////////////////////////////////////////////////////////////////////////
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

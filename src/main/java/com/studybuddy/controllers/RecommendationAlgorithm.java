package com.studybuddy.controllers;

import com.studybuddy.models.TimeChunk;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class RecommendationAlgorithm {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int COMPRESSION_FACTOR = 1;
    private static final int SECONDS_PER_MINUTE = 60 * COMPRESSION_FACTOR; //seconds/60 = minutes
    private static final long SECONDS_PER_DAY = 86400;
    private static final long SECONDS_OF_SLEEP = 28800;
    private static final int MINUTES_PER_HOUR = (int) (SECONDS_PER_DAY/SECONDS_PER_MINUTE/24);
    private static final long MINUTES_PER_DAY = SECONDS_PER_DAY/SECONDS_PER_MINUTE;
    private static final long MINUTES_OF_SLEEP = SECONDS_OF_SLEEP/SECONDS_PER_MINUTE;
    private static final long FIFTEEN_MINUTES = SECONDS_PER_MINUTE * MINUTES_PER_HOUR / 4;

    private static final double SLEEP_WEIGHT = -1;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RECOMMENDATION ALGORITHM 1 //////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<TimeChunk> makeRecommendation(LocalDateTime start, LocalDateTime end, List<TimeChunk> unavailable, double fraction) {

        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/SECONDS_PER_MINUTE;
        int[] timeArray = new int[lengthInMinutes];

        for (TimeChunk t : unavailable) {
            long trueS = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long trueF = t.getEndTime().toEpochSecond(ZoneOffset.UTC);

            int s = (int) (trueS - startSec)/SECONDS_PER_MINUTE;
            int f = (int) (trueF - startSec)/SECONDS_PER_MINUTE;

            if (s < 0) {s = 0;}
            if (f < 0) {f = 0;}
            if (s >= lengthInMinutes) {s = lengthInMinutes - 1;}
            if (f >= lengthInMinutes) {f = lengthInMinutes - 1;}

            for (int i = s; i <= f; i++) { timeArray[i]++; }
        }

        long sleepStart = (LocalDateTime.of(2020,1,1,0,0)).toEpochSecond(ZoneOffset.UTC);
        int relativeSleepStart = (int) ((sleepStart - startSec) % SECONDS_PER_DAY) / SECONDS_PER_MINUTE;

        int len = timeArray.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                timeArray[i] = -1;
            }
        }

//        printTimeArray(timeArray); // Debugging Print Statement

        return findStudyTimes(timeArray, startSec, fraction);
    }

    private static List<TimeChunk> findStudyTimes(int[] timeArray, long startSec, double fraction) {

        int length = timeArray.length;
        int[] freeTime = new int[length];

        for (int i = 0; i < length; i++) {
            if (timeArray[i] == 0) { freeTime[i] = 1; }
            else { freeTime[i] = 0; }
        }

//        printFreeTimeChunks(freeTime); // Debugging Print Statement

        List<TimeChunk> chunks = new ArrayList<>();
        int state = -1;
        for (int i = 0; i < length; i++) {
            if (freeTime[i] == 1) {
                if (state == -1) {
                    state = i;
                }
            } else if (freeTime[i] == 0)  {
                if (state != -1) {
                    TimeChunk c = new TimeChunk(
                            makeTime(startSec + (state) * SECONDS_PER_MINUTE),
                            makeTime(startSec + (i) * SECONDS_PER_MINUTE)
                    );
                    chunks.add(c);
//                    printTimeChunkWithTag(c, "x");
                    state = -1;
                }
            }
        }
        if (state != -1) {
            TimeChunk c = new TimeChunk(
                    makeTime(startSec + (state)*SECONDS_PER_MINUTE),
                    makeTime(startSec + (length-1)*SECONDS_PER_MINUTE)
            );
            chunks.add(c);
//            printTimeChunkWithTag(c, "y");
        }

        return createStudyChunks(chunks, fraction);
    }

    private static List<TimeChunk> createStudyChunks(List<TimeChunk> chunks, double fraction) {

        List<TimeChunk> studyChunks = new ArrayList<>();
        int studyLength = (int) (fraction*MINUTES_PER_HOUR);

        for (var chunk: chunks) {
            long start = chunk.getStartTime().toEpochSecond(ZoneOffset.UTC)/SECONDS_PER_MINUTE;
            long end = chunk.getEndTime().toEpochSecond(ZoneOffset.UTC)/SECONDS_PER_MINUTE;
            int chunkLength = (int) (end - start);

//            printTimeChunkWithTag(chunk, "c");

            if (studyLength <= chunkLength) {
                double fractionSlots = (chunkLength + 5) * (1.0) / studyLength;
                int numSlots = (int) fractionSlots;
                if (fractionSlots - numSlots >= 0.5) { numSlots++;}
                for (int i = 0; i < numSlots/2; i++) {

                    long forwardBegin = start + i*studyLength;
                    long forwardEnd = start + (i+1)*studyLength;
                    TimeChunk c = new TimeChunk(
                            makeTime((forwardBegin-1)*SECONDS_PER_MINUTE),
                            makeTime((forwardEnd-1)*SECONDS_PER_MINUTE)
                    );
//                    printTimeChunkWithTag(c, "f");
                    studyChunks.add(c);

                    long reverseEnd = end - i*studyLength;
                    long reverseBegin = end - (i+1)*studyLength;
                    c = new TimeChunk(
                            makeTime(reverseBegin*SECONDS_PER_MINUTE),
                            makeTime(reverseEnd*SECONDS_PER_MINUTE)
                    );
//                    printTimeChunkWithTag(c, "r");
                    studyChunks.add(c);

                }
                if (numSlots % 2 == 1) {
                    long forwardBegin = start + numSlots/2*studyLength;
                    long forwardEnd = start + (numSlots/2+1)*studyLength;
                    TimeChunk c = new TimeChunk(
                            makeTime((forwardBegin-1)*SECONDS_PER_MINUTE),
                            makeTime((forwardEnd-1)*SECONDS_PER_MINUTE)
                    );
//                    printTimeChunkWithTag(c, "a");
                    studyChunks.add(c);
                }
            }
        }

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

        studyChunks.sort(new TimeChunkComparator());

        return studyChunks;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RECOMMENDATION ALGORITHM 2 //////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<TimeChunk> makeBetterRecommendation(LocalDateTime start, LocalDateTime end, List<TimeChunk> unavailable, double fraction, int numRecs) {

        // Initialize array of doubles to hold availability of individuals
        long startSec = start.toEpochSecond(ZoneOffset.UTC);
        long endSec = end.toEpochSecond(ZoneOffset.UTC);
        int lengthInMinutes = (int) (endSec-startSec)/SECONDS_PER_MINUTE;
        double[] available = new double[lengthInMinutes];

        // Time of interval shorter than length of studying time
        if (lengthInMinutes < (int) (fraction * MINUTES_PER_HOUR)) { return new ArrayList<>();}

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

        // Give negative value to times when people are sleeping
        int len = available.length;
        for (int i = 0; i < len; i++) {
            if ((i - relativeSleepStart + MINUTES_PER_DAY) % MINUTES_PER_DAY <= MINUTES_OF_SLEEP) {
                available[i] = SLEEP_WEIGHT;
            }
        }

        ArrayList<TimeChunk> recommendations = new ArrayList<>();
        int lengthStudy = (int) (fraction*MINUTES_PER_HOUR);
        double[] chunkValues;

        for (int n = 0; n < numRecs; n++) {
//            printFreeTimeChunks(available);
//            System.out.println();

            // Initialize array of values of Studying Chunks
            chunkValues = new double[lengthInMinutes - lengthStudy];
//            chunkValues = new double[available.length - lengthStudy];

            // Calculate first value of first TimeChunk
            for (int i = 0; i < lengthStudy; i++) {
                chunkValues[0] += available[i];
            }

            // Calculate values of the rest of the TimeChunks
            for (int i = 1; i < chunkValues.length; i++) {
                chunkValues[i] = chunkValues[i-1] - available[i-1] + available[i+lengthStudy];
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
                int endIndex = startIndex + lengthStudy;
                // Add TimeChunk starting at this time
                TimeChunk chunk = nearest15(new TimeChunk(
                                makeTime(startSec + (startIndex) * SECONDS_PER_MINUTE),
                                makeTime(startSec + (endIndex) * SECONDS_PER_MINUTE)
                ));
                recommendations.add(chunk);
                startIndex = (int) ((chunk.getStartTime().toEpochSecond(ZoneOffset.UTC) - startSec)/60);
                endIndex = startIndex + lengthStudy;
                // set all values in available to NEGATIVE_INFINITY so that we don't have overlapping chunks
                for (int i = startIndex; i < endIndex; i++) {
                    available[i] = min - 1;
                }
            }
//            System.out.println(startIndex);
//            System.out.println(n);
        }

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

        recommendations.sort(new TimeChunkComparator());

        return recommendations;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static LocalDateTime makeTime(long t) {
        return LocalDateTime.ofEpochSecond(t,0,ZoneOffset.ofHours(0));
    }

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
                System.out.print((int) freeTime[i] + " ");
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

    private static TimeChunk nearest15(TimeChunk t) {

        long startSec = t.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long endSec = t.getEndTime().toEpochSecond(ZoneOffset.UTC);
        long mod = startSec % (FIFTEEN_MINUTES);

        if (mod <= FIFTEEN_MINUTES / 2) {
            startSec = startSec - mod;
            endSec = endSec - mod;
        } else {
            startSec = startSec - mod + (FIFTEEN_MINUTES);
            endSec = endSec - mod + (FIFTEEN_MINUTES);
        }

        return new TimeChunk(makeTime(startSec), makeTime(endSec));
    }
}

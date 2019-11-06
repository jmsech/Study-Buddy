package com.studybuddy.controllers;

import com.studybuddy.models.TimeChunk;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FUNCTIONS ///////////////////////////////////////////////////////////////////////////////////////////////////////
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

    private static void printTimeChunkWithTag(TimeChunk c, String tag) {
        System.out.println(c.getStartTime() + " " + c.getEndTime() + " " + tag);
    }
}

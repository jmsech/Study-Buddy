package com.studybuddy.models;

import com.studybuddy.RecommendationAlgorithm;
import com.studybuddy.WeightedRecommendationAlgorithm;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class RecommendationTests {

    LocalDateTime jan_1_2019_12_00_AM = LocalDateTime.of(2019,1,1,0,0);
    LocalDateTime jan_1_2019_1_00_AM = LocalDateTime.of(2019,1,1,1,0);
    LocalDateTime jan_1_2019_2_00_AM = LocalDateTime.of(2019,1,1,2,0);
    LocalDateTime jan_1_2019_3_00_AM = LocalDateTime.of(2019,1,1,3,0);
    LocalDateTime jan_1_2019_4_00_AM = LocalDateTime.of(2019,1,1,4,0);
    LocalDateTime jan_1_2019_5_00_AM = LocalDateTime.of(2019,1,1,5,0);
    LocalDateTime jan_1_2019_6_00_AM = LocalDateTime.of(2019,1,1,6,0);
    LocalDateTime jan_1_2019_7_00_AM = LocalDateTime.of(2019,1,1,7,0);
    LocalDateTime jan_1_2019_8_00_AM = LocalDateTime.of(2019,1,1,8,0);
    LocalDateTime jan_1_2019_9_00_AM = LocalDateTime.of(2019,1,1,9,0);
    LocalDateTime jan_1_2019_10_00_AM = LocalDateTime.of(2019,1,1,10,0);
    LocalDateTime jan_1_2019_11_00_AM = LocalDateTime.of(2019,1,1,11,0);
    LocalDateTime jan_1_2019_12_00_PM = LocalDateTime.of(2019,1,1,12,0);
    LocalDateTime jan_1_2019_1_00_PM = LocalDateTime.of(2019,1,1,13,0);
    LocalDateTime jan_1_2019_2_00_PM = LocalDateTime.of(2019,1,1,14,0);
    LocalDateTime jan_1_2019_3_00_PM = LocalDateTime.of(2019,1,1,15,0);
    LocalDateTime jan_1_2019_4_00_PM = LocalDateTime.of(2019,1,1,16,0);
    LocalDateTime jan_1_2019_5_00_PM = LocalDateTime.of(2019,1,1,17,0);
    LocalDateTime jan_1_2019_6_00_PM = LocalDateTime.of(2019,1,1,18,0);
    LocalDateTime jan_1_2019_7_00_PM = LocalDateTime.of(2019,1,1,19,0);
    LocalDateTime jan_1_2019_8_00_PM = LocalDateTime.of(2019,1,1,20,0);
    LocalDateTime jan_1_2019_9_00_PM = LocalDateTime.of(2019,1,1,21,0);
    LocalDateTime jan_1_2019_10_00_PM = LocalDateTime.of(2019,1,1,22,0);
    LocalDateTime jan_1_2019_11_00_PM = LocalDateTime.of(2019,1,1,23,0);
    LocalDateTime jan_2_2019_12_00_AM = LocalDateTime.of(2019,1,2,0,0);

    @Test
    public void recommendFreeTimesFromMorningToNight() {
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_8_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 1, 16);
        assertEquals(16,recs.size());
    }

    @Test
    public void recommendFreeTimesFromForEntireFreeDay() { // Must account for sleep time
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 1, 16);
        assertEquals(16,recs.size());
    }

    @Test
    public void recommendOnlyTimesWithPositiveValues() { // Don't recommned times when people are mostly asleep
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 1, 24);
        assertEquals(16,recs.size());
    }

    @Test
    public void studyTimesAreCorrectLength() {
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 1, 1);
        assertEquals(3600,recs.get(0).getEndTime().toEpochSecond(ZoneOffset.UTC)-recs.get(0).getStartTime().toEpochSecond(ZoneOffset.UTC));
        recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 4, 1);
        assertEquals(4*3600,recs.get(0).getEndTime().toEpochSecond(ZoneOffset.UTC)-recs.get(0).getStartTime().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void studyTimesAreCorrectLengthForDecimalFraction() {
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 1.5, 1);
        assertEquals((int) (1.5*3600/60/5)*60*5,recs.get(0).getEndTime().toEpochSecond(ZoneOffset.UTC)-recs.get(0).getStartTime().toEpochSecond(ZoneOffset.UTC));
        recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, new ArrayList<>(), 3.456789, 1);
        assertEquals((int) (3.456789*3600/60/5)*60*5,recs.get(0).getEndTime().toEpochSecond(ZoneOffset.UTC)-recs.get(0).getStartTime().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void recommendationCompressesTimeIntoStartEndInterval() { // Don't worry about event times that are outside specified interval
        ArrayList<TimeChunk> list = new ArrayList<>();
        list.add(new TimeChunk(jan_1_2019_8_00_AM, jan_1_2019_4_00_PM));
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_10_00_AM, jan_1_2019_2_00_PM, list, 1, 4);
        assertEquals(4,recs.size());
        assertEquals(recs.get(0).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_10_00_AM.toEpochSecond(ZoneOffset.UTC));
        assertEquals(recs.get(2).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_12_00_PM.toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void recommendationsDoNotRecommendFilledTime() {
        ArrayList<TimeChunk> list = new ArrayList<>();
        list.add(new TimeChunk(jan_1_2019_10_00_AM, jan_1_2019_12_00_PM));
        list.add(new TimeChunk(jan_1_2019_2_00_PM, jan_1_2019_6_00_PM));
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, list, 2, 5);
        assertEquals(5,recs.size());
        assertEquals(recs.get(1).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_12_00_PM.toEpochSecond(ZoneOffset.UTC));
        assertEquals(recs.get(3).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_8_00_PM.toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void recommendationsDontScheduleDuringOverSchedule() {
        ArrayList<TimeChunk> list = new ArrayList<>();
        list.add(new TimeChunk(jan_1_2019_10_00_AM, jan_1_2019_12_00_PM));
        list.add(new TimeChunk(jan_1_2019_2_00_PM, jan_1_2019_6_00_PM));
        List<TimeChunk> recs = WeightedRecommendationAlgorithm.makeRecommendation(jan_1_2019_12_00_AM, jan_2_2019_12_00_AM, list, list, 2, 20);
        assertEquals(5,recs.size());
        assertEquals(recs.get(1).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_12_00_PM.toEpochSecond(ZoneOffset.UTC));
        assertEquals(recs.get(3).getStartTime().toEpochSecond(ZoneOffset.UTC), jan_1_2019_8_00_PM.toEpochSecond(ZoneOffset.UTC));
    }

    public static void printTimeChunks(List<TimeChunk> chunks) {
        for (var chunk : chunks) {
            System.out.println(chunk.getStartTime() + " " + chunk.getEndTime());
        }
    }
}

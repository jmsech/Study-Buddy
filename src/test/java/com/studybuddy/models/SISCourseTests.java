package com.studybuddy.models;

import com.google.api.services.calendar.model.Events;
import com.studybuddy.repositories.EventRepository;
import com.studybuddy.repositories.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SISCourseTests {
     String termStartDate = "TermStartDate";
     String schoolName = "SchoolName";
     String coursePrefix = "CoursePrefix";
     String term = "Term";
     String term_IDR = "Term_IDR";
     String offeringName = "OfferingName";
     String sectionName = "SectionName";
     String title = "Title";
     String credits = "Credits";
     String department = "Department";
     String level = "Level";
     String status = "Status";
     String DOW = "DOW";
     String DOWSort = "DOWSort";
     String timeOfDay = "TimeOfDay";
     String subDepartment = "SubDepartment";
     String sectionRegRestrictions = "SectionRegRestrictions";
     String seatsAvailable = "SeatsAvailable";
     String maxSeats = "MaxSeats";
     String openSeats = "OpenSeats";
     String waitlisted = "Waitlisted";
     String isWritingIntensive = "IsWritingIntensive";
     String allDepartments = "AllDepartments";
     String instructors = "Instructors";
     String instructorsFullName = "InstructorsFullName";
     String location = "Location";
     String building = "Building ";
     String hasBio = "HasBio";
     String meetings = "Meetings";
     String areas = "Areas";
     String instructionMethod = "InstructionMethod";
     String sectionCoRequisites = "SectionCoRequisites";
     String sectionCoReqNotes = "SectionCoReqNotes";
     String SSS_SectionsID = "SSS_SectionsID";
     String term_JSS = "Term_JSS";
     String repeatable = "Repeatable";
     String sectionDetails = "SectionDetails";

     @Test
     void testGetters() {
         SISCourse testSIS = new SISCourse(termStartDate, schoolName, coursePrefix, term, term_IDR,
                 offeringName, sectionName, title, credits, department, level,
                 status, DOW, DOWSort, timeOfDay, subDepartment, sectionRegRestrictions,
                 seatsAvailable, maxSeats, openSeats, waitlisted, isWritingIntensive,
                 allDepartments, instructors, instructorsFullName, location, building,
                 hasBio, meetings, areas, instructionMethod, sectionCoRequisites,
                 sectionCoReqNotes, SSS_SectionsID, term_JSS, repeatable, sectionDetails);

         assertEquals(termStartDate, testSIS.getTermStartDate());
         assertEquals(schoolName, testSIS.getSchoolName());
         assertEquals(coursePrefix, testSIS.getCoursePrefix());
         assertEquals(term, testSIS.getTerm());
         assertEquals(term_IDR, testSIS.getTerm_IDR());
         assertEquals(offeringName, testSIS.getOfferingName());
         assertEquals(sectionName, testSIS.getSectionName());
         assertEquals(title, testSIS.getTitle());
         assertEquals(credits, testSIS.getCredits());
         assertEquals(department, testSIS.getDepartment());
         assertEquals(level, testSIS.getLevel());
         assertEquals(status, testSIS.getStatus());
         assertEquals(DOW, testSIS.getDOW());
         assertEquals(DOWSort, testSIS.getDOWSort());
         assertEquals(timeOfDay, testSIS.getTimeOfDay());
         assertEquals(subDepartment, testSIS.getSubDepartment());
         assertEquals(sectionRegRestrictions, testSIS.getSectionRegRestrictions());
         assertEquals(seatsAvailable, testSIS.getSeatsAvailable());
         assertEquals(maxSeats, testSIS.getMaxSeats());
         assertEquals(openSeats, testSIS.getOpenSeats());
         assertEquals(waitlisted, testSIS.getWaitlisted());
         assertEquals(isWritingIntensive, testSIS.getIsWritingIntensive());
         assertEquals(allDepartments, testSIS.getAllDepartments());
         assertEquals(instructors, testSIS.getInstructors());
         assertEquals(instructorsFullName, testSIS.getInstructorsFullName());
         assertEquals(location, testSIS.getLocation());
         assertEquals(building, testSIS.getBuilding());
         assertEquals(hasBio, testSIS.getHasBio());
         assertEquals(meetings, testSIS.getMeetings());
         assertEquals(areas, testSIS.getAreas());
         assertEquals(instructionMethod, testSIS.getInstructionMethod());
         assertEquals(sectionCoRequisites, testSIS.getSectionCoRequisites());
         assertEquals(sectionCoReqNotes, testSIS.getSectionCoReqNotes());
         assertEquals(SSS_SectionsID, testSIS.getSSS_SectionsID());
         assertEquals(term_JSS, testSIS.getTerm_JSS());
         assertEquals(repeatable, testSIS.getRepeatable());
         assertEquals(sectionDetails, testSIS.getSectionDetails());
     }

     @Test
     void testSetters() {
         SISCourse testSIS = new SISCourse(termStartDate, schoolName, coursePrefix, term, term_IDR,
                 offeringName, sectionName, title, credits, department, level,
                 status, DOW, DOWSort, timeOfDay, subDepartment, sectionRegRestrictions,
                 seatsAvailable, maxSeats, openSeats, waitlisted, isWritingIntensive,
                 allDepartments, instructors, instructorsFullName, location, building,
                 hasBio, meetings, areas, instructionMethod, sectionCoRequisites,
                 sectionCoReqNotes, SSS_SectionsID, term_JSS, repeatable, sectionDetails);

         String termStartDate2 = "TermStartDate2";
         String schoolName2 = "SchoolName2";
         String coursePrefix2 = "CoursePrefix2";
         String term2 = "Term2";
         String term_IDR2 = "Term_IDR2";
         String offeringName2 = "OfferingName2";
         String sectionName2 = "SectionName2";
         String title2 = "Title2";
         String credits2 = "Credits2";
         String department2 = "Department2";
         String level2 = "Level2";
         String status2 = "Status2";
         String DOW2 = "DOW2";
         String DOWSort2 = "DOWSort2";
         String timeOfDay2 = "TimeOfDay2";
         String subDepartment2 = "SubDepartment2";
         String sectionRegRestrictions2 = "SectionRegRestrictions2";
         String seatsAvailable2 = "SeatsAvailable2";
         String maxSeats2 = "MaxSeats2";
         String openSeats2 = "OpenSeats2";
         String waitlisted2 = "Waitlisted2";
         String isWritingIntensive2 = "IsWritingIntensive2";
         String allDepartments2 = "AllDepartments2";
         String instructors2 = "Instructors2";
         String instructorsFullName2 = "InstructorsFullName2";
         String location2 = "Location2";
         String building2 = "Building2";
         String hasBio2 = "HasBio2";
         String meetings2 = "Meetings2";
         String areas2 = "Areas2";
         String instructionMethod2 = "InstructionMethod2";
         String sectionCoRequisites2 = "SectionCoRequisites2";
         String sectionCoReqNotes2 = "SectionCoReqNotes2";
         String SSS_SectionsID2 = "SSS_SectionsID2";
         String term_JSS2 = "Term_JSS2";
         String repeatable2 = "Repeatable2";
         String sectionDetails2 = "SectionDetails2";

         testSIS.setTermStartDate(termStartDate2);
         testSIS.setSchoolName(schoolName2);
         testSIS.setCoursePrefix(coursePrefix2);
         testSIS.setTerm(term2);
         testSIS.setTerm_IDR(term_IDR2);
         testSIS.setOfferingName(offeringName2);
         testSIS.setSectionName(sectionName2);
         testSIS.setTitle(title2);
         testSIS.setCredits(credits2);
         testSIS.setDepartment(department2);
         testSIS.setLevel(level2);
         testSIS.setStatus(status2);
         testSIS.setDOW(DOW2);
         testSIS.setDOWSort(DOWSort2);
         testSIS.setTimeOfDay(timeOfDay2);
         testSIS.setSubDepartment(subDepartment2);
         testSIS.setSectionRegRestrictions(sectionRegRestrictions2);
         testSIS.setSeatsAvailable(seatsAvailable2);
         testSIS.setMaxSeats(maxSeats2);
         testSIS.setOpenSeats(openSeats2);
         testSIS.setWaitlisted(waitlisted2);
         testSIS.setIsWritingIntensive(isWritingIntensive2);
         testSIS.setAllDepartments(allDepartments2);
         testSIS.setInstructors(instructors2);
         testSIS.setInstructorsFullName(instructorsFullName2);
         testSIS.setLocation(location2);
         testSIS.setBuilding(building2);
         testSIS.setHasBio(hasBio2);
         testSIS.setMeetings(meetings2);
         testSIS.setAreas(areas2);
         testSIS.setInstructionMethod(instructionMethod2);
         testSIS.setSectionCoRequisites(sectionCoRequisites2);
         testSIS.setSectionCoReqNotes(sectionCoReqNotes2);
         testSIS.setSSS_SectionsID(SSS_SectionsID2);
         testSIS.setTerm_JSS(term_JSS2);
         testSIS.setRepeatable(repeatable2);
         testSIS.setSectionDetails(sectionDetails2);

         assertEquals(termStartDate2, testSIS.getTermStartDate());
         assertEquals(schoolName2, testSIS.getSchoolName());
         assertEquals(coursePrefix2, testSIS.getCoursePrefix());
         assertEquals(term2, testSIS.getTerm());
         assertEquals(term_IDR2, testSIS.getTerm_IDR());
         assertEquals(offeringName2, testSIS.getOfferingName());
         assertEquals(sectionName2, testSIS.getSectionName());
         assertEquals(title2, testSIS.getTitle());
         assertEquals(credits2, testSIS.getCredits());
         assertEquals(department2, testSIS.getDepartment());
         assertEquals(level2, testSIS.getLevel());
         assertEquals(status2, testSIS.getStatus());
         assertEquals(DOW2, testSIS.getDOW());
         assertEquals(DOWSort2, testSIS.getDOWSort());
         assertEquals(timeOfDay2, testSIS.getTimeOfDay());
         assertEquals(subDepartment2, testSIS.getSubDepartment());
         assertEquals(sectionRegRestrictions2, testSIS.getSectionRegRestrictions());
         assertEquals(seatsAvailable2, testSIS.getSeatsAvailable());
         assertEquals(maxSeats2, testSIS.getMaxSeats());
         assertEquals(openSeats2, testSIS.getOpenSeats());
         assertEquals(waitlisted2, testSIS.getWaitlisted());
         assertEquals(isWritingIntensive2, testSIS.getIsWritingIntensive());
         assertEquals(allDepartments2, testSIS.getAllDepartments());
         assertEquals(instructors2, testSIS.getInstructors());
         assertEquals(instructorsFullName2, testSIS.getInstructorsFullName());
         assertEquals(location2, testSIS.getLocation());
         assertEquals(building2, testSIS.getBuilding());
         assertEquals(hasBio2, testSIS.getHasBio());
         assertEquals(meetings2, testSIS.getMeetings());
         assertEquals(areas2, testSIS.getAreas());
         assertEquals(instructionMethod2, testSIS.getInstructionMethod());
         assertEquals(sectionCoRequisites2, testSIS.getSectionCoRequisites());
         assertEquals(sectionCoReqNotes2, testSIS.getSectionCoReqNotes());
         assertEquals(SSS_SectionsID2, testSIS.getSSS_SectionsID());
         assertEquals(term_JSS2, testSIS.getTerm_JSS());
         assertEquals(repeatable2, testSIS.getRepeatable());
         assertEquals(sectionDetails2, testSIS.getSectionDetails());
     }

}

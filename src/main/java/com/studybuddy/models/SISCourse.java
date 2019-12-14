package com.studybuddy.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SISCourse {
    private String TermStartDate;
    private String SchoolName;
    private String CoursePrefix;
    private String Term;
    private String Term_IDR;
    private String OfferingName;
    private String SectionName;
    private String Title;
    private String Credits;
    private String Department;
    private String Level;
    private String Status;
    private String DOW;
    private String DOWSort;
    private String TimeOfDay;
    private String SubDepartment;
    private String SectionRegRestrictions;
    private String SeatsAvailable;
    private String MaxSeats;
    private String OpenSeats;
    private String Waitlisted;
    private String IsWritingIntensive;
    private String AllDepartments;
    private String Instructors;
    private String InstructorsFullName;
    private String Location;
    private String Building;
    private String HasBio;
    private String Meetings;
    private String Areas;
    private String InstructionMethod;
    private String SectionCoRequisites;
    private String SectionCoReqNotes;
    private String SSS_SectionsID;
    private String Term_JSS;
    private String Repeatable;
    private String SectionDetails;

    public SISCourse() {
        super();
    }

    public SISCourse(String termStartDate, String schoolName, String coursePrefix, String term, String term_IDR,
                     String offeringName, String sectionName, String title, String credits, String department, String level,
                     String status, String DOW, String DOWSort, String timeOfDay, String subDepartment, String sectionRegRestrictions,
                     String seatsAvailable, String maxSeats, String openSeats, String waitlisted, String isWritingIntensive,
                     String allDepartments, String instructors, String instructorsFullName, String location, String building,
                     String hasBio, String meetings, String areas, String instructionMethod, String sectionCoRequisites,
                     String sectionCoReqNotes, String SSS_SectionsID, String term_JSS, String repeatable, String sectionDetails) {
        TermStartDate = termStartDate;
        SchoolName = schoolName;
        CoursePrefix = coursePrefix;
        Term = term;
        Term_IDR = term_IDR;
        OfferingName = offeringName;
        SectionName = sectionName;
        Title = title;
        Credits = credits;
        Department = department;
        Level = level;
        Status = status;
        this.DOW = DOW;
        this.DOWSort = DOWSort;
        TimeOfDay = timeOfDay;
        SubDepartment = subDepartment;
        SectionRegRestrictions = sectionRegRestrictions;
        SeatsAvailable = seatsAvailable;
        MaxSeats = maxSeats;
        OpenSeats = openSeats;
        Waitlisted = waitlisted;
        IsWritingIntensive = isWritingIntensive;
        AllDepartments = allDepartments;
        Instructors = instructors;
        InstructorsFullName = instructorsFullName;
        Location = location;
        Building = building;
        HasBio = hasBio;
        Meetings = meetings;
        Areas = areas;
        InstructionMethod = instructionMethod;
        SectionCoRequisites = sectionCoRequisites;
        SectionCoReqNotes = sectionCoReqNotes;
        this.SSS_SectionsID = SSS_SectionsID;
        Term_JSS = term_JSS;
        Repeatable = repeatable;
        SectionDetails = sectionDetails;
    }

    @JsonProperty("TermStartDate")
    public String getTermStartDate() {
        return TermStartDate;
    }

    public void setTermStartDate(String termStartDate) {
        TermStartDate = termStartDate;
    }

    @JsonProperty("SchoolName")
    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    @JsonProperty("CoursePrefix")
    public String getCoursePrefix() {
        return CoursePrefix;
    }

    public void setCoursePrefix(String coursePrefix) {
        CoursePrefix = coursePrefix;
    }

    @JsonProperty("Term")
    public String getTerm() {
        return Term;
    }

    public void setTerm(String term) {
        Term = term;
    }

    @JsonProperty("Term_IDR")
    public String getTerm_IDR() {
        return Term_IDR;
    }

    public void setTerm_IDR(String term_IDR) {
        Term_IDR = term_IDR;
    }

    @JsonProperty("OfferingName")
    public String getOfferingName() {
        return OfferingName;
    }

    public void setOfferingName(String offeringName) {
        OfferingName = offeringName;
    }

    @JsonProperty("SectionName")
    public String getSectionName() {
        return SectionName;
    }

    public void setSectionName(String sectionName) {
        SectionName = sectionName;
    }

    @JsonProperty("Title")
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    @JsonProperty("Credits")
    public String getCredits() {
        return Credits;
    }

    public void setCredits(String credits) {
        Credits = credits;
    }

    @JsonProperty("Department")
    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    @JsonProperty("Level")
    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @JsonProperty("DOW")
    public String getDOW() {
        return DOW;
    }

    public void setDOW(String DOW) {
        this.DOW = DOW;
    }

    @JsonProperty("DOWSort")
    public String getDOWSort() {
        return DOWSort;
    }

    public void setDOWSort(String DOWSort) {
        this.DOWSort = DOWSort;
    }

    @JsonProperty("TimeOfDay")
    public String getTimeOfDay() {
        return TimeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        TimeOfDay = timeOfDay;
    }

    @JsonProperty("SubDepartment")
    public String getSubDepartment() {
        return SubDepartment;
    }

    public void setSubDepartment(String subDepartment) {
        SubDepartment = subDepartment;
    }

    @JsonProperty("SectionRegRestrictions")
    public String getSectionRegRestrictions() {
        return SectionRegRestrictions;
    }

    public void setSectionRegRestrictions(String sectionRegRestrictions) {
        SectionRegRestrictions = sectionRegRestrictions;
    }

    @JsonProperty("SeatsAvailable")
    public String getSeatsAvailable() {
        return SeatsAvailable;
    }

    public void setSeatsAvailable(String seatsAvailable) {
        SeatsAvailable = seatsAvailable;
    }

    @JsonProperty("MaxSeats")
    public String getMaxSeats() {
        return MaxSeats;
    }

    public void setMaxSeats(String maxSeats) {
        MaxSeats = maxSeats;
    }

    @JsonProperty("OpenSeats")
    public String getOpenSeats() {
        return OpenSeats;
    }

    public void setOpenSeats(String openSeats) {
        OpenSeats = openSeats;
    }

    @JsonProperty("Waitlisted")
    public String getWaitlisted() {
        return Waitlisted;
    }

    public void setWaitlisted(String waitlisted) {
        Waitlisted = waitlisted;
    }

    @JsonProperty("IsWritingIntensive")
    public String getIsWritingIntensive() {
        return IsWritingIntensive;
    }

    public void setIsWritingIntensive(String isWritingIntensive) {
        IsWritingIntensive = isWritingIntensive;
    }

    @JsonProperty("AllDepartments")
    public String getAllDepartments() {
        return AllDepartments;
    }

    public void setAllDepartments(String allDepartments) {
        AllDepartments = allDepartments;
    }

    @JsonProperty("Instructors")
    public String getInstructors() {
        return Instructors;
    }

    public void setInstructors(String instructors) {
        Instructors = instructors;
    }

    @JsonProperty("InstructorsFullName")
    public String getInstructorsFullName() {
        return InstructorsFullName;
    }

    public void setInstructorsFullName(String instructorsFullName) {
        InstructorsFullName = instructorsFullName;
    }

    @JsonProperty("Location")
    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    @JsonProperty("Building")
    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String building) {
        Building = building;
    }

    @JsonProperty("HasBio")
    public String getHasBio() {
        return HasBio;
    }

    public void setHasBio(String hasBio) {
        HasBio = hasBio;
    }

    @JsonProperty("Meetings")
    public String getMeetings() {
        return Meetings;
    }

    public void setMeetings(String meetings) {
        Meetings = meetings;
    }

    @JsonProperty("Areas")
    public String getAreas() {
        return Areas;
    }

    public void setAreas(String areas) {
        Areas = areas;
    }

    @JsonProperty("InstructionMethod")
    public String getInstructionMethod() {
        return InstructionMethod;
    }

    public void setInstructionMethod(String instructionMethod) {
        InstructionMethod = instructionMethod;
    }

    @JsonProperty("SectionCoRequisites")
    public String getSectionCoRequisites() {
        return SectionCoRequisites;
    }

    public void setSectionCoRequisites(String sectionCoRequisites) {
        SectionCoRequisites = sectionCoRequisites;
    }

    @JsonProperty("SectionCoReqNotes")
    public String getSectionCoReqNotes() {
        return SectionCoReqNotes;
    }

    public void setSectionCoReqNotes(String sectionCoReqNotes) {
        SectionCoReqNotes = sectionCoReqNotes;
    }

    @JsonProperty("SSS_SectionsID")
    public String getSSS_SectionsID() {
        return SSS_SectionsID;
    }

    public void setSSS_SectionsID(String SSS_SectionsID) {
        this.SSS_SectionsID = SSS_SectionsID;
    }

    @JsonProperty("Term_JSS")
    public String getTerm_JSS() {
        return Term_JSS;
    }

    public void setTerm_JSS(String term_JSS) {
        Term_JSS = term_JSS;
    }

    @JsonProperty("Repeatable")
    public String getRepeatable() {
        return Repeatable;
    }

    public void setRepeatable(String repeatable) {
        Repeatable = repeatable;
    }

    @JsonProperty("SectionDetails")
    public String getSectionDetails() {
        return SectionDetails;
    }

    public void setSectionDetails(String sectionDetails) {
        SectionDetails = sectionDetails;
    }
}

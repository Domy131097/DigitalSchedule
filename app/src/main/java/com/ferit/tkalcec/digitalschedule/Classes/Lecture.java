package com.ferit.tkalcec.digitalschedule.Classes;

import java.util.Date;

public class Lecture {
    private String courseName;
    private String typeOfLecture;
    private Date startTime;
    private Date endTime;
    private String location;
    private String hall;
    private String courseId;
    private String facultyId;
    private String userId;
    public String documentId;

    public Lecture() {
        this.courseName = null;
        this.typeOfLecture = null;
        this.startTime = null;
        this.endTime = null;
        this.location = null;
        this.hall = null;
        this.courseId = null;
        this.facultyId = null;
        this.userId = null;
        this.documentId = null;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTypeOfLecture() {
        return typeOfLecture;
    }

    public void setTypeOfLecture(String typeOfLecture) {
        this.typeOfLecture = typeOfLecture;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

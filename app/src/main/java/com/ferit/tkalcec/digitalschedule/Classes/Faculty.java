package com.ferit.tkalcec.digitalschedule.Classes;

import com.google.firebase.Timestamp;

public class Faculty {
    private String userId;
    private String name;
    private String study;
    private String year;
    private Timestamp timestamp;
    private String documentId;

    public Faculty() { }

    public Faculty (String facultyName, String studyOfFaculty, String yearOfStudy, String userId, Timestamp timestamp) {
        this.userId = userId;
        this.name = facultyName;
        this.study = studyOfFaculty;
        this.year = yearOfStudy;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getStudy() {
        return study;
    }

    public String getYear() {
        return year;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

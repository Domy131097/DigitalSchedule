package com.ferit.tkalcec.digitalschedule.Classes;

import com.google.firebase.Timestamp;

public class Course {
    private String name;
    private String code;
    private String holder;
    private int exercisesHours;
    private int lecturesHours;
    private int designHours;
    private int auditoryHours;
    private Timestamp timestamp;
    private String userID;
    private String facultyId;
    private String documentId;

    public Course () {
        this.name = null;
        this.code = null;
        this.holder = null;
        this.exercisesHours = 0;
        this.lecturesHours = 0;
        this.designHours = 0;
        this.auditoryHours = 0;
        this.timestamp = null;
        this.userID = null;
        this.facultyId = null;
        this.documentId = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public int getExercisesHours() {
        return exercisesHours;
    }

    public void setExercisesHours(int exercisesHours) {
        this.exercisesHours = exercisesHours;
    }

    public int getLecturesHours() {
        return lecturesHours;
    }

    public void setLecturesHours(int lecturesHours) {
        this.lecturesHours = lecturesHours;
    }

    public int getDesignHours() {
        return designHours;
    }

    public void setDesignHours(int designHours) {
        this.designHours = designHours;
    }

    public int getAuditoryHours() {
        return auditoryHours;
    }

    public void setAuditoryHours(int auditoryHours) {
        this.auditoryHours = auditoryHours;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return name + code;
    }

}

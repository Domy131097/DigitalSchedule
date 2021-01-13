package com.ferit.tkalcec.digitalschedule;

import com.ferit.tkalcec.digitalschedule.Activities.NewFacultyActivity;

import org.junit.Test;

import static org.junit.Assert.*;

public class NewFacultyValidInputTest {
    private NewFacultyActivity newFacultyActivity = new NewFacultyActivity();
    @Test
    public void studyOfYear_isCorrect() {
        assertTrue(newFacultyActivity.isValidYearOfStudy("1"));
    }

    @Test
    public void studyOfYear_isNegative() {
        assertFalse(newFacultyActivity.isValidYearOfStudy("-1"));
    }

    @Test
    public void studyOfYear_isZero() {
        assertFalse(newFacultyActivity.isValidYearOfStudy("0"));
    }

    @Test
    public void studyOfYear_isEmpty() {
        assertFalse(newFacultyActivity.isValidYearOfStudy(""));
    }

    @Test
    public void studyOfYear_isTooHigh() {
        assertFalse(newFacultyActivity.isValidYearOfStudy("20"));
    }
}

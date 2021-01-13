package com.ferit.tkalcec.digitalschedule;

import com.ferit.tkalcec.digitalschedule.Activities.NewCourseActivity;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewCourseValidInputTest {
    NewCourseActivity newCourseActivity = new NewCourseActivity();

    @Test
    public void hour_isCorrect() {
        assertTrue(newCourseActivity.isValidHours("1"));
    }

    @Test
    public void hour_isNegative() {
        assertFalse(newCourseActivity.isValidHours("-1"));
    }

    @Test
    public void hour_isEmpty() {
        assertFalse(newCourseActivity.isValidHours(""));
    }

}

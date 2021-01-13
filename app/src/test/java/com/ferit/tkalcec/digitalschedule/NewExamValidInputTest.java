package com.ferit.tkalcec.digitalschedule;

import com.ferit.tkalcec.digitalschedule.Activities.NewExamActivity;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewExamValidInputTest {
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private NewExamActivity newExamActivity = new NewExamActivity();

    @Test
    public void date_isCorrect() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        String dateOfLecture = lectureDateFormat.format(currentDate);
        String lectureStartTime = "23:59";

        assertTrue(newExamActivity.isValidDate(dateOfLecture, lectureStartTime));
    }

    @Test
    public void date_isIncorrect() {
        String dateOfLecture = "01.01.2019.";
        String lectureStartTime = "00:00";

        assertFalse(newExamActivity.isValidDate(dateOfLecture, lectureStartTime));
    }
}

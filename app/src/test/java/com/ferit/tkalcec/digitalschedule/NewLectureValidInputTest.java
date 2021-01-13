package com.ferit.tkalcec.digitalschedule;

import com.ferit.tkalcec.digitalschedule.Activities.NewLectureActivity;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewLectureValidInputTest {
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private NewLectureActivity newLectureActivity = new NewLectureActivity();

    @Test
    public void date_isCorrect() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        String dateOfLecture = lectureDateFormat.format(currentDate);
        String lectureStartTime = "23:59";

        assertTrue(newLectureActivity.isValidDate(dateOfLecture, lectureStartTime));
    }

    @Test
    public void date_isIncorrect() {
        String dateOfLecture = "01.01.2019.";
        String lectureStartTime = "00:00";

        assertFalse(newLectureActivity.isValidDate(dateOfLecture, lectureStartTime));
    }

    @Test
    public void time_isCorrect() {
        String lectureStartTime = "15:00";
        String lectureEndTime = "16:00";

        assertTrue(newLectureActivity.isValidTime(lectureStartTime, lectureEndTime));
    }

    @Test
    public void time_isIncorrect() {
        String lectureStartTime = "16:00";
        String lectureEndTime = "15:00";

        assertFalse(newLectureActivity.isValidTime(lectureStartTime, lectureEndTime));
    }

}

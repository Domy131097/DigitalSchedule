package com.ferit.tkalcec.digitalschedule;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManagement {
    private static String PREFS_FILE = "FacultyPreferences";

    public void saveFacultyData(Context context, String facultyName, String studyOfFaculty, String yearOfStudy, String facultyId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", facultyName);
        editor.putString("Study", yearOfStudy + ". " + studyOfFaculty);
        editor.putString("FacultyId", facultyId);
        editor.apply();
    }

    public String getFacultyName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("Name", null);
    }

    public String getStudyOfFaculty(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("Study", null);
    }

    public String getFacultyId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("FacultyId", null);
    }

    public void deleteFacultyData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Name");
        editor.remove("Study");
        editor.remove("FacultyId");
        editor.commit();
    }
}

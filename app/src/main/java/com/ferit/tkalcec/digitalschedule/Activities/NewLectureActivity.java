package com.ferit.tkalcec.digitalschedule.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.ferit.tkalcec.digitalschedule.Classes.Course;
import com.ferit.tkalcec.digitalschedule.Fragments.DatePickerFragment;
import com.ferit.tkalcec.digitalschedule.Fragments.TimePickerFragment;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class NewLectureActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("dd.MM.yyyy.'T'HH:mm'Z'");
    private static final SimpleDateFormat lectureTimeFormat = new SimpleDateFormat("HH:mm");

    private Spinner spinnerCourseSelect;
    private Spinner spinnerTypeSelect;
    private Button btnAddNewLecture;
    private TextView tvLectureDateSelect;
    private TextView tvLectureStartTimeSelect;
    private TextView tvLectureEndTimeSelect;
    private EditText etLectureLocation;
    private EditText etLectureLocationHall;
    private ProgressBar progressAddLecture;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private PreferenceManagement prefsManagement;
    private ArrayList<Course> coursesList = new ArrayList<Course>();

    private boolean startTimeSelected = false;
    private boolean endTimeSelected = false;
    private String currentCourseName = null;
    private String currentDocumentId = null;

    private ArrayAdapter<String> lectureTypeAdapter;
    private ArrayAdapter<Course> courseNameAdapter;

    private String errorMsg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lecture);

        prefsManagement = new PreferenceManagement();

        setUpFirebase();
        setUpUI();
        setUpSpinners();
    }

    private void setUpFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI() {
        this.spinnerCourseSelect = (Spinner) findViewById(R.id.spinnerCourseSelect);
        this.spinnerTypeSelect = (Spinner) findViewById(R.id.spinnerTypeSelect);
        this.btnAddNewLecture = (Button) findViewById(R.id.btnAddNewLecture);
        this.tvLectureDateSelect = (TextView) findViewById(R.id.tvDateSelect);
        this.tvLectureStartTimeSelect = (TextView) findViewById(R.id.tvStartTimeSelect);
        this.tvLectureEndTimeSelect = (TextView) findViewById(R.id.tvEndTimeSelect);
        this.etLectureLocation = (EditText) findViewById(R.id.etLectureLocation);
        this.etLectureLocationHall = (EditText) findViewById(R.id.etSelectLocationHall);
        this.progressAddLecture = (ProgressBar) findViewById(R.id.progressAddLecture) ;

        this.btnAddNewLecture.setOnClickListener(this);
        this.tvLectureDateSelect.setOnClickListener(this);
        this.tvLectureStartTimeSelect.setOnClickListener(this);
        this.tvLectureEndTimeSelect.setOnClickListener(this);
    }

    private void setUpSpinners() {
        Course defaultCourse = new Course();
        defaultCourse.setName("Odabir kolegija...");
        defaultCourse.setCode("");
        coursesList.add(defaultCourse);

        loadCoursesNames(coursesList);

        lectureTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lectureTypeList));
        lectureTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeSelect.setAdapter(lectureTypeAdapter);

        courseNameAdapter = new ArrayAdapter<Course>(this, android.R.layout.simple_spinner_item, coursesList);
        courseNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseSelect.setAdapter(courseNameAdapter);

    }

    private void loadCoursesNames(final ArrayList<Course> coursesList) {
        if(firebaseUser != null) {
            CollectionReference courseCollectionReference = firebaseFirestore.collection("courses");
            Query loadCourseQuery = courseCollectionReference.whereEqualTo("facultyId", prefsManagement.getFacultyId(this));

            loadCourseQuery.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(QUERY_SNAPSHOT_TAG, "Error:" + e.getMessage());
                    } else {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Course course = doc.getDocument().toObject(Course.class);
                                    coursesList.add(course);
                                    course.setDocumentId(doc.getDocument().getId());
                                }
                            }
                        }
                        getIncomingIntent();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tvDateSelect:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
                break;
            case R.id.tvStartTimeSelect:
                DialogFragment startTimePicker = new TimePickerFragment();
                startTimePicker.show(getSupportFragmentManager(), "Start time picker");
                startTimeSelected = true;
                endTimeSelected = false;
                break;
            case R.id.tvEndTimeSelect:
                DialogFragment endTimePicker = new TimePickerFragment();
                endTimePicker.show(getSupportFragmentManager(), "End time picker");
                startTimeSelected = false;
                endTimeSelected = true;
                break;
            case R.id.btnAddNewLecture:
                errorMsg = "Molimo popunite sva polja";

                Course course = getSelectedCourse(view);
                String typeOfLecture = getSelectedTypeOfLecture(view);
                String lectureLocationBuilding = this.etLectureLocation.getText().toString();
                String lectureLocationHall = this.etLectureLocationHall.getText().toString();
                String dateOfLecture = this.tvLectureDateSelect.getText().toString();
                String lectureStartTime = this.tvLectureStartTimeSelect.getText().toString();
                String lectureEndTime = this.tvLectureEndTimeSelect.getText().toString();

                if (!course.getName().equalsIgnoreCase("Odabir kolegija...") && !typeOfLecture.equalsIgnoreCase("Odabir vrste...") && !dateOfLecture.equalsIgnoreCase("Odaberi") && !lectureStartTime.equalsIgnoreCase("Odaberi") && !lectureEndTime.equalsIgnoreCase("Odaberi")
                && !TextUtils.isEmpty(lectureLocationBuilding) && !TextUtils.isEmpty(lectureLocationHall) && isValidDate(dateOfLecture, lectureStartTime) && isValidTime(lectureStartTime, lectureEndTime)) {
                    String courseName = course.getName() + course.getCode();
                    String facultyId = course.getFacultyId(), courseId = course.getDocumentId();

                    progressAddLecture.setVisibility(View.VISIBLE);

                    Map<String, Object> lectures = new HashMap<>();
                    lectures.put("courseName", courseName);
                    lectures.put("typeOfLecture", typeOfLecture);
                    lectures.put("startTime", getDateFromString(dateOfLecture + "T" + lectureStartTime + "Z"));
                    lectures.put("endTime", getDateFromString(dateOfLecture + "T" + lectureEndTime + "Z"));
                    lectures.put("location", lectureLocationBuilding);
                    lectures.put("hall", lectureLocationHall);
                    lectures.put("courseId", courseId);
                    lectures.put("facultyId", facultyId);
                    lectures.put("userId", firebaseUser.getUid());

                    if(btnAddNewLecture.getText().equals("IZRADI PREDAVANJE")) {
                        addNewLecture(lectures);
                    }
                    else if (btnAddNewLecture.getText().equals("SPREMI PROMJENE")) {
                        updateLecture(lectures);
                    }

                    progressAddLecture.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public boolean isValidDate(String dateOfLecture, String lectureStartTime) {
        Calendar calendar = Calendar.getInstance();
        Date date = getDateFromString(dateOfLecture + "T" + lectureStartTime + "Z");
        Date currentDate = calendar.getTime();

        if(date.before(currentDate)) {
            errorMsg = "Datum ili vrijeme početka predavanja nije ipravno.";
            return false;
        }

        return true;
    }

    public boolean isValidTime(String lectureStartTime, String lectureEndTime) {
        try {
            if(lectureTimeFormat.parse(lectureEndTime).before(lectureTimeFormat.parse(lectureStartTime))) {
                errorMsg = "Vrijeme početka predavanja mora biti prije vremena završetka.";
                return false;
            }
        } catch (ParseException e) {
            Log.d("LECTURE", "Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void addNewLecture(Map<String, Object> lectures) {
        firebaseFirestore.collection("lectures")
                .add(lectures)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewLectureActivity.this, "Uspješno ste dodali novo predavanje.", Toast.LENGTH_LONG).show();
                        sendToLectures();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewLectureActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLecture(Map<String, Object> lectures) {
        final DocumentReference lectureReference = firebaseFirestore.collection("lectures").document(currentDocumentId);
        lectureReference.set(lectures, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewLectureActivity.this, "Uspješno ste izmjenili " + currentCourseName, Toast.LENGTH_SHORT).show();
                sendToLectures();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewLectureActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToLectures() {
        Intent lectureIntent = new Intent(NewLectureActivity.this, LecturesActivity.class);
        startActivity(lectureIntent);
    }

    private Course getSelectedCourse(View view) {
        Course course = (Course) spinnerCourseSelect.getSelectedItem();
        return course;
    }

    private String getSelectedTypeOfLecture(View view) {
        String typeOfLecture = (String) spinnerTypeSelect.getSelectedItem();
        return typeOfLecture;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        if (this.startTimeSelected)
            tvLectureStartTimeSelect.setText(hour + ":" + min);
        if (this.endTimeSelected)
            tvLectureEndTimeSelect.setText(hour + ":" + min);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month += 1;
        tvLectureDateSelect.setText(day + "." + month + "." + year + ".");
    }

    public Date getDateFromString(String dateToConvert){
        try {
            Date date = lectureDateFormat.parse(dateToConvert);
            return date ;
        } catch (ParseException e){
            return null ;
        }
    }

    public void getIncomingIntent() {
        if(getIntent().hasExtra("courseName") && getIntent().hasExtra("typeOfLecture") && getIntent().hasExtra("date") && getIntent().hasExtra("startTime") && getIntent().hasExtra("endTime") && getIntent().hasExtra("location") && getIntent().hasExtra("hall") && getIntent().hasExtra("documentId")) {
            currentCourseName = getIntent().getStringExtra("courseName");
            String typeOfLecture = getIntent().getStringExtra("typeOfLecture");
            String date = getIntent().getStringExtra("date");
            String startTime = getIntent().getStringExtra("startTime");
            String endTime = getIntent().getStringExtra("endTime");
            String location = getIntent().getStringExtra("location");
            String hall = getIntent().getStringExtra("hall");
            currentDocumentId = getIntent().getStringExtra("documentId");

            spinnerCourseSelect.setSelection(getCourseIndex(courseNameAdapter, currentCourseName));
            spinnerTypeSelect.setSelection(lectureTypeAdapter.getPosition(typeOfLecture));
            tvLectureDateSelect.setText(date);
            tvLectureStartTimeSelect.setText(startTime);
            tvLectureEndTimeSelect.setText(endTime);
            etLectureLocation.setText(location);
            etLectureLocationHall.setText(hall);
            btnAddNewLecture.setText("SPREMI PROMJENE");
        }
    }

    private int getCourseIndex(ArrayAdapter<Course> courseNameAdapter, String currentCourseName) {
        for(int i = 0; i < courseNameAdapter.getCount(); i++) {
            if (courseNameAdapter.getItem(i).toString().equalsIgnoreCase(currentCourseName)) {
                return i;
            }
        }
        return 0;
    }

}

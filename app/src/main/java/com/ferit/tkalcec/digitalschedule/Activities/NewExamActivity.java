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

public class NewExamActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("dd.MM.yyyy.'T'HH:mm'Z'");

    private ProgressBar progressAddExam;
    private Spinner spinnerCourseSelect;
    private Spinner spinnerTypeOfExam;
    private TextView tvDateSelect;
    private TextView tvStartTimeSelect;
    private EditText etLocationOfExam;
    private EditText etHallOfExam;
    private Button btnAddNewExam;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private PreferenceManagement prefsManagement;
    private String currentCourseName;
    private String currentDocumentId;

    private ArrayList<Course> coursesList = new ArrayList<>();
    private ArrayAdapter<Course> courseNameAdapter;
    private ArrayAdapter<String> examTypeAdapter;

    private String errorMsg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exam);

        prefsManagement = new PreferenceManagement();

        setUpFirebase();
        setUpUI();
        setUpSpinner();
    }

    private void setUpFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI() {
        this.progressAddExam = (ProgressBar) findViewById(R.id.progressAddExam);
        this.spinnerCourseSelect = (Spinner) findViewById(R.id.spinnerExamCourseSelect);
        this.spinnerTypeOfExam = (Spinner) findViewById(R.id.spinnerTypeOfExam);
        this.tvDateSelect = (TextView) findViewById(R.id.tvExamDateSelect);
        this.tvStartTimeSelect = (TextView) findViewById(R.id.tvExamStartTimeSelect);
        this.etLocationOfExam = (EditText) findViewById(R.id.etExamLocation);
        this.etHallOfExam = (EditText) findViewById(R.id.etExamHall);
        this.btnAddNewExam = (Button) findViewById(R.id.btnAddNewExam);

        this.tvDateSelect.setOnClickListener(this);
        this.tvStartTimeSelect.setOnClickListener(this);
        this.btnAddNewExam.setOnClickListener(this);
    }

    private void setUpSpinner() {
        Course defaultCourse = new Course();
        defaultCourse.setName("Odabir kolegija...");
        defaultCourse.setCode("");
        coursesList.add(defaultCourse);

        loadCoursesNames();

        courseNameAdapter = new ArrayAdapter<Course>(this, android.R.layout.simple_spinner_item, coursesList);
        courseNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseSelect.setAdapter(courseNameAdapter);

        examTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.examTypeList));
        examTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfExam.setAdapter(examTypeAdapter);

    }

    private void loadCoursesNames() {
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

    private void getIncomingIntent() {
        if(getIntent().hasExtra("courseName") && getIntent().hasExtra("date") && getIntent().hasExtra("type") && getIntent().hasExtra("startTime") && getIntent().hasExtra("location") && getIntent().hasExtra("hall") && getIntent().hasExtra("documentId")) {
            currentCourseName = getIntent().getStringExtra("courseName");
            String type = getIntent().getStringExtra("type");
            String date = getIntent().getStringExtra("date");
            String startTime = getIntent().getStringExtra("startTime");
            String location = getIntent().getStringExtra("location");
            String hall = getIntent().getStringExtra("hall");
            currentDocumentId = getIntent().getStringExtra("documentId");

            spinnerCourseSelect.setSelection(getCourseIndex(courseNameAdapter, currentCourseName));
            spinnerTypeOfExam.setSelection(examTypeAdapter.getPosition(type));
            tvDateSelect.setText(date);
            tvStartTimeSelect.setText(startTime);
            etLocationOfExam.setText(location);
            etHallOfExam.setText(hall);
            btnAddNewExam.setText("SPREMI PROMJENE");
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tvExamDateSelect:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
                break;
            case R.id.tvExamStartTimeSelect:
                DialogFragment startTimePicker = new TimePickerFragment();
                startTimePicker.show(getSupportFragmentManager(), "Start time picker");
                break;
            case R.id.btnAddNewExam:
                errorMsg = "Molimo popunite sva polja.";

                Course course = getSelectedCourse(view);
                String typeOfExam = getSelectedTypeOfExam(view);
                String examLocationBuilding = this.etLocationOfExam.getText().toString();
                String examLocationHall = this.etHallOfExam.getText().toString();
                String dateOfExam = this.tvDateSelect.getText().toString();
                String examStartTime = this.tvStartTimeSelect.getText().toString();

                if (!course.getName().equalsIgnoreCase("Odabir kolegija...") && !typeOfExam.equalsIgnoreCase("Odabir vrste...") && !dateOfExam.equalsIgnoreCase("Odaberi") && !examStartTime.equalsIgnoreCase("Odaberi")
                        && !TextUtils.isEmpty(examLocationBuilding) && !TextUtils.isEmpty(examLocationHall) && isValidDate(dateOfExam, examStartTime)) {
                    String courseName = course.getName() + course.getCode();
                    String facultyId = course.getFacultyId(), courseId = course.getDocumentId();

                    progressAddExam.setVisibility(View.VISIBLE);

                    Map<String, Object> exams = new HashMap<>();
                    exams.put("courseName", courseName);
                    exams.put("type", typeOfExam);
                    exams.put("startTime", getDateFromString(dateOfExam + "T" + examStartTime + "Z"));
                    exams.put("location", examLocationBuilding);
                    exams.put("hall", examLocationHall);
                    exams.put("courseId", courseId);
                    exams.put("facultyId", facultyId);
                    exams.put("userId", firebaseUser.getUid());

                    if(btnAddNewExam.getText().equals("DODAJ ISPIT")) {
                        addNewExam(exams);
                    }
                    else if (btnAddNewExam.getText().equals("SPREMI PROMJENE")) {
                        updateExam(exams);
                    }

                    progressAddExam.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String getSelectedTypeOfExam(View view) {
        String typeOfExam = (String) spinnerTypeOfExam.getSelectedItem();
        return typeOfExam;
    }

    public boolean isValidDate(String dateOfExam, String examStartTime) {
        Calendar calendar = Calendar.getInstance();
        Date date = getDateFromString(dateOfExam + "T" + examStartTime + "Z");
        Date currentDate = calendar.getTime();

        if(date.before(currentDate)) {
            errorMsg = "Datum ili vrijeme početka ispita nije ipravno.";
            return false;
        }

        return true;
    }

    private void addNewExam(Map<String, Object> exams) {
        firebaseFirestore.collection("exams")
                .add(exams)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewExamActivity.this, "Uspješno ste dodali novi ispit.", Toast.LENGTH_LONG).show();
                        sendToExam();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewExamActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateExam(Map<String, Object> exams) {
        final DocumentReference lectureReference = firebaseFirestore.collection("exams").document(currentDocumentId);
        lectureReference.set(exams, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewExamActivity.this, "Uspješno ste izmjenili ispit iz kolegija " + currentCourseName, Toast.LENGTH_SHORT).show();
                sendToExam();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewExamActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToExam() {
        Intent examIntent = new Intent(NewExamActivity.this, ExamActivity.class);
        startActivity(examIntent);
    }

    private Course getSelectedCourse(View view) {
        Course course = (Course) spinnerCourseSelect.getSelectedItem();
        return course;
    }

    public Date getDateFromString(String dateToConvert){
        try {
            Date date = lectureDateFormat.parse(dateToConvert);
            return date ;
        } catch (ParseException e){
            return null ;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        tvStartTimeSelect.setText(hour + ":" + min);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month += 1;
        tvDateSelect.setText(day + "." + month + "." + year + ".");
    }
}

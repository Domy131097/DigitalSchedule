package com.ferit.tkalcec.digitalschedule.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class NewCourseActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCourseName;
    private EditText etCodeOfCourse;
    private EditText etHolderOfCourse;
    private EditText etLecturesHours;
    private EditText etExercisesHours;
    private EditText etAuditoryHours;
    private EditText etDesignHours;
    private Button btnAddNewCourse;
    private ProgressBar progressAddCourse;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private PreferenceManagement prefsManagement;

    private String currentCourseName;
    private String currentDocumentId;

    private String errorMsg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        prefsManagement = new PreferenceManagement();

        setUpFirebase();
        setUpUI();
        getIncomingIntent();
    }

    private void setUpFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setUpUI() {
        this.etCourseName = (EditText) findViewById(R.id.etCourseName);
        this.etCodeOfCourse = (EditText) findViewById(R.id.etCodeOfCourse);
        this.etHolderOfCourse = (EditText) findViewById(R.id.etHolderOfCourse);
        this.etLecturesHours = (EditText) findViewById(R.id.etLecturesHours);
        this.etExercisesHours = (EditText) findViewById(R.id.etExercisesHours);
        this.etAuditoryHours = (EditText) findViewById(R.id.etAuditoryHours);
        this.etDesignHours = (EditText) findViewById(R.id.etDesignHours);
        this.btnAddNewCourse = (Button) findViewById(R.id.btnAddNewCourse);
        this.progressAddCourse = (ProgressBar) findViewById(R.id.progressAddCourse);

        this.btnAddNewCourse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        errorMsg = "Molimo popunite sva polja.";

        String courseName = this.etCourseName.getText().toString();
        String codeOfCourse = this.etCodeOfCourse.getText().toString();
        String holderOfCourse = this.etHolderOfCourse.getText().toString();
        String lecturesHours = this.etLecturesHours.getText().toString();
        String exercisesHours = this.etExercisesHours.getText().toString();
        String auditoryHours = this.etAuditoryHours.getText().toString();
        String designHours = this.etDesignHours.getText().toString();

        if(firebaseUser != null) {
            if(!TextUtils.isEmpty(courseName) && !TextUtils.isEmpty(codeOfCourse) && !TextUtils.isEmpty(holderOfCourse) && isValidHours(lecturesHours) && isValidHours(exercisesHours) && isValidHours(auditoryHours) && isValidHours(designHours)) {
                progressAddCourse.setVisibility(View.VISIBLE);

                Map<String, Object> courses = new HashMap<>();
                courses.put("name", courseName);
                courses.put("code", codeOfCourse);
                courses.put("holder", holderOfCourse);
                courses.put("exercisesHours", Integer.parseInt(exercisesHours));
                courses.put("lecturesHours", Integer.parseInt(lecturesHours));
                courses.put("designHours", Integer.parseInt(designHours));
                courses.put("auditoryHours", Integer.parseInt(auditoryHours));
                courses.put("timestamp", FieldValue.serverTimestamp());
                courses.put("userId", firebaseUser.getUid());
                courses.put("facultyId", getFacultyId());

                if(btnAddNewCourse.getText().equals("DODAJ KOLEGIJ")) {
                    addNewCourse(courses);
                }
                else if (btnAddNewCourse.getText().equals("SPREMI PROMJENE")) {
                    updateCourse(courses);
                }

                progressAddCourse.setVisibility(View.INVISIBLE);
            }
            else {
                Toast.makeText(NewCourseActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean isValidHours(String hours) {
        int hour;
        try {
            hour = Integer.parseInt(hours);
        } catch (NumberFormatException error) {
            Log.d("COURSE", "The hours is not entered.");
            return false;
        }
        if(hour < 0) {
            errorMsg = "Broj sati ne može biti negativan.";
            return false;
        }

        return true;
    }

    private String getFacultyId() {
        if(this.prefsManagement != null) {
            String facultyId = this.prefsManagement.getFacultyId(this);
            if (facultyId != null) {
                return facultyId;
            }
            else {
                return null;
            }
        }
        else
            return null;
    }

    private void addNewCourse(Map<String, Object> courses) {
        firebaseFirestore.collection("courses")
                .add(courses)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewCourseActivity.this, "Uspješno ste dodali novi kolegij.", Toast.LENGTH_LONG).show();
                        sendToCourse();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewCourseActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCourse(Map<String, Object> courses) {
        final DocumentReference courseReference = firebaseFirestore.collection("courses").document(currentDocumentId);
        courseReference.set(courses, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewCourseActivity.this, "Uspješno ste izmjenili " + currentCourseName, Toast.LENGTH_SHORT).show();
                sendToCourse();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewCourseActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToCourse() {
        Intent coursesIntent = new Intent(NewCourseActivity.this, CoursesActivity.class);
        startActivity(coursesIntent);
    }

    public void getIncomingIntent() {
        if(getIntent().hasExtra("courseName") && getIntent().hasExtra("codeOfCourse") && getIntent().hasExtra("holderOfCourse") && getIntent().hasExtra("lecturesHours") && getIntent().hasExtra("exercisesHours") && getIntent().hasExtra("auditoryHours") && getIntent().hasExtra("designHours") && getIntent().hasExtra("documentId")) {
            currentCourseName = getIntent().getStringExtra("courseName");
            String codeOfCourse = getIntent().getStringExtra("codeOfCourse");
            String holderOfCourse = getIntent().getStringExtra("holderOfCourse");
            String lecturesHours = getIntent().getStringExtra("lecturesHours");
            String exercisesHours = getIntent().getStringExtra("exercisesHours");
            String auditoryHours = getIntent().getStringExtra("auditoryHours");
            String designHours = getIntent().getStringExtra("designHours");
            currentDocumentId = getIntent().getStringExtra("documentId");

            etCourseName.setText(currentCourseName);
            etCodeOfCourse.setText(codeOfCourse);
            etHolderOfCourse.setText(holderOfCourse);
            etLecturesHours.setText(lecturesHours);
            etExercisesHours.setText(exercisesHours);
            etAuditoryHours.setText(auditoryHours);
            etDesignHours.setText(designHours);
            btnAddNewCourse.setText("SPREMI PROMJENE");
        }
    }
}

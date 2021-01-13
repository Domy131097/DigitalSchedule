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

import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class NewFacultyActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etFacultyName;
    private EditText etStudyOfFaculty;
    private EditText etYearOfStudy;
    private Button btnAddNewFaculty;
    private ProgressBar progressAddFaculty;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String currentUserId = null;
    private String currentDocumentId = null;
    private String currentFacultyName = null;

    private String errorMsg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_faculty);

        setUpFirebase();
        setUpUI();
        getIncomingIntent();
    }

    private void setUpFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        this.currentUserId = firebaseUser.getUid();
    }

    private void setUpUI() {
        this.etFacultyName = (EditText) findViewById(R.id.etFacultyName);
        this.etStudyOfFaculty = (EditText) findViewById(R.id.etStudyOfFaculty);
        this.etYearOfStudy = (EditText) findViewById(R.id.etYearOfStudy);
        this.btnAddNewFaculty = (Button) findViewById(R.id.btnAddNewFaculty);
        this.progressAddFaculty = (ProgressBar) findViewById(R.id.progressAddFaculty);

        this.btnAddNewFaculty.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.errorMsg = "Molimo popunite sva polja.";

        String facultyName = etFacultyName.getText().toString();
        String studyOfFaculty = etStudyOfFaculty.getText().toString();
        String yearOfStudy = etYearOfStudy.getText().toString();

        if(firebaseUser != null) {
            if (!TextUtils.isEmpty(facultyName) && !TextUtils.isEmpty(studyOfFaculty) && isValidYearOfStudy(yearOfStudy)) {
                progressAddFaculty.setVisibility(View.VISIBLE);

                Map<String, Object> faculties = new HashMap<>();
                faculties.put("name", facultyName);
                faculties.put("study", studyOfFaculty);
                faculties.put("year", yearOfStudy);
                faculties.put("timestamp", FieldValue.serverTimestamp());
                faculties.put("userId", currentUserId);

                if(btnAddNewFaculty.getText().equals("DODAJ FAKULTET")) {
                    addNewFaculty(faculties);
                }
                else if (btnAddNewFaculty.getText().equals("SPREMI PROMJENE")) {
                    updateFaculty(faculties);
                }

                progressAddFaculty.setVisibility(View.INVISIBLE);
            }
            else {
                Toast.makeText(NewFacultyActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValidYearOfStudy(String yearOfStudy) {
        int year;
        try {
            year = Integer.parseInt(yearOfStudy);
        } catch (NumberFormatException error) {
            Log.d("FACULTY", "The year is not entered.");
            return false;
        }
        if (year == 0) {
            errorMsg = "Godina studija ne može biti 0.";
            return false;
        }
        else if(year < 0) {
            errorMsg = "Godina studija ne može biti negativna.";
            return false;
        }
        else if(year > 6) {
            errorMsg = "Godina studija ne može biti veća od 6.";
            return false;
        }

        return true;
    }

    private void updateFaculty(final Map<String, Object> faculties) {
        final DocumentReference courseReference = firebaseFirestore.collection("faculties").document(currentDocumentId);
        courseReference.set(faculties, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewFacultyActivity.this, "Uspješno ste izmjenili " + currentFacultyName, Toast.LENGTH_SHORT).show();
                sendToFaculty();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewFacultyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewFaculty(Map<String, Object> faculties) {
        firebaseFirestore.collection("faculties").add(faculties)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewFacultyActivity.this, "Uspješno ste dodali novi fakultet.", Toast.LENGTH_LONG).show();
                        sendToFaculty();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewFacultyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendToFaculty() {
        Intent facultyIntent = new Intent(NewFacultyActivity.this, FacultyActivity.class);
        startActivity(facultyIntent);
    }

    public void getIncomingIntent() {
        if(getIntent().hasExtra("facultyName") && getIntent().hasExtra("studyOfFaculty") && getIntent().hasExtra("yearOfStudy") && getIntent().hasExtra("documentId")) {
            currentFacultyName = getIntent().getStringExtra("facultyName");
            String studyOfFaculty = getIntent().getStringExtra("studyOfFaculty");
            String yearOfStudy = getIntent().getStringExtra("yearOfStudy");
            currentDocumentId = getIntent().getStringExtra("documentId");

            etFacultyName.setText(currentFacultyName);
            etStudyOfFaculty.setText(studyOfFaculty);
            etYearOfStudy.setText(yearOfStudy);
            btnAddNewFaculty.setText("SPREMI PROMJENE");
        }
    }


}

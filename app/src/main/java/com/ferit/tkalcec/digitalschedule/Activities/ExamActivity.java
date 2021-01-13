package com.ferit.tkalcec.digitalschedule.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Adapters.ExamAdapter;
import com.ferit.tkalcec.digitalschedule.Adapters.LectureAdapter;
import com.ferit.tkalcec.digitalschedule.Classes.Exam;
import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener, ExamAdapter.OnExamContextMenuListener, ExamAdapter.OnExamListClickListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private FloatingActionButton fabAddExam;
    private RecyclerView rvExamsListsView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ArrayList<Exam> examsList = new ArrayList<>();
    private ExamAdapter examAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private Context context;
    private PreferenceManagement prefsManagement;
    private int currentPosition;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        context = getApplicationContext();
        prefsManagement = new PreferenceManagement();

        setUpFirebase();
        setUpUI();
        setUpView();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI() {
        this.fabAddExam = (FloatingActionButton) findViewById(R.id.fabAddExam);
        this.rvExamsListsView = (RecyclerView) findViewById(R.id.rvExamsListView);

        this.fabAddExam.setOnClickListener(this);
        this.rvExamsListsView.setHasFixedSize(true);
    }

    private void setUpView() {
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.examsList = new ArrayList<>();

        loadExamData();

        this.examAdapter = new ExamAdapter(examsList, this, this);

        this.rvExamsListsView.addItemDecoration(this.mItemDecoration);
        this.rvExamsListsView.setLayoutManager(this.mLayoutManager);
        this.rvExamsListsView.setAdapter(this.examAdapter);
    }

    private void loadExamData() {
        if(firebaseUser != null) {
            CollectionReference courseCollectionReference = firebaseFirestore.collection("exams");
            Query loadCourseQuery = courseCollectionReference.whereEqualTo("facultyId", prefsManagement.getFacultyId(context))
                    .whereEqualTo("userId", firebaseUser.getUid());

            loadCourseQuery.orderBy("startTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(QUERY_SNAPSHOT_TAG, "Error:" + e.getMessage());
                    } else {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Exam exam = doc.getDocument().toObject(Exam.class);
                                    examsList.add(exam);
                                    exam.setDocumentId(doc.getDocument().getId());

                                    examAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        sendToNewExam();
    }

    private void sendToNewExam() {
        Intent newExamIntent = new Intent(ExamActivity.this, NewExamActivity.class);
        startActivity(newExamIntent);
    }

    @Override
    public void onExamContextMenuListener(int adapterPosition, ContextMenu contextMenu) {
        contextMenu.add(adapterPosition, 127, 0, "Uredi");
        contextMenu.add(adapterPosition, 128, 0, "Izbriši");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Exam exam;
        switch(item.getItemId()) {
            case 127:
                exam = this.examsList.get(item.getGroupId());
                Intent newExamActivity = new Intent(ExamActivity.this, NewExamActivity.class);
                newExamActivity.putExtra("courseName", exam.getCourseName());
                newExamActivity.putExtra("type", exam.getType());
                newExamActivity.putExtra("date", dateFormat.format(exam.getStartTime()));
                newExamActivity.putExtra("startTime", timeFormat.format(exam.getStartTime()));
                newExamActivity.putExtra("location", exam.getLocation());
                newExamActivity.putExtra("hall", exam.getHall());
                newExamActivity.putExtra("documentId", exam.getDocumentId());
                startActivity(newExamActivity);
                return true;
            case 128:
                currentPosition = item.getGroupId();
                exam = this.examsList.get(item.getGroupId());
                showDeleteExamDialog(exam.getCourseName(), exam.getDocumentId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showDeleteExamDialog(final String courseName, final String documentId) {
        AlertDialog.Builder deleteExamDialog = new AlertDialog.Builder(ExamActivity.this);
        deleteExamDialog.setTitle("Brisanje ispita");
        deleteExamDialog.setMessage("Jeste li sigurni da želite obrisati ispit iz kolegija " + courseName + "?");
        deleteExamDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteExam(courseName, documentId);
            }
        });
        deleteExamDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteExamDialog.show();
    }

    private void deleteExam(final String courseName, String documentId) {
        CollectionReference collectionReference = firebaseFirestore.collection("exams");
        collectionReference.document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ExamActivity.this, "Uspješno ste obrisali ispit iz kolegija " + courseName, Toast.LENGTH_LONG).show();
                        removeExamFromView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExamActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeExamFromView() {
        this.examAdapter.removeExam(currentPosition);
    }

    @Override
    public void onExamClickListener(int position) {
        Exam exam = this.examsList.get(position);
        showExamInformationDialog(exam);
    }

    private void showExamInformationDialog(Exam exam) {
        AlertDialog.Builder examInformationDialog = new AlertDialog.Builder(ExamActivity.this);
        examInformationDialog.setTitle(exam.getCourseName());
        examInformationDialog.setMessage("Vrsta: " + exam.getType() + "\n"
                + "Datum: " + dateFormat.format(exam.getStartTime()) + "\n"
                + "Vrijeme početka: " + timeFormat.format(exam.getStartTime()) + "h" + "\n"
                + "Lokacija: " + exam.getLocation() + "\n"
                + "Dvorana: " + exam.getHall());
        examInformationDialog.setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        examInformationDialog.show();
    }
}

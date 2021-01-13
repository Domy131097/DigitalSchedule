package com.ferit.tkalcec.digitalschedule.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ferit.tkalcec.digitalschedule.Adapters.LectureAdapter;
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

public class LecturesActivity extends AppCompatActivity implements View.OnClickListener, LectureAdapter.OnLectureContextMenuListener, LectureAdapter.OnLectureListClickListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private FloatingActionButton fabAddLecture;
    private RecyclerView rvLectureListView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ArrayList<Lecture> lecturesList = new ArrayList();
    private LectureAdapter lectureAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private PreferenceManagement prefsManagement;
    private int currentPosition;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        prefsManagement = new PreferenceManagement();

        setUpFirebase();
        setUpUI();
        loadLecturesData();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI() {
        this.fabAddLecture = (FloatingActionButton) findViewById(R.id.fabAddLecture);
        this.rvLectureListView = (RecyclerView) findViewById(R.id.rvLectureListView);

        this.fabAddLecture.setOnClickListener(this);
        this.rvLectureListView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View view) {
        sendToNewLecture();
    }

    private void loadLecturesData() {
        Context context = getApplicationContext();
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.lecturesList = new ArrayList<>();

        if(firebaseUser != null) {
            CollectionReference courseCollectionReference = firebaseFirestore.collection("lectures");
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
                                    Lecture lecture = doc.getDocument().toObject(Lecture.class);
                                    lecturesList.add(lecture);
                                    lecture.setDocumentId(doc.getDocument().getId());

                                    lectureAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });

            this.lectureAdapter = new LectureAdapter(lecturesList, this, this);

            this.rvLectureListView.addItemDecoration(this.mItemDecoration);
            this.rvLectureListView.setLayoutManager(this.mLayoutManager);
            this.rvLectureListView.setAdapter(this.lectureAdapter);
        }
    }

    private void sendToNewLecture() {
        Intent newLectureIntent = new Intent(LecturesActivity.this, NewLectureActivity.class);
        startActivity(newLectureIntent);
    }

    @Override
    public void onLectureContextMenuListener(int adapterPosition, ContextMenu contextMenu) {
        contextMenu.add(adapterPosition, 125, 0, "Uredi");
        contextMenu.add(adapterPosition, 126, 0, "Izbriši");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Lecture lecture;
        switch(item.getItemId()) {
            case 125:
                lecture = this.lecturesList.get(item.getGroupId());
                Intent newLectureActivity = new Intent(this, NewLectureActivity.class);
                newLectureActivity.putExtra("courseName", lecture.getCourseName());
                newLectureActivity.putExtra("typeOfLecture", lecture.getTypeOfLecture());
                newLectureActivity.putExtra("date", dateFormat.format(lecture.getStartTime()));
                newLectureActivity.putExtra("startTime", timeFormat.format(lecture.getStartTime()));
                newLectureActivity.putExtra("endTime", timeFormat.format(lecture.getEndTime()));
                newLectureActivity.putExtra("location", lecture.getLocation());
                newLectureActivity.putExtra("hall", lecture.getHall());
                newLectureActivity.putExtra("documentId", lecture.getDocumentId());
                startActivity(newLectureActivity);
                return true;
            case 126:
                currentPosition = item.getGroupId();
                lecture = this.lecturesList.get(item.getGroupId());
                showDeleteLectureDialog(lecture.getCourseName(), lecture.getDocumentId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showDeleteLectureDialog(final String courseName, final String documentId) {
        AlertDialog.Builder deleteLectureDialog = new AlertDialog.Builder(LecturesActivity.this);
        deleteLectureDialog.setTitle("Brisanje predavanja");
        deleteLectureDialog.setMessage("Jeste li sigurni da želite obrisati predavanje za kolegij " + courseName + "?");
        deleteLectureDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteLecture(courseName, documentId);
            }
        });
        deleteLectureDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteLectureDialog.show();
    }

    private void deleteLecture(final String courseName, String documentId) {
        CollectionReference collectionReference = firebaseFirestore.collection("lectures");
        collectionReference.document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LecturesActivity.this, "Uspješno ste obrisali predavanje iz kolegija " + courseName, Toast.LENGTH_LONG).show();
                        removeLectureFromView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LecturesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeLectureFromView() {
        this.lectureAdapter.removeLecture(currentPosition);
    }

    @Override
    public void onLectureClickListener(int position) {
        Lecture lecture = this.lecturesList.get(position);
        showLectureInformationDialog(lecture);
    }

    private void showLectureInformationDialog(Lecture lecture) {
        AlertDialog.Builder lectureInformationDialog = new AlertDialog.Builder(LecturesActivity.this);
        lectureInformationDialog.setTitle(lecture.getCourseName());
        lectureInformationDialog.setMessage("Vrsta: " + lecture.getTypeOfLecture() + "\n"
                + "Datum: " + dateFormat.format(lecture.getStartTime()) + "\n"
                + "Vrijeme početka: " + timeFormat.format(lecture.getStartTime()) + "h" + "\n"
                + "Vrijeme završteka: " + timeFormat.format(lecture.getEndTime()) + "h" + "\n"
                + "Lokacija: " + lecture.getLocation() + "\n"
                + "Dvorana: " + lecture.getHall());
        lectureInformationDialog.setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        lectureInformationDialog.show();
    }
}

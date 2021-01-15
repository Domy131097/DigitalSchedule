package com.ferit.tkalcec.digitalschedule.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ferit.tkalcec.digitalschedule.Adapters.FacultyAdapter;
import com.ferit.tkalcec.digitalschedule.Classes.Faculty;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FacultyActivity extends AppCompatActivity implements View.OnClickListener, FacultyAdapter.OnFacultiesListListener, FacultyAdapter.OnFacultyContextMenuListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference facultyCollectionReference;

    private FloatingActionButton fabAddFaculty;
    private RecyclerView rvFacultiesList;

    private ArrayList<Faculty> facultiesList;
    private FacultyAdapter facultyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private PreferenceManagement mPrefsManagement;
    private Context context;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mPrefsManagement = new PreferenceManagement();
        context = getApplicationContext();

        setUpUI();
        setUpView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            sendToLogin();
        }
        checkPrefs();
    }

    private void checkPrefs() {
        if(this.mPrefsManagement != null) {
            String facultyName = this.mPrefsManagement.getFacultyName(this);
            String studyOfFaculty = this.mPrefsManagement.getStudyOfFaculty(this);
            if (facultyName != null && studyOfFaculty != null) {
                sendToMain();
            }
        }
    }

    private void setUpView() {
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.facultiesList = new ArrayList<>();

        loadFacultiesData();

        this.facultyAdapter = new FacultyAdapter(facultiesList, this, this);

        this.rvFacultiesList.addItemDecoration(this.mItemDecoration);
        this.rvFacultiesList.setLayoutManager(this.mLayoutManager);
        this.rvFacultiesList.setAdapter(this.facultyAdapter);
    }

    private void loadFacultiesData() {
        if(mAuth.getCurrentUser() != null) {
            facultyCollectionReference = firebaseFirestore.collection("faculties");
            Query loadFacultyQuery = facultyCollectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

            loadFacultyQuery.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.d(QUERY_SNAPSHOT_TAG,"Error:" + e.getMessage());
                    }
                    else {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Faculty faculty = doc.getDocument().toObject(Faculty.class);
                                    facultiesList.add(faculty);
                                    faculty.setDocumentId(doc.getDocument().getId());

                                    facultyAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    private void setUpUI() {
        this.fabAddFaculty = (FloatingActionButton) findViewById(R.id.fabAddFaculty);
        this.rvFacultiesList = (RecyclerView) findViewById(R.id.rvFacultyList);

        this.fabAddFaculty.setOnClickListener(this);
        this.rvFacultiesList.setHasFixedSize(true);
    }

    @Override
    public void onClick(View view) {
        addNewFaculty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater facultyMenuInflater = this.getMenuInflater();
        facultyMenuInflater.inflate(R.menu.faculty_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.actionLogout):
                logOut();
                return true;
            default:
                return false;
        }
    }

    private void addNewFaculty() {
        Intent newFacultyIntent = new Intent(FacultyActivity.this, NewFacultyActivity.class);
        startActivity(newFacultyIntent);
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    public void sendToLogin() {
        Intent loginIntent = new Intent(FacultyActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public void sendToMain() {
        Intent mainIntent = new Intent(FacultyActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onFacultiesListClick(int position) {
        Faculty faculty = this.facultiesList.get(position);
        mPrefsManagement.saveFacultyData(getApplicationContext(), faculty.getName(), faculty.getStudy(), faculty.getYear(), faculty.getDocumentId());
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public void onFacultyContextMenuListener(int position, ContextMenu menu) {
        menu.add(position, 121, 0, "Uredi");
        menu.add(position, 122, 0, "Izbriši");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Faculty faculty;
        switch(item.getItemId()) {
            case 121:
                faculty = this.facultiesList.get(item.getGroupId());
                Intent newFacultyActivity = new Intent(this, NewFacultyActivity.class);
                newFacultyActivity.putExtra("facultyName", faculty.getName());
                newFacultyActivity.putExtra("studyOfFaculty", faculty.getStudy());
                newFacultyActivity.putExtra("yearOfStudy", faculty.getYear());
                newFacultyActivity.putExtra("documentId", faculty.getDocumentId());
                startActivity(newFacultyActivity);
                return true;
            case 122:
                currentPosition = item.getGroupId();
                faculty = this.facultiesList.get(item.getGroupId());
                showDeleteFacultyDialog(faculty.getName(), faculty.getDocumentId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showDeleteFacultyDialog(final String facultyName, final String documentId) {
        AlertDialog.Builder deleteFacultyDialog = new AlertDialog.Builder(FacultyActivity.this);
        deleteFacultyDialog.setTitle("Brisanje fakulteta");
        deleteFacultyDialog.setMessage("Jeste li sigurni da želite obrisati " + facultyName + "?");
        deleteFacultyDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFaculty(facultyName, documentId);
            }
        });
        deleteFacultyDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteFacultyDialog.show();
    }

    private void deleteFaculty(final String facultyName, final String documentId) {
        firebaseFirestore.collection("faculties").document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Faculties", "Faculty " + facultyName + " is deleted.");
                        deleteCourses(facultyName, documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FacultyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteCourses(final String facultyName, final String documentId) {
        final CollectionReference courseCollectionReference = firebaseFirestore.collection("courses");
        Query courseQuery = courseCollectionReference.whereEqualTo("facultyId", documentId);
        courseQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    courseCollectionReference.document(doc.getId()).delete();
                                }
                                Log.d("Faculties", "Courses in " + facultyName + " are deleted.");
                                deleteLectures(facultyName, documentId);
                            } else {
                                Toast.makeText(FacultyActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Faculties", "Courses are not found in " + facultyName + ".");
                            deleteLectures(facultyName, documentId);
                        }
                    }
                });
    }

    private void deleteLectures(final String facultyName, final String documentId) {
        final CollectionReference lectureCollectionReference = firebaseFirestore.collection("lectures");
        Query lectureQuery = lectureCollectionReference.whereEqualTo("facultyId", documentId);
        lectureQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    lectureCollectionReference.document(doc.getId()).delete();
                                }
                                Log.d("Faculties", "Lectures in " + facultyName + " are deleted.");
                                deleteExams(facultyName, documentId);
                            } else {
                                Toast.makeText(FacultyActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Faculties", "Lectures are not found in " + facultyName + ".");
                            deleteExams(facultyName, documentId);
                        }
                    }
                });
    }

    private void deleteExams(final String facultyName, final String documentId) {
        final CollectionReference examCollectionReference = firebaseFirestore.collection("exams");
        Query examQuery = examCollectionReference.whereEqualTo("facultyId", documentId);
        examQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    examCollectionReference.document(doc.getId()).delete();
                                }
                                Log.d("Faculties", "Exams in " + facultyName + " are deleted.");
                                Toast.makeText(FacultyActivity.this, "Uspješno ste obrisali " + facultyName, Toast.LENGTH_SHORT).show();
                                removeCourseFromView();
                            } else {
                                Toast.makeText(FacultyActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Faculties", "Exams are not found in " + facultyName + ".");
                            Toast.makeText(FacultyActivity.this, "Uspješno ste obrisali " + facultyName, Toast.LENGTH_SHORT).show();
                            removeCourseFromView();
                        }
                    }
                });
    }

    private void removeCourseFromView() {
        this.facultyAdapter.removeFaculty(currentPosition);
    }
}

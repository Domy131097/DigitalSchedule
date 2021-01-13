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

import com.ferit.tkalcec.digitalschedule.Adapters.CourseAdapter;
import com.ferit.tkalcec.digitalschedule.Classes.Course;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
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

public class CoursesActivity extends AppCompatActivity implements View.OnClickListener, CourseAdapter.OnCourseContextMenuListener, CourseAdapter.OnCourseListClickListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private RecyclerView rvCourseListView;
    private FloatingActionButton fabAddCourse;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ArrayList<Course> coursesList = new ArrayList<>();
    private CourseAdapter courseAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private PreferenceManagement prefsManagement;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        prefsManagement = new PreferenceManagement();
        context = getApplicationContext();

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
        this.rvCourseListView = (RecyclerView) findViewById(R.id.rvCourseListView);
        this.fabAddCourse = (FloatingActionButton) findViewById(R.id.fabAddCourse);

        this.fabAddCourse.setOnClickListener(this);
        this.rvCourseListView.setHasFixedSize(true);
    }

    private void setUpView() {
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.coursesList = new ArrayList<>();

        loadCoursesData();

        this.courseAdapter = new CourseAdapter(coursesList, this, this);

        this.rvCourseListView.addItemDecoration(this.mItemDecoration);
        this.rvCourseListView.setLayoutManager(this.mLayoutManager);
        this.rvCourseListView.setAdapter(this.courseAdapter);
    }

    @Override
    public void onClick(View view) {
        sendToNewCourse();
    }

    private void loadCoursesData() {
        if(firebaseUser != null) {
            CollectionReference courseCollectionReference = firebaseFirestore.collection("courses");
            Query loadCourseQuery = courseCollectionReference.whereEqualTo("facultyId", prefsManagement.getFacultyId(context))
                    .whereEqualTo("userId", firebaseUser.getUid());

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

                                    courseAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void sendToNewCourse() {
        Intent newCourseIntent = new Intent(CoursesActivity.this, NewCourseActivity.class);
        startActivity(newCourseIntent);
    }

    @Override
    public void onCourseContextMenuListener(int adapterPosition, ContextMenu contextMenu) {
        contextMenu.add(adapterPosition, 123, 0, "Uredi");
        contextMenu.add(adapterPosition, 124, 0, "Izbriši");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Course course;
        switch(item.getItemId()) {
            case 123:
                course = this.coursesList.get(item.getGroupId());
                Intent newCourseActivity = new Intent(this, NewCourseActivity.class);
                newCourseActivity.putExtra("courseName", course.getName());
                newCourseActivity.putExtra("codeOfCourse", course.getCode());
                newCourseActivity.putExtra("holderOfCourse", course.getHolder());
                newCourseActivity.putExtra("exercisesHours", Integer.toString(course.getExercisesHours()));
                newCourseActivity.putExtra("lecturesHours", Integer.toString(course.getLecturesHours()));
                newCourseActivity.putExtra("auditoryHours", Integer.toString(course.getAuditoryHours()));
                newCourseActivity.putExtra("designHours", Integer.toString(course.getDesignHours()));
                newCourseActivity.putExtra("documentId", course.getDocumentId());
                startActivity(newCourseActivity);
                return true;
            case 124:
                course = this.coursesList.get(item.getGroupId());
                showDeleteCourseDialog(course.getName(), course.getDocumentId(), item.getGroupId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCourseClickListener(int position) {
        Course course = this.coursesList.get(position);
        showCourseInformationDialog(course);
    }

    private void showCourseInformationDialog(Course course) {
        AlertDialog.Builder courseInformationDialog = new AlertDialog.Builder(CoursesActivity.this);
        courseInformationDialog.setTitle(course.getName());
        courseInformationDialog.setMessage("Šifra: " + course.getCode() + "\n"
        + "Nositelj: " + course.getHolder() + "\n"
        + "Predavanja: " + course.getLecturesHours() + "h" + "\n"
        + "Laboratorijske vježbe: " + course.getExercisesHours() + "\n"
        + "Auditorne vježbe: " + course.getAuditoryHours() + "\n"
        + "Konstrukcijske vježbe: " + course.getDesignHours());
        courseInformationDialog.setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        courseInformationDialog.show();
    }

    private void showDeleteCourseDialog(final String courseName, final String documentId, final int position) {
        AlertDialog.Builder deleteCourseDialog = new AlertDialog.Builder(CoursesActivity.this);
        deleteCourseDialog.setTitle("Brisanje kolegija");
        deleteCourseDialog.setMessage("Jeste li sigurni da želite obrisati " + courseName + "?");
        deleteCourseDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCourse(courseName, documentId, position);
            }
        });
        deleteCourseDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteCourseDialog.show();
    }

    private void deleteCourse(final String courseName, final String documentId, final int position) {
        CollectionReference collectionReference = firebaseFirestore.collection("courses");
        collectionReference.document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Courses", "Course " + courseName + " is deleted.");
                        deleteLectures(courseName, documentId, position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CoursesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteLectures(final String courseName, final String documentId, final int position) {
        final CollectionReference lectureCollectionReference = firebaseFirestore.collection("lectures");
        Query lectureQuery = lectureCollectionReference.whereEqualTo("courseId", documentId);
        lectureQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    lectureCollectionReference.document(doc.getId()).delete();
                                }
                                Log.d("Courses", "Lectures in " + courseName + " are deleted.");
                                deleteExams(courseName, documentId, position);
                            } else {
                                Toast.makeText(CoursesActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Courses", "Lectures are not found in " + courseName + ".");
                            deleteExams(courseName, documentId, position);
                        }
                    }
                });
    }

    private void deleteExams(final String courseName, String documentId, final int position) {
        final CollectionReference examCollectionReference = firebaseFirestore.collection("exams");
        Query examQuery = examCollectionReference.whereEqualTo("courseId", documentId);
        examQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    examCollectionReference.document(doc.getId()).delete();
                                }
                                Log.d("Courses", "Exams in " + courseName + " are deleted.");
                                Toast.makeText(CoursesActivity.this, "Uspješno ste obrisali kolegij " + courseName, Toast.LENGTH_SHORT).show();
                                removeCourseFromView(position);
                            } else {
                                Toast.makeText(CoursesActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Courses", "Exams are not found in " + courseName + ".");
                            Toast.makeText(CoursesActivity.this, "Uspješno ste obrisali kolegij " + courseName, Toast.LENGTH_SHORT).show();
                            removeCourseFromView(position);
                        }
                    }
                });
    }

    private void removeCourseFromView(int position) {
        this.courseAdapter.removeCourse(position);
    }
}

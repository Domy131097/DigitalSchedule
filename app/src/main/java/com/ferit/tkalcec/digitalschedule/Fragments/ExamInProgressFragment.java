package com.ferit.tkalcec.digitalschedule.Fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferit.tkalcec.digitalschedule.Adapters.ExamsAdapter;
import com.ferit.tkalcec.digitalschedule.Classes.Exam;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamInProgressFragment extends Fragment implements ExamsAdapter.OnExamsContextMenuListener, ExamsAdapter.OnExamsListClickListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";
    private static final SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private RecyclerView rvExamInProgressList;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ArrayList<Exam> examsList = new ArrayList<>();
    private ArrayList<Exam> currentExamsList = new ArrayList<>();
    private ExamsAdapter examsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private PreferenceManagement prefsManagement;
    private Context context;
    private Calendar calendar;

    public ExamInProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        View view = inflater.inflate(R.layout.fragment_exam_in_progress, container, false);

        prefsManagement = new PreferenceManagement();
        calendar = Calendar.getInstance();
        context = getContext();

        setUpFirebase();
        setUpUI(view);
        loadExamsData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExamsInProgress();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI(View view) {
        this.rvExamInProgressList = (RecyclerView) view.findViewById(R.id.rvExamInProgressList);

        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.rvExamInProgressList.addItemDecoration(this.mItemDecoration);
        this.rvExamInProgressList.setLayoutManager(this.mLayoutManager);
    }

    private void loadExamsData() {
        if(firebaseUser != null) {
            if(examsList.size() == 0) {
                CollectionReference courseCollectionReference = firebaseFirestore.collection("exams");
                Query loadCourseQuery = courseCollectionReference.whereEqualTo("facultyId", prefsManagement.getFacultyId(context))
                        .whereEqualTo("userId", firebaseUser.getUid());

                loadCourseQuery.orderBy("startTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    }
                                }
                            }
                            loadExamsInProgress();
                        }
                    }
                });
            }
        }
    }

    private void loadExamsInProgress() {
        if(examsList != null) {
            currentExamsList.clear();
            for (int i = 0; i < examsList.size(); i++) {
                Exam exam = examsList.get(i);
                String loadedDate = currentDateFormat.format(exam.getStartTime());
                String currentDate = currentDateFormat.format(calendar.getTime());
                try {
                    if (currentDateFormat.parse(loadedDate).equals(currentDateFormat.parse(currentDate)) || currentDateFormat.parse(loadedDate).after(currentDateFormat.parse(currentDate))) {
                        currentExamsList.add(exam);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        this.examsAdapter = new ExamsAdapter(currentExamsList, this, this, true);

        this.rvExamInProgressList.setAdapter(this.examsAdapter);
    }

    @Override
    public void onExamsContextMenuListener(int adapterPosition, ContextMenu contextMenu) {

    }

    @Override
    public void onExamsClickListener(int position) {

    }
}

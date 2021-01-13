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
import android.widget.ImageButton;
import android.widget.TextView;

import com.ferit.tkalcec.digitalschedule.Adapters.ScheduleAdapter;
import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthsFragment extends Fragment implements View.OnClickListener, ScheduleAdapter.OnScheduleContextMenuListener, ScheduleAdapter.OnScheduleListClickListener {

    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private RecyclerView rvScheduleMonthsList;
    private ImageButton ibPreviousMonth;
    private ImageButton ibNextMonth;
    private TextView tvCurrentMonth;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ArrayList<Lecture> lecturesList = new ArrayList<>();
    private ArrayList<Lecture> currentLecturesList = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private PreferenceManagement prefsManagement;
    private Context context;

    private static final SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy.");
    private static final SimpleDateFormat searchDateFormat = new SimpleDateFormat("MM.yyyy.");

    private Calendar calendar;
    private String currentFormattedDate = null;


    public MonthsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_months, container, false);

        prefsManagement = new PreferenceManagement();
        calendar = Calendar.getInstance();
        context = getContext();

        setUpFirebase();
        setUpUI(view);
        loadLecturesData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentMonthLecture();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI(View view) {
        this.rvScheduleMonthsList = (RecyclerView) view.findViewById(R.id.rvScheduleMonthsList);
        this.ibPreviousMonth = (ImageButton) view.findViewById(R.id.ibPreviousMonth);
        this.ibNextMonth = (ImageButton) view.findViewById(R.id.ibNextMonth);
        this.tvCurrentMonth = (TextView) view.findViewById(R.id.tvCurrentMonth);

        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.rvScheduleMonthsList.addItemDecoration(this.mItemDecoration);
        this.rvScheduleMonthsList.setLayoutManager(this.mLayoutManager);

        this.ibPreviousMonth.setOnClickListener(this);
        this.ibNextMonth.setOnClickListener(this);

        currentFormattedDate = currentDateFormat.format(calendar.getTime());
        tvCurrentMonth.setText(monthsOfYearInString() + " - " + currentFormattedDate);
    }

    private String monthsOfYearInString() {
        int monthOfYear = calendar.get(Calendar.MONTH);

        String yearMonth = null;

        if (Calendar.JANUARY == monthOfYear) yearMonth = "Sječanj";
        else if (Calendar.FEBRUARY == monthOfYear) yearMonth = "Veljača";
        else if (Calendar.MARCH == monthOfYear) yearMonth = "Ožujak";
        else if (Calendar.APRIL == monthOfYear) yearMonth = "Travanj";
        else if (Calendar.MAY == monthOfYear) yearMonth = "Svibanj";
        else if (Calendar.JUNE == monthOfYear) yearMonth = "Lipanj";
        else if (Calendar.JULY == monthOfYear) yearMonth = "Srpanj";
        else if (Calendar.AUGUST == monthOfYear) yearMonth = "Kolovoz";
        else if (Calendar.SEPTEMBER == monthOfYear) yearMonth = "Rujan";
        else if (Calendar.OCTOBER == monthOfYear) yearMonth = "Listopad";
        else if (Calendar.NOVEMBER == monthOfYear) yearMonth = "Studeni";
        else if (Calendar.DECEMBER == monthOfYear) yearMonth = "Prosinac";

        return yearMonth;
    }

    private void loadLecturesData() {
        if(firebaseUser != null) {
            if(lecturesList.size() == 0) {
                CollectionReference courseCollectionReference = firebaseFirestore.collection("lectures");
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
                                        Lecture lecture = doc.getDocument().toObject(Lecture.class);

                                        lecturesList.add(lecture);
                                        lecture.setDocumentId(doc.getDocument().getId());
                                    }
                                }
                            }
                            loadCurrentMonthLecture();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ibPreviousMonth:
                calendar.add(Calendar.MONTH, -1);

                currentFormattedDate = currentDateFormat.format(calendar.getTime());
                tvCurrentMonth.setText(monthsOfYearInString() + " - " + currentFormattedDate);

                loadCurrentMonthLecture();
                break;
            case R.id.ibNextMonth:
                calendar.add(Calendar.MONTH, 1);

                currentFormattedDate = currentDateFormat.format(calendar.getTime());
                tvCurrentMonth.setText(monthsOfYearInString() + " - " + currentFormattedDate);

                loadCurrentMonthLecture();
                break;
            default:
                break;
        }
    }

    private void loadCurrentMonthLecture() {
        if(lecturesList != null) {
            currentLecturesList.clear();
            rvScheduleMonthsList.removeAllViewsInLayout();
            for (int i = 0; i < lecturesList.size(); i++) {
                Lecture lecture = lecturesList.get(i);
                if (searchDateFormat.format(lecture.getStartTime()).equals(searchDateFormat.format(calendar.getTime()))) {
                    currentLecturesList.add(lecture);


                }
            }
        }
        this.scheduleAdapter = new ScheduleAdapter(currentLecturesList, this, this, true);

        this.rvScheduleMonthsList.setAdapter(this.scheduleAdapter);
    }

    @Override
    public void onScheduleContextMenuListener(int adapterPosition, ContextMenu contextMenu) {

    }

    @Override
    public void onScheduleClickListener(int position) {

    }
}

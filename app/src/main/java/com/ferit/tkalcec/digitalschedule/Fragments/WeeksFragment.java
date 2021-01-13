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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeeksFragment extends Fragment implements View.OnClickListener, ScheduleAdapter.OnScheduleContextMenuListener, ScheduleAdapter.OnScheduleListClickListener {

    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";

    private RecyclerView rvScheduleWeeksList;
    private ImageButton ibPreviousWeek;
    private ImageButton ibNextWeek;
    private TextView tvCurrentWeek;

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

    private static final SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private Calendar calendar;
    private String firstDateOfCurrentWeek = null;
    private String lastDateOfCurrentWeek = null;


    public WeeksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weeks, container, false);

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
        loadCurrentWeekLecture();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI(View view) {
        this.rvScheduleWeeksList = (RecyclerView) view.findViewById(R.id.rvScheduleWeeksList);
        this.ibPreviousWeek = (ImageButton) view.findViewById(R.id.ibPreviousWeek);
        this.ibNextWeek = (ImageButton) view.findViewById(R.id.ibNextWeek);
        this.tvCurrentWeek = (TextView) view.findViewById(R.id.tvCurrentWeek);

        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.rvScheduleWeeksList.addItemDecoration(this.mItemDecoration);
        this.rvScheduleWeeksList.setLayoutManager(this.mLayoutManager);

        this.ibPreviousWeek.setOnClickListener(this);
        this.ibNextWeek.setOnClickListener(this);

        setFirstAndLastDaysOfCurrentWeek();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ibPreviousWeek:
                calendar.add(Calendar.DATE, -13);
                firstDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());

                calendar.add(Calendar.DATE, 6);
                lastDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());

                tvCurrentWeek.setText(firstDateOfCurrentWeek + " - " + lastDateOfCurrentWeek);

                loadCurrentWeekLecture();
                break;
            case R.id.ibNextWeek:
                calendar.add(Calendar.DATE, 1);
                firstDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());

                calendar.add(Calendar.DATE, 6);
                lastDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());

                tvCurrentWeek.setText(firstDateOfCurrentWeek + " - " + lastDateOfCurrentWeek);

                loadCurrentWeekLecture();
                break;
            default:
                break;
        }
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
                            loadCurrentWeekLecture();
                        }
                    }
                });
            }
        }
    }

    private void loadCurrentWeekLecture() {
        if(lecturesList != null) {
            currentLecturesList.clear();
            rvScheduleWeeksList.removeAllViewsInLayout();
            for (int i = 0; i < lecturesList.size(); i++) {
                Lecture lecture = lecturesList.get(i);
                String currentDate = currentDateFormat.format(lecture.getStartTime());
                try {
                    if (currentDateFormat.parse(currentDate).equals(currentDateFormat.parse(firstDateOfCurrentWeek)) || currentDateFormat.parse(currentDate).equals(currentDateFormat.parse(lastDateOfCurrentWeek)) ||
                            (currentDateFormat.parse(currentDate).after(currentDateFormat.parse(firstDateOfCurrentWeek)) && currentDateFormat.parse(currentDate).before(currentDateFormat.parse(lastDateOfCurrentWeek)))) {
                        currentLecturesList.add(lecture);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        this.scheduleAdapter = new ScheduleAdapter(currentLecturesList, this, this, true);

        this.rvScheduleWeeksList.setAdapter(this.scheduleAdapter);
    }

    private void setFirstAndLastDaysOfCurrentWeek() {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        firstDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        lastDateOfCurrentWeek = currentDateFormat.format(calendar.getTime());

        tvCurrentWeek.setText(firstDateOfCurrentWeek + " - " + lastDateOfCurrentWeek);
    }

    @Override
    public void onScheduleContextMenuListener(int adapterPosition, ContextMenu contextMenu) {

    }

    @Override
    public void onScheduleClickListener(int position) {

    }
}

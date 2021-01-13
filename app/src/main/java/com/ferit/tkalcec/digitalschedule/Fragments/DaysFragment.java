package com.ferit.tkalcec.digitalschedule.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.ferit.tkalcec.digitalschedule.Adapters.ScheduleAdapter;
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


public class DaysFragment extends Fragment implements View.OnClickListener, ScheduleAdapter.OnScheduleListClickListener, ScheduleAdapter.OnScheduleContextMenuListener {
    private static final String QUERY_SNAPSHOT_TAG = "QueryDocumentSnapshots";
    private static final SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private RecyclerView rvScheduleDaysList;
    private ImageButton ibPreviousDay;
    private ImageButton ibNextDay;
    private TextView tvCurrentDay;

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

    private Calendar calendar;
    private String currentFormattedDate = null;

    public DaysFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_days, container, false);

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
        loadCurrentDayLecture();
    }

    private void setUpFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUpUI(View view) {
        this.rvScheduleDaysList = (RecyclerView) view.findViewById(R.id.rvScheduleDaysList);
        this.ibPreviousDay = (ImageButton) view.findViewById(R.id.ibPreviousDay);
        this.ibNextDay = (ImageButton) view.findViewById(R.id.ibNextDay);
        this.tvCurrentDay = (TextView) view.findViewById(R.id.tvCurrentDay);

        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context, 0);

        this.rvScheduleDaysList.addItemDecoration(this.mItemDecoration);
        this.rvScheduleDaysList.setLayoutManager(this.mLayoutManager);

        this.ibPreviousDay.setOnClickListener(this);
        this.ibNextDay.setOnClickListener(this);

        currentFormattedDate = currentDateFormat.format(calendar.getTime());
        tvCurrentDay.setText(dayOfWeekInString() + " - " + currentFormattedDate);
    }

    private String dayOfWeekInString() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String weekDay = null;

        if (Calendar.MONDAY == dayOfWeek) weekDay = "Ponedjeljak";
        else if (Calendar.TUESDAY == dayOfWeek) weekDay = "Utorak";
        else if (Calendar.WEDNESDAY == dayOfWeek) weekDay = "Srijeda";
        else if (Calendar.THURSDAY == dayOfWeek) weekDay = "Četvrtak";
        else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Petak";
        else if (Calendar.SATURDAY == dayOfWeek) weekDay = "Subota";
        else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Nedjelja";

        return weekDay;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ibPreviousDay:
                calendar.add(Calendar.DATE, -1);

                currentFormattedDate = currentDateFormat.format(calendar.getTime());
                tvCurrentDay.setText(dayOfWeekInString() + " - " + currentFormattedDate);

                loadCurrentDayLecture();
                break;
            case R.id.ibNextDay:
                calendar.add(Calendar.DATE, 1);

                currentFormattedDate = currentDateFormat.format(calendar.getTime());
                tvCurrentDay.setText(dayOfWeekInString() + " - " + currentFormattedDate);

                loadCurrentDayLecture();
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
                            loadCurrentDayLecture();
                        }
                    }
                });
            }
        }
    }

    private void loadCurrentDayLecture() {
        if(lecturesList != null) {
            currentLecturesList.clear();
            for (int i = 0; i < lecturesList.size(); i++) {
                Lecture lecture = lecturesList.get(i);
                if (currentDateFormat.format(lecture.getStartTime()).equals(currentDateFormat.format(calendar.getTime()))) {
                    currentLecturesList.add(lecture);
                }
            }
        }
        this.scheduleAdapter = new ScheduleAdapter(currentLecturesList, this, this, false);

        this.rvScheduleDaysList.setAdapter(this.scheduleAdapter);
    }

    @Override
    public void onScheduleClickListener(int position) {

    }

    @Override
    public void onScheduleContextMenuListener(int adapterPosition, ContextMenu contextMenu) {

    }
}

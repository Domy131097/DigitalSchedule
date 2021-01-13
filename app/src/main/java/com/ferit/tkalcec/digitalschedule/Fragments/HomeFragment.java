package com.ferit.tkalcec.digitalschedule.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ferit.tkalcec.digitalschedule.Activities.CoursesActivity;
import com.ferit.tkalcec.digitalschedule.Activities.FacultyActivity;
import com.ferit.tkalcec.digitalschedule.Activities.LecturesActivity;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.ferit.tkalcec.digitalschedule.Activities.ExamActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView tvFacultyName;
    private TextView tvStudyOfFaculty;
    private CardView cvCoursesListBtn;
    private CardView cvLecturesListBtn;
    private CardView cvTestsListBtn;
    private CardView cvExitFromFacultyBtn;

    private PreferenceManagement mPrefManagement;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mPrefManagement = new PreferenceManagement();

        setUpUI(view);

        return view;
    }

    private void setUpPrefs() {
        if(this.mPrefManagement != null) {
            String facultyName = this.mPrefManagement.getFacultyName(getContext());
            String studyOfFaculty = this.mPrefManagement.getStudyOfFaculty(getContext());
            if (facultyName != null && studyOfFaculty != null) {
                this.tvFacultyName.setText(facultyName);
                this.tvStudyOfFaculty.setText(studyOfFaculty);
            }
            else {
                sendToFaculty();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpPrefs();
    }

    private void sendToFaculty() {
        Intent facultyIntent = new Intent(getActivity(), FacultyActivity.class);
        startActivity(facultyIntent);
    }

    private void setUpUI(View view) {
        this.tvFacultyName = (TextView) view.findViewById(R.id.tvFacultyName);
        this.tvStudyOfFaculty = (TextView) view.findViewById(R.id.tvStudyOfFaculty);
        this.cvCoursesListBtn = (CardView) view.findViewById(R.id.cvCoursesListBtn);
        this.cvLecturesListBtn = (CardView) view.findViewById(R.id.cvLecturesListBtn);
        this.cvTestsListBtn = (CardView) view.findViewById(R.id.cvTestsListBtn);
        this.cvExitFromFacultyBtn = (CardView) view.findViewById(R.id.cvExitFromFacultyBtn);

        this.cvCoursesListBtn.setOnClickListener(this);
        this.cvLecturesListBtn.setOnClickListener(this);
        this.cvTestsListBtn.setOnClickListener(this);
        this.cvExitFromFacultyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.cvCoursesListBtn):
                Intent courseActivityIntent = new Intent(getActivity(), CoursesActivity.class);
                startActivity(courseActivityIntent);
                break;
            case (R.id.cvLecturesListBtn):
                Intent lecturesActivityIntent = new Intent(getActivity(), LecturesActivity.class);
                startActivity(lecturesActivityIntent);
                break;
            case (R.id.cvTestsListBtn):
                Intent testsActivityIntent = new Intent(getActivity(), ExamActivity.class);
                startActivity(testsActivityIntent);
                break;
            case (R.id.cvExitFromFacultyBtn):
                if(this.mPrefManagement != null) {
                    mPrefManagement.deleteFacultyData(getContext());
                }
                Intent facultyActivityIntent = new Intent(getActivity(), FacultyActivity.class);
                startActivity(facultyActivityIntent);
                break;
        }
    }
}

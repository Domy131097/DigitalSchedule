package com.ferit.tkalcec.digitalschedule.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ferit.tkalcec.digitalschedule.Adapters.PageAdapter;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExamsFragment extends Fragment {
    private ViewPager vpExamController;
    private TabLayout tabLayoutExam;

    public ExamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exams, container, false);

        setUpUI(view);
        setUpPager();
        return view;
    }

    private void setUpUI(View view) {
        this.vpExamController = (ViewPager) view.findViewById(R.id.vpExamController);
        this.tabLayoutExam = (TabLayout) view.findViewById(R.id.tabLayoutExam);
    }

    private void setUpPager() {
        PageAdapter tabAdapter = new PageAdapter(getChildFragmentManager());
        tabAdapter.addFragment(new ExamInProgressFragment(), "Ispiti u tijeku");
        tabAdapter.addFragment(new ExamPastFragment(), "Prošli ispiti");
        vpExamController.setAdapter(tabAdapter);
        tabLayoutExam.setupWithViewPager(vpExamController);
    }

}

package com.ferit.tkalcec.digitalschedule.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ferit.tkalcec.digitalschedule.R;
import com.ferit.tkalcec.digitalschedule.Adapters.PageAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private ViewPager vpScheduleController;
    private TabLayout tabLayoutSchedule;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        setUpUI(view);
        setUpPager();
        return view;
    }


    private void setUpUI(View view) {
        this.vpScheduleController = (ViewPager) view.findViewById(R.id.vpScheduleController);
        this.tabLayoutSchedule = (TabLayout) view.findViewById(R.id.tabLayoutSchedule);
    }

    private void setUpPager() {
        PageAdapter tabAdapter = new PageAdapter(getChildFragmentManager());
        tabAdapter.addFragment(new DaysFragment(), "Dani");
        tabAdapter.addFragment(new WeeksFragment(), "Tjedni");
        tabAdapter.addFragment(new MonthsFragment(), "Mjeseci");
        vpScheduleController.setAdapter(tabAdapter);
        tabLayoutSchedule.setupWithViewPager(vpScheduleController);

    }


}

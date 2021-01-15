package com.ferit.tkalcec.digitalschedule.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ferit.tkalcec.digitalschedule.Fragments.HomeFragment;
import com.ferit.tkalcec.digitalschedule.Fragments.ScheduleFragment;
import com.ferit.tkalcec.digitalschedule.Fragments.ExamsFragment;
import com.ferit.tkalcec.digitalschedule.PreferenceManagement;
import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private FrameLayout flMainContainer;
    private BottomNavigationView bottomNavigationMenu;

    private HomeFragment homeFragment;
    private ScheduleFragment scheduleFragment;
    private ExamsFragment examsFragment;

    private PreferenceManagement mPrefManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefManagement = new PreferenceManagement();
        mAuth = FirebaseAuth.getInstance();

        setUpUI();
        setUpFragments();

    }

    private void setUpUI() {
        this.flMainContainer = (FrameLayout) findViewById(R.id.flMainContainer);
        this.bottomNavigationMenu = (BottomNavigationView) findViewById(R.id.bottomNavigationMenu);

        this.bottomNavigationMenu.setOnNavigationItemSelectedListener(this);
    }

    private void setUpFragments() {
        this.homeFragment = new HomeFragment();
        this.scheduleFragment = new ScheduleFragment();
        this.examsFragment = new ExamsFragment();

        replaceFragment(this.homeFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mainMenuInflater = this.getMenuInflater();
        mainMenuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.actionExitScheduleBtn):
                sendToFaculty();
                return true;
            case (R.id.actionLogoutBtn):
                logOut();
                return true;
            default:
                return false;
        }
    }

    private void sendToFaculty() {
        Intent facultyIntent = new Intent(MainActivity.this, FacultyActivity.class);
        startActivity(facultyIntent);
        finish();
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flMainContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.botMenuHome):
                replaceFragment(homeFragment);
                return true;
            case (R.id.botMenuLectures):
                replaceFragment(scheduleFragment);
                return true;
            case (R.id.botMenuTests):
                replaceFragment(examsFragment);
                return true;
            default:
                return false;
        }
    }

    public void exitFromScheduler() {
        mPrefManagement.deleteFacultyData(getApplicationContext());
        sendToFaculty();
    }
}

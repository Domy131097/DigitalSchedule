package com.ferit.tkalcec.digitalschedule.Activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.ferit.tkalcec.digitalschedule.Fragments.ExamsFragment;
import com.ferit.tkalcec.digitalschedule.Fragments.HomeFragment;
import com.ferit.tkalcec.digitalschedule.Fragments.ScheduleFragment;
import com.ferit.tkalcec.digitalschedule.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenSchedulerActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class, true,
            true);

    private MainActivity mainActivity = null;

    @Before
    public void openScheduler_setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void openSchedulerActivityTest() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rvFacultyList),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
    }

    @Test
    public void mainActivity_homeFragmentLaunch() {
        FrameLayout flMainController = (FrameLayout) mainActivity.findViewById(R.id.flMainContainer);

        assertNotNull(flMainController);

        HomeFragment homeFragment = new HomeFragment();

        mainActivity.getSupportFragmentManager().beginTransaction().add(flMainController.getId(), homeFragment).commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        View view = homeFragment.getView().findViewById(R.id.tvFacultyName);

        assertNotNull(view);
    }

    @Test
    public void mainActivity_scheduleFragmentLaunch() {
        FrameLayout flMainController = (FrameLayout) mainActivity.findViewById(R.id.flMainContainer);

        assertNotNull(flMainController);

        ScheduleFragment scheduleFragment = new ScheduleFragment();

        mainActivity.getSupportFragmentManager().beginTransaction().add(flMainController.getId(), scheduleFragment).commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        View view = scheduleFragment.getView().findViewById(R.id.vpScheduleController);

        assertNotNull(view);
    }

    @Test
    public void mainActivity_examsFragmentLaunch() {
        FrameLayout flMainController = (FrameLayout) mainActivity.findViewById(R.id.flMainContainer);

        assertNotNull(flMainController);

        ExamsFragment examsFragment = new ExamsFragment();

        mainActivity.getSupportFragmentManager().beginTransaction().add(flMainController.getId(), examsFragment).commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        View view = examsFragment.getView().findViewById(R.id.vpExamController);

        assertNotNull(view);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @After
    public void openScheduler_tearDown() throws Exception {
        mainActivity = null;
    }
}

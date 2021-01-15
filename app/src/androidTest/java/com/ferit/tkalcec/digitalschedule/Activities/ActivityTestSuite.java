package com.ferit.tkalcec.digitalschedule.Activities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterActivityTest.class,
        LoginActivityTest.class,
        AddFacultyActivityTest.class,
        OpenSchedulerActivityTest.class,
        AddCourseActivityTest.class,
        AddLectureActivityTest.class

})
public class ActivityTestSuite {
}

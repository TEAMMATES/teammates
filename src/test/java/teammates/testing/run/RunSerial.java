package teammates.testing.run;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.old.TestCoordCourse;
import teammates.testing.old.TestCoordDeleteStudents;
import teammates.testing.old.TestStudentSubmitFeedbacks;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	TestCoordCourse.class,
	TestCoordDeleteStudents.class,
	TestStudentSubmitFeedbacks.class
})
public class RunSerial {

}

package teammates.testing.run;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.TestCoordCourse;
import teammates.testing.TestCoordDeleteStudents;
import teammates.testing.TestStudentSubmitFeedbacks;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	TestCoordCourse.class,
	TestCoordDeleteStudents.class,
	TestStudentSubmitFeedbacks.class
})
public class RunSerial {

}

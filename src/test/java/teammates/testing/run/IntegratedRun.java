package teammates.testing.run;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.profiler.Profiler;
import teammates.testing.TestCoordCourse;
import teammates.testing.TestCoordDeleteStudents;
import teammates.testing.TestCoordEditResults;
import teammates.testing.TestCoordEnrollStudents;
import teammates.testing.TestCoordEvaluation;
import teammates.testing.TestCoordLogin;
import teammates.testing.TestCoordPublishResults;
import teammates.testing.TestCoordRemindEvaluation;
import teammates.testing.TestCoordRemindIndividualJoinCourse;
import teammates.testing.TestCoordSendKeysJoinCourse;
import teammates.testing.TestCoordSubmitFeedbacks;
import teammates.testing.TestCoordViewResults;
import teammates.testing.TestFooter;
import teammates.testing.TestStudentEditFeedbacks;
import teammates.testing.TestStudentLogin;
import teammates.testing.TestStudentMoveDropTeam;
import teammates.testing.TestStudentReceiveResults;
import teammates.testing.TestStudentSubmitFeedbacks;
import teammates.testing.TestSystemCaseSensitivity;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	/* 
	 * Commented out by Wang Sha, covered by rest of the test cases 
	 */
	TestCoordLogin.class,
	TestStudentLogin.class,
	TestCoordEvaluation.class,
	TestCoordSendKeysJoinCourse.class,
	TestCoordCourse.class, 
	TestCoordDeleteStudents.class,
	TestCoordEditResults.class,
	TestCoordEnrollStudents.class,
	TestCoordRemindEvaluation.class,
	TestCoordRemindIndividualJoinCourse.class,	
	TestCoordSubmitFeedbacks.class,
	TestCoordViewResults.class,
	TestCoordPublishResults.class,	

	TestStudentSubmitFeedbacks.class,
	TestSystemCaseSensitivity.class,
	TestStudentMoveDropTeam.class,
	TestStudentReceiveResults.class,

	TestFooter.class,

	TestStudentEditFeedbacks.class
})
public class IntegratedRun {


}

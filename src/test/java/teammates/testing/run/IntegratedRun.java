package teammates.testing.run;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.old.TestCoordCourse;
import teammates.testing.old.TestCoordDeleteStudents;
import teammates.testing.old.TestCoordEditResults;
import teammates.testing.old.TestCoordEnrollStudents;
import teammates.testing.old.TestCoordEvaluation;
import teammates.testing.old.TestCoordPublishResults;
import teammates.testing.old.TestCoordRemindEvaluation;
import teammates.testing.old.TestCoordRemindIndividualJoinCourse;
import teammates.testing.old.TestCoordSendKeysJoinCourse;
import teammates.testing.old.TestCoordSubmitFeedbacks;
import teammates.testing.old.TestCoordTeamForming;
import teammates.testing.old.TestCoordViewResults;
import teammates.testing.old.TestFooter;
import teammates.testing.old.TestStudentEditFeedbacks;
import teammates.testing.old.TestStudentMoveDropTeam;
import teammates.testing.old.TestStudentReceiveResults;
import teammates.testing.old.TestStudentSubmitFeedbacks;
import teammates.testing.old.TestSystemCaseSensitivity;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	
	/* Commentted out by Wang Sha, covered by rest of the test cases
	 * 
	TestCoordLogin.class,
	TestStudentLogin.class,
	*/
	TestStudentSubmitFeedbacks.class,
	TestSystemCaseSensitivity.class,
	TestStudentMoveDropTeam.class,
	TestCoordPublishResults.class,	
	TestStudentReceiveResults.class,
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
	TestCoordTeamForming.class,
	TestFooter.class,	
	TestStudentEditFeedbacks.class
	
//	TestCoordCourse.class,
//	TestCoordEvaluation.class, 
//	TestCoordDeleteStudents.class,
//	TestCoordEnrollStudents.class,
//	TestCoordSubmitFeedbacks.class,
//	TestStudentLogin.class,
//	TestCoordLogin.class
})
public class IntegratedRun {


}

package teammates.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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

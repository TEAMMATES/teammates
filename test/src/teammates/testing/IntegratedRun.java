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
	TestCoordEvaluation.class,
	TestCoordCourse.class, 
	TestCoordDeleteStudents.class,
	TestCoordEditResults.class,
	TestCoordEnrollStudents.class,
	TestCoordPublishResults.class,
	TestCoordRemindEvaluation.class,
	TestCoordRemindIndividualJoinCourse.class,
	TestCoordSendKeysJoinCourse.class,
	TestCoordSubmitFeedbacks.class,
	TestCoordViewResults.class,
	
	
	TestFooter.class,
	
	TestStudentEditFeedbacks.class,
	TestStudentMoveDropTeam.class,
	TestStudentReceiveResults.class,
	TestStudentSubmitFeedbacks.class,
	
	TestSystemCaseSensitivity.class


})
public class IntegratedRun {


}

package teammates.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	TestCoordLogin.class,
	TestStudentLogin.class,
	
	TestCoordCourse.class, 
	TestCoordEnrollStudents.class,
	TestCoordEvaluation.class,
	TestSendKeysJoinCourse.class,
	
	TestStudentSubmitFeedbacks.class, 
	TestStudentReceiveResults.class,
	TestCoordPublishResults.class, 
	TestCoordRemindIndividualJoinCourse.class,
	
	TestCoordRemindEvaluation.class,
	TestCoordEditStudents.class,
	TestStudentMoveDropTeam.class,
})
public class IntegratedRun {


}

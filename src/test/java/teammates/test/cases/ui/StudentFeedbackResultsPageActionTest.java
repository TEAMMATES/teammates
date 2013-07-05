package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.EvaluationsDb;

public class StudentFeedbackResultsPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		fs.resultsVisibleFromTime = fs.startTime;
		FeedbackSessionsLogic.inst().updateFeedbackSession(fs);
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
				};
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		String studentId = student1InCourse1.googleId;
	
		verifyUnaccessibleWithoutLogin(submissionParams);
		
		//if the user is not a student of the course, we redirect to home page.
		gaeSimulation.loginUser("unreg.user");
		verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		
		//if the user is not a student of the course, we redirect to home page.
		gaeSimulation.loginAsInstructor(instructorId);
		verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
		
		//TODO: test no questions -> redirect after moving detection logic to proper access control level. 
	}

	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		//TODO: ensure results are not viewable when not PUBLISHED
	}

	
}

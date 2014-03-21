package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class InstructorFeedbackPreviewAsInstructorActionTest extends
		BaseActionTest {
	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, session.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
				Const.ParamsNames.PREVIEWAS, instructor.email
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		//TODO: implement this
	}
}

package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Config;

public class InstructorFeedbackResultsPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Config.PAGE_INSTRUCTOR_FEEDBACK_RESULTS;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] submissionParams = new String[]{
				Config.PARAM_COURSE_ID, fs.courseId,
				Config.PARAM_FEEDBACK_SESSION_NAME, fs.feedbackSessionName
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
}

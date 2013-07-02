package teammates.test.cases.ui;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Config;
import teammates.logic.FeedbackQuestionsLogic;
import teammates.ui.controller.ControllerServlet;

public class InstructorFeedbackQuestionEditActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = Config.PAGE_INSTRUCTOR_FEEDBACK_QUESTION_EDIT;
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		FeedbackQuestionAttributes fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		
		String[] submissionParams = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		
		submissionParams = addQuestionIdToParams(fq.getId(), submissionParams);
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	
	private String[] addQuestionIdToParams(String questionId, String[] params) {
		List<String> list = new ArrayList<String>();
		list.add(Config.PARAM_FEEDBACK_QUESTION_ID);
		list.add(questionId);
		for (String s : params) {
			list.add(s);
		}
		return list.toArray(new String[list.size()]);
	}
}

package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.controller.InstructorFeedbackQuestionAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackQuestionAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		FeedbackSessionAttributes fs = 
				dataBundle.feedbackSessions.get("session1InCourse1");
		
		String[] submissionParams = 
				createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		InstructorAttributes instructor1ofCourse1 =
				dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

		______TS("Not enough parameters");

		verifyAssumptionFailure();
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		String[] params = {	Const.ParamsNames.COURSE_ID, fs.courseId,
							Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName};
		verifyAssumptionFailure(params);

		______TS("Typical case");

		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		params[13] = "max"; //change number of feedback to give to unlimited
		InstructorFeedbackQuestionAddAction action = getAction(params);
		RedirectResult result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());

		String expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Custom number of students to give feedback to");

		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());

		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Custom number of teams to give feedback to");

		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		params[7] = "TEAMS"; //change recipientType to TEAMS
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());

		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Remnant custom number of entities when recipient is changed to non-student and non-team");
		
		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		params[7] = "INSTRUCTORS"; //change recipientType to INSTRUCTORS
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());

		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Failure: Empty or null participant lists");
		
		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		params[19] = ""; //change showGiverTo to empty
		
		// remove showRecipientTo
		params[20] = Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE;
		params[21] = "edit";
		params = Arrays.copyOf(params, 22);		
		
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());

		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		
		______TS("Failure: Invalid Parameter");

		params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
		params[5] = "NONE"; // Change giverType to NONE
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=NONE+is+not+a+valid+feedback+giver.."
						+ "&error=true",
				result.getDestinationWithParams());

		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, "NONE", FieldValidator.GIVER_TYPE_NAME) 
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
				
		______TS("Masquerade mode");
		
		gaeSimulation.loginAsAdmin("admin.user");

		params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
		params = addUserIdToParams(instructor1ofCourse1.googleId, params);
		
		action = getAction(params);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=First+feedback+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+question+has+been+added+to+this+feedback+session."
						+ "&error=false",
				result.getDestinationWithParams());
		
		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
						+ "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
						+ "Created Feedback Question for Feedback Session:<span class=\"bold\">"
						+ "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
						+ " created.<br><span class=\"bold\">Text:</span> <Text: question>|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
	}
	
	private InstructorFeedbackQuestionAddAction getAction (String... params) throws Exception {
		return (InstructorFeedbackQuestionAddAction) gaeSimulation.getActionObject(uri, params);
	}
}

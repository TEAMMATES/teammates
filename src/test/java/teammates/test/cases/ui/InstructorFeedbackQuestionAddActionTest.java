package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
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
	public void testExecuteAndPostProcessMsq() throws Exception{
		InstructorAttributes instructor1ofCourse1 =
				dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
				
		______TS("Typical case");

		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		String[] params = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
				//Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "", // empty option
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "  		", // empty option with extra whitespace
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-4", "The Atmosphere",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
		};
		
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
						+ " created.<br><span class=\"bold\">Multiple-select question:</span> What do you like best about the class?"
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Generated options");

		params = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like in the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
		};
		
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
						+ " created.<br><span class=\"bold\">Multiple-select question:</span> Who do you like in the class?"
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
	}

	@Test
	public void testExecuteAndPostProcessMcq() throws Exception{
		InstructorAttributes instructor1ofCourse1 =
				dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
				
		______TS("Typical case");

		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		String[] params = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
				//Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "", // empty option
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "  		", // empty option with extra whitespace
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-4", "The Atmosphere",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
		};
		
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
						+ " created.<br><span class=\"bold\">Multiple-choice question:</span> What do you like best about the class?"
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Generated options");

		params = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like best in the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2", // this field defaults to 2
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
		};
		
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
						+ " created.<br><span class=\"bold\">Multiple-choice question:</span> Who do you like best in the class?"
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
	}

	@Test
	public void testExecuteAndPostProcessNumScale() throws Exception{
		InstructorAttributes instructor1ofCourse1 =
				dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
				
		______TS("Typical case");

		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		String[] params = new String[]{
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NUMSCALE",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Rate the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX, "5",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP, "0.5",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
		};
		
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
						+ " created.<br><span class=\"bold\">Numerical-scale question:</span> Rate the class?"
						+ "|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
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
		params[15] = "max"; //change number of feedback to give to unlimited
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
		
		______TS("Failure: Empty or null participant lists");
		
		params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
		params[21] = ""; //change showGiverTo to empty
		
		// remove showRecipientTo
		params[22] = Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE;
		params[23] = "edit";
		params = Arrays.copyOf(params, 24);		
		
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
		
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
						+ " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
		assertEquals(expectedLogMessage, action.getLogMessage());
	}
	
	private InstructorFeedbackQuestionAddAction getAction (String... params) throws Exception {
		return (InstructorFeedbackQuestionAddAction) gaeSimulation.getActionObject(uri, params);
	}
}

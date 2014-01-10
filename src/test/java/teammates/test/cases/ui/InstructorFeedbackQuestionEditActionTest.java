package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorFeedbackQuestionEditAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackQuestionEditActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT;
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
		gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
		FeedbackQuestionAttributes fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		
		______TS("Typical Case");
		
		String[] typicalParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		InstructorFeedbackQuestionEditAction a = getAction(typicalParams);
		RedirectResult r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=idOfTypicalCourse1"
				+ "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("Custom number of recipient");
		
		String[] customParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(customParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=idOfTypicalCourse1"
				+ "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("Anonymous Team Session");
		
		String[] teamParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.TEAMS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, "",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(teamParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=idOfTypicalCourse1"
				+ "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("Self Feedback");
		
		String[] selfParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.SELF.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, "",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(selfParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=idOfTypicalCourse1"
				+ "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		______TS("Delete Feedback");
		
		String[] deleteParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, "",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "delete",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(deleteParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=idOfTypicalCourse1"
				+ "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1"
				+ "&message=The+question+has+been+deleted.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, r.getStatusMessage());
		assertFalse(r.isError);
	}
	
	@Test
	public void testExecuteAndPostProcessMcq() throws Exception{
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		
		InstructorAttributes instructor1ofCourse1 =	dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("mcqSession");
		FeedbackQuestionAttributes fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		FeedbackMcqQuestionDetails mcqDetails = (FeedbackMcqQuestionDetails) fq.getQuestionDetails();
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();
		
		______TS("Edit text");
		
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); // There is already responses for this question
		
		String[] editTextParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, Integer.toString(mcqDetails.numOfMcqChoices),
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", mcqDetails.mcqChoices.get(0),
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", mcqDetails.mcqChoices.get(1),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
		RedirectResult r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
				+ "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		// All existing response should remain
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
		
		______TS("Edit options");
		
		// There should already be responses for this question
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
		
		String[] editOptionParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
				//Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "", // empty option
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "  		", // empty option with extra whitespace
				Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-4", "The Atmosphere",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(editOptionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
				+ "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		// All existing response should be deleted as option is edited
		assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
	}
	
	@Test
	public void testExecuteAndPostProcessMsq() throws Exception{
		dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
		
		InstructorAttributes instructor1ofCourse1 =	dataBundle.instructors.get("instructor1OfCourse1");

		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
		
		FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("msqSession");
		FeedbackQuestionAttributes fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
		FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) fq.getQuestionDetails();
		FeedbackResponsesDb frDb = new FeedbackResponsesDb();
		
		______TS("Edit text");
		
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); // There is already responses for this question
		
		String[] editTextParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, Integer.toString(msqDetails.numOfMsqChoices),
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", msqDetails.msqChoices.get(0),
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", msqDetails.msqChoices.get(1),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
		RedirectResult r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
				+ "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		// All existing response should remain
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
		
		______TS("Edit options");
		
		// There should already be responses for this question
		assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
		
		String[] editOptionParams = {
				Const.ParamsNames.COURSE_ID, fs.courseId,
				Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
				Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
				Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
				Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
				//Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "", // empty option
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "  		", // empty option with extra whitespace
				Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-4", "The Atmosphere",
				Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
				Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
				Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
		};
		
		a = getAction(editOptionParams);
		r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
				+ "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1"
				+ "&message=The+changes+to+the+question+has+been+updated.&error=false", 
				r.getDestinationWithParams());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
		assertFalse(r.isError);
		
		// All existing response should be deleted as option is edited
		assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty()); 
	}
	
	private InstructorFeedbackQuestionEditAction getAction(String... submissionParams){
		return (InstructorFeedbackQuestionEditAction) gaeSimulation.getActionObject(uri, submissionParams);
	}
	
	private String[] addQuestionIdToParams(String questionId, String[] params) {
		List<String> list = new ArrayList<String>();
		list.add(Const.ParamsNames.FEEDBACK_QUESTION_ID);
		list.add(questionId);
		for (String s : params) {
			list.add(s);
		}
		return list.toArray(new String[list.size()]);
	}
}

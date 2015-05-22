package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorFeedbackQuestionEditAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackQuestionEditActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

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
                     + "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1&error=false",
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
                     + "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1&error=false",
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
                     + "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1&error=false",
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
                     + "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("Invalid edit type");

        String[] invalidEditTypeParams = {
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
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "INVALID", //change to invalid edit type.
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        verifyAssumptionFailure(invalidEditTypeParams);

        ______TS("Invalid questionNumber");

        String[] invalidQnNumParams = {
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

        // change questionNumber to an invalid number
        modifyParamValue(invalidQnNumParams, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "-1");
        verifyAssumptionFailure(invalidQnNumParams);

        // change questionNumber to invalid "number"
        modifyParamValue(invalidQnNumParams, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "ABC");

        try {
            a = getAction(invalidQnNumParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (NumberFormatException e) {
            ignoreExpectedException();
        }

        ______TS("Invalid parameters");

        String[] invalidParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.TEAMS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.OWN_TEAM_MEMBERS.toString(),
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

        a = getAction(invalidParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                   FeedbackParticipantType.OWN_TEAM_MEMBERS.toDisplayRecipientName(),
                                   FeedbackParticipantType.TEAMS.toDisplayGiverName()),
                     r.getStatusMessage());

        ______TS("Delete Feedback");

        String[] deleteParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "",
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
                     + "&fsname=First+feedback+session&user=idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("Unsuccessful case: test null course id parameter");

        String[] submissionParams = new String[]{
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

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                         Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Unsuccessful case: test null course id parameter");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
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

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                         Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }
    }

    @Test
    public void testExecuteAndPostProcessMcq() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("mcqSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackMcqQuestionDetails mcqDetails = (FeedbackMcqQuestionDetails) fq.getQuestionDetails();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();

        ______TS("Edit text");

        // There is already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

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
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };

        InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
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
                // This option is deleted during creation, don't pass parameter
                // Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };

        a = getAction(editOptionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing response should be deleted as option is edited
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit to generated");

        String[] editToGeneratedOptionParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "4",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };

        a = getAction(editToGeneratedOptionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("Delete Feedback");

        String[] deleteParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "4",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "delete",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };

        a = getAction(deleteParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MCQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, r.getStatusMessage());
        assertFalse(r.isError);
    }

    @Test
    public void testExecuteAndPostProcessMsq() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("msqSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) fq.getQuestionDetails();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();

        ______TS("Edit text");

        // There is already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

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
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };

        InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
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
                // This option is deleted during creation, don't pass parameter
                //Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };

        a = getAction(editOptionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing response should be deleted as option is edited
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit to generated options");

        String[] editToGeneratedOptionParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "4",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };

        a = getAction(editToGeneratedOptionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                    + "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                    r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("Delete Feedback");

        String[] deleteParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "4",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "delete",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };

        a = getAction(deleteParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=MSQ+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, r.getStatusMessage());
        assertFalse(r.isError);
    }

    @Test
    public void testExecuteAndPostProcessNumScale() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("numscaleSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackNumericalScaleQuestionDetails numscaleDetails =
                (FeedbackNumericalScaleQuestionDetails) fq.getQuestionDetails();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();

        ______TS("Edit text");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editTextParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NUMSCALE",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, numscaleDetails.questionText + " (edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN, Integer.toString(numscaleDetails.minScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX, Integer.toString(numscaleDetails.maxScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP,
                StringHelper.toDecimalFormatString(numscaleDetails.step),
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
                     + "&fsname=NUMSCALE+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing response should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit scales");

        String[] editScalesParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NUMSCALE",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, numscaleDetails.questionText + " (edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN, Integer.toString(1),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX, Integer.toString(10),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP, StringHelper.toDecimalFormatString(1.0),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editScalesParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=NUMSCALE+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing response should be deleted as the scales are edited
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());
    }

    @Test
    public void testExecuteAndPostProcessConstSumOption() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("constSumSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();

        ______TS("Edit text");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editTextParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among the options.(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "100",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "false",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-0", "Grades",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-1", "Fun",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, "false",
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
                     + "&fsname=CONSTSUM+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing responses should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit points");

        String[] editPointsParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among the options.(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "1000",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "false",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-0", "Grades",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-1", "Fun",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, "false",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editPointsParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=CONSTSUM+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing responses should be deleted as the options are edited
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());
    }

    @Test
    public void testExecuteAndPostProcessConstSumRecipient() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("constSumSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 2);
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackConstantSumQuestionDetails fqd = (FeedbackConstantSumQuestionDetails) fq.getQuestionDetails();

        ______TS("Edit text");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editTextParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, Integer.toString(fqd.points),
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, String.valueOf(fqd.pointsPerOption),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, Integer.toString(fqd.numOfConstSumOptions),
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, String.valueOf(fqd.distributeToRecipients),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=CONSTSUM+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit points per option");

        String[] editPointsParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, Integer.toString(fqd.points),
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, String.valueOf(fqd.pointsPerOption),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, Integer.toString(fqd.numOfConstSumOptions),
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, String.valueOf(fqd.distributeToRecipients),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        // Reverse it from true to false
        modifyParamValue(editPointsParams, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "false");

        a = getAction(editPointsParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=CONSTSUM+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertFalse(r.isError);

        // All existing responses should be deleted as the options are edited
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());
    }

    @Test
    public void testExecuteAndPostProcessContributionQuestion() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("contribSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackContributionQuestionDetails fqd = (FeedbackContributionQuestionDetails) fq.getQuestionDetails();

        ______TS("Edit text");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editTextParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONTRIB",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED, "on"
        };

        InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=CONTRIB+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit: Invalid recipient type");

        String[] editRecipientTypeParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONTRIB",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText,
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editRecipientTypeParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(FeedbackContributionQuestionDetails.ERROR_CONTRIB_QN_INVALID_FEEDBACK_PATH
                     + "<br />" + Const.StatusMessages.FEEDBACK_QUESTION_EDITED,
                     r.getStatusMessage());

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=CONTRIB+Session&user=FSQTT.idOfInstructor1OfCourse1&error=true",
                     r.getDestinationWithParams());
        assertTrue(r.isError);

        // delete session to clean database
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
    }

    @Test
    public void testExecuteAndPostProcessRubricQuestion() throws Exception {
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("rubricSession");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) fq.getQuestionDetails();

        ______TS("Edit text");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editTextParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "RUBRIC",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", "This student has done a good job.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", "This student has tried his/her best.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", "Yes",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", "No",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-1", "",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-0", "Most of the time",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-1", "Less than half the time",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        InstructorFeedbackQuestionEditAction a = getAction(editTextParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=RUBRIC+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit descriptions");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editDescriptionParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "RUBRIC",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", "This student has done a good job.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", "This student has tried his/her best.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", "Yes",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", "No",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-0", "New description",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-1", "",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-0", "Most of the time(Edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-1", "Less than half the time",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editDescriptionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=RUBRIC+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit sub-questions");

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editSubQnParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "RUBRIC",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", "This student has done a good job.(Edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", "This student has tried his/her best.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", "Yes",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", "No",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-0", "New description",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-1", "",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-0", "Most of the time(Edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-1", "Less than half the time",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editSubQnParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=RUBRIC+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should be deleted
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        ______TS("Edit choices");

        // Restore responses
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        fs = dataBundle.feedbackSessions.get("rubricSession");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        fqd = (FeedbackRubricQuestionDetails) fq.getQuestionDetails();

        // There are already responses for this question
        assertFalse(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        String[] editChoicesParams = {
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, fq.giverType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, fq.recipientType.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, Integer.toString(fq.questionNumber),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "RUBRIC",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, fqd.questionText + "(edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", "This student has done a good job.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", "This student has tried his/her best.",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", "Yes(Edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", "No",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-0", "New description",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-0-1", "",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-0", "Most of the time(Edited)",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-1-1", "Less than half the time",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(editChoicesParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, r.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=FSQTT.idOfTypicalCourse1"
                     + "&fsname=RUBRIC+Session&user=FSQTT.idOfInstructor1OfCourse1&error=false",
                     r.getDestinationWithParams());
        assertFalse(r.isError);

        // All existing responses should be deleted
        assertTrue(frDb.getFeedbackResponsesForQuestion(fq.getId()).isEmpty());

        // delete session to clean database
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
    }

    private InstructorFeedbackQuestionEditAction getAction(String... submissionParams) throws Exception {
        return (InstructorFeedbackQuestionEditAction) gaeSimulation.getActionObject(uri, submissionParams);
    }
}

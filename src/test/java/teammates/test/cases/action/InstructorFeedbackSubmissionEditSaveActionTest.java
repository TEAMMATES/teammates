package teammates.test.cases.action;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackSubmissionEditSaveAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackSubmissionEditSaveAction}.
 */
public class InstructorFeedbackSubmissionEditSaveActionTest extends BaseActionTest {
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    private final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/InstructorFeedbackSubmissionEditSaveActionTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        prepareTestData();
        InstructorAttributes instructor1InCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        ______TS("Unsuccessful case: test empty feedback session name parameter");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, dataBundle.feedbackResponses.get("response1ForQ1S1C1").courseId
        };

        InstructorFeedbackSubmissionEditSaveAction a;
        RedirectResult r;

        try {
            a = getAction(submissionParams);
            r = getRedirectResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }

        ______TS("Unsuccessful case: test empty course id parameter");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                dataBundle.feedbackResponses.get("response1ForQ1S1C1").feedbackSessionName
        };

        try {
            a = getAction(submissionParams);
            r = getRedirectResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Successful case: edit existing answer");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First Session", "idOfCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        instructor1InCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // submission confirmation email not sent if parameter does not exist
        verifyNoEmailsSent(a);

        ______TS("Successful case: deleted response");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "on"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // submission confirmation email sent
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper email = getEmailsSent(a).get(0);
        String courseName = coursesLogic.getCourse(fr.courseId).getName();
        assertEquals(String.format(EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject(), courseName,
                                   fr.feedbackSessionName),
                     email.getSubject());
        assertEquals(instructor1InCourse1.email, email.getRecipient());

        // delete respondent task scheduled
        verifySpecifiedTasksAdded(a, Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_QUEUE_NAME, 1);

        ______TS("Successful case: skipped question");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "off"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // submission confirmation email not sent if parameter is not "on"
        verifyNoEmailsSent(a);

        ______TS("Successful case: new response");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // append respondent task scheduled
        verifySpecifiedTasksAdded(a, Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_QUEUE_NAME, 1);

        ______TS("Successful case: edit response, did not specify recipient");

        fq = fqDb.getFeedbackQuestion("First Session", "idOfCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "student1InCourse1@gmail.tmt",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Edited" + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: new response, did not specify recipient");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "student1InCourse1@gmail.tmt",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Unsuccessful case: modified recipient to invalid recipient");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", "invalid_recipient_email",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "on"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertTrue(r.isError);
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, "invalid_recipient_email"));

        // submission confirmation email not sent if the action is an error, even with submission parameter "on"
        verifyNoEmailsSent(a);

        ______TS("Successful case: mcq: typical case");

        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(dataBundle);

        fq = fqDb.getFeedbackQuestion("MCQ Session", "FSQTT.idOfTypicalCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: mcq: question skipped");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: msq: typical case");

        fq = fqDb.getFeedbackQuestion("MSQ Session", "FSQTT.idOfTypicalCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ2S2C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful csae: msq: question skipped");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: numerical scale: typical case");

        fq = fqDb.getFeedbackQuestion("NUMSCALE Session", "FSQTT.idOfTypicalCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);
        FeedbackNumericalScaleQuestionDetails fqd =
                (FeedbackNumericalScaleQuestionDetails) fq.getQuestionDetails();

        fr = dataBundle.feedbackResponses.get("response1ForQ2S3C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "3.5",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.getMinScale()),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.getMaxScale()),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.getStep())
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: numerical scale: question skipped");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.getMinScale()),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.getMaxScale()),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.getStep())
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: const sum: typical case");

        fq = fqDb.getFeedbackQuestion("CONSTSUM Session", "FSQTT.idOfTypicalCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ2S4C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        FeedbackResponseAttributes fr2 = dataBundle.feedbackResponses.get("response2ForQ2S4C1");
        // necessary to get the correct responseId
        fr2 = frDb.getFeedbackResponse(fq.getId(), fr2.giver, fr2.recipient);
        assertNotNull("Feedback response not found in database", fr2);

        instructor1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "2",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "150",

                //Const sum question needs response to each recipient to sum up properly.
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-1", fr2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr2.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr2.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr2.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-1", fr2.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr2.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-1", "50",
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertFalse(r.isError);
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr2.giver, fr2.recipient));

        ______TS("Successful case: const sum: question skipped");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, r.isError, "FSQTT.idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Successful case: contrib qn: typical case");

        // No tests since contrib qn can only be answered by students to own team members including self.
    }

    @Test
    public void testGracePeriodExecuteAndPostProcess() throws Exception {
        dataBundle = loadDataBundle("/InstructorFeedbackSubmissionEditSaveActionTest.json");
        FeedbackSessionsDb feedbackSessionDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Grace Period Session");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName()
        };

        ______TS("opened");

        fs.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        feedbackSessionDb.updateFeedbackSession(fs);

        assertTrue(fs.isOpened());
        assertFalse(fs.isInGracePeriod());

        InstructorFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("during grace period");

        fs.setEndTime(Instant.now());
        feedbackSessionDb.updateFeedbackSession(fs);

        assertFalse(fs.isOpened());
        assertTrue(fs.isInGracePeriod());

        a = getAction(submissionParams);
        r = getRedirectResult(a);
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, "instructor1InCourse1"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("after grace period");

        fs.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-10));
        feedbackSessionDb.updateFeedbackSession(fs);

        assertFalse(fs.isOpened());
        assertFalse(fs.isInGracePeriod());

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, r.getStatusMessage());
    }

    @Override
    protected InstructorFeedbackSubmissionEditSaveAction getAction(String... params) {
        return (InstructorFeedbackSubmissionEditSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName()
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        testGracePeriodAccessControlForInstructors();
    }

    private void testGracePeriodAccessControlForInstructors() throws Exception {

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("gracePeriodSession");

        closeSession(fs);

        assertFalse(fs.isOpened());
        assertTrue(fs.isInGracePeriod());
        assertFalse(fs.isClosed());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    private void closeSession(FeedbackSessionAttributes fs) throws Exception {
        fs.setEndTime(Instant.now());
        fsDb.updateFeedbackSession(fs);
    }
}

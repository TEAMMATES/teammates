package teammates.test.cases.action;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorEditStudentFeedbackSaveAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorEditStudentFeedbackSaveAction}.
 */
public class InstructorEditStudentFeedbackSaveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        testModifyResponses();

        testIncorrectParameters();

        testSubmitResponseForInvalidQuestion();
        testClosedSession();
    }

    private void testModifyResponses() {
        ______TS("edit existing answer");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // submission confirmation email not sent if parameter does not exist
        verifyNoEmailsSent(a);

        ______TS("deleted response");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail,
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "on"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED + Const.HTML_BR_TAG
                + Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "1.", r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // submission confirmation email still not sent even if parameter is "on" because this is moderation
        verifyNoEmailsSent(a);

        // delete respondent task scheduled
        verifySpecifiedTasksAdded(a, Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_QUEUE_NAME, 1);

        ______TS("skipped question");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED + Const.HTML_BR_TAG
                + Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "1.", r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("new response");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        // append respondent task scheduled
        verifySpecifiedTasksAdded(a, Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_QUEUE_NAME, 1);
    }

    private void testIncorrectParameters() {
        ______TS("Unsuccessful case: test empty feedback session name parameter");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, dataBundle.feedbackResponses.get("response1ForQ1").courseId,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }

        ______TS("Unsuccessful case: test empty course id parameter");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                        dataBundle.feedbackResponses.get("response1ForQ1").feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Unsuccessful case: test no moderated student parameter");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON), e.getMessage());
        }

    }

    private void testDifferentPrivileges() {
        ______TS("Unsuccessful case: insufficient privileges");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";

        InstructorAttributes instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor ["
                                 + instructorHelper.email + "] for privilege "
                                 + "[" + Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                 + "] on section [Section 1]",
                         e.getMessage());
        }

        ______TS("Unsuccessful case: sufficient privileges only for a section, but attempted to modify another section");

        instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor ["
                                 + instructorHelper.email + "] for privilege "
                                 + "[" + Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                 + "] on section [Section 1]",
                         e.getMessage());
        }

        ______TS("Successful case: sufficient privileges only for a section");

        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);

        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student2InIESFPTCourse%40gmail.tmt",
                        "IESFPTCoursehelper1",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("failure case: privileges sufficient for section BUT insufficient for a session");
        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        InstructorAttributes instructorHelper2 = dataBundle.instructors.get("IESFPTCoursehelper2");
        gaeSimulation.loginAsInstructor(instructorHelper2.googleId);

        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor ["
                             + instructorHelper2.email + "] for privilege [canmodifysessioncommentinsection] "
                             + "on section [Section 2]",
                         e.getMessage());
        }

        ______TS("Successful case: sufficient for section, although insufficient for another session");

        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForS2Q1");
        fq = fqDb.getFeedbackQuestion("Another feedback session", "IESFPTCourse", 1);

        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student2InIESFPTCourse%40gmail.tmt",
                        "IESFPTCoursehelper2",
                        "IESFPTCourse",
                        "Another+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Success case: insufficient for section, BUT sufficient for a session");

        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        InstructorAttributes instructorHelper3 = dataBundle.instructors.get("IESFPTCoursehelper3");
        gaeSimulation.loginAsInstructor(instructorHelper3.googleId);

        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);

        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student2InIESFPTCourse%40gmail.tmt",
                        "IESFPTCoursehelper3",
                        "IESFPTCourse",
                        "First+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));

        ______TS("Failure case: insufficient for section, although sufficient for another session");

        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";

        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForS2Q1");
        fq = fqDb.getFeedbackQuestion("Another feedback session", "IESFPTCourse", 1);

        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            getAction(submissionParams).executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Another feedback session] is not accessible to instructor ["
                             + instructorHelper3.email + "] for privilege ["
                             + Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                             + "] on section [Section 2]",
                         e.getMessage());
        }
    }

    private void testSubmitResponseForInvalidQuestion() {
        ______TS("Failure case: submit response for question in session, but should not be editable by instructor");

        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ3");
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 3);

        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackSaveAction a;
        try {
            a = getAction(submissionParams);
            a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] "
                             + "is not accessible to instructor [" + instructor.email + "]",
                         e.getMessage());
        }

        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 4);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ4");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            a = getAction(submissionParams);
            a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] "
                             + "is not accessible to instructor [" + instructor.email + "]",
                         e.getMessage());
        }

        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 5);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ5");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            a = getAction(submissionParams);
            a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] "
                             + "is not accessible to instructor [" + instructor.email + "]",
                         e.getMessage());
        }
    }

    private void testClosedSession() {
        ______TS("Success case: modifying responses in closed session");

        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("Closed feedback session", "IESFPTCourse", 1);

        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1InClosedSession");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "Closed+feedback+session"),
                r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient));
    }

    @Override
    protected InstructorEditStudentFeedbackSaveAction getAction(String... params) {
        return (InstructorEditStudentFeedbackSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(
            String parentUri, boolean isError, String moderatedPerson, String userId, String courseId, String fsname) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(
                pageDestination, Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPerson);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.FEEDBACK_SESSION_NAME, fsname);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        testDifferentPrivileges();

        StudentAttributes student = typicalBundle.students.get("student1InCourse1");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String moderatedStudentEmail = student.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
    }

    @Test
    public void testSaveAndUpdateFeedbackParticipantCommentsOnResponseInOpenSession() {
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();

        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 6);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ6");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";

        ______TS("Save new comment on response");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ADD_TEXT + "-1-0", "New comment",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        RedirectResult result = getRedirectResult(getAction(submissionParams));

        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, result.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                result.getDestinationWithParams());

        FeedbackResponseCommentAttributes frc = getFeedbackParticipantComment(fr.getId());
        assertEquals("New comment", frc.commentText.getValue());
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals("student1InIESFPTCourse@gmail.tmt", frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
        assertTrue(frc.isVisibilityFollowingFeedbackQuestion);

        ______TS("Update response comment");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT + "-1-0", "Edited comment",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID + "-1-0", frc.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        result = getRedirectResult(getAction(submissionParams));

        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, result.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "First+feedback+session"),
                result.getDestinationWithParams());
        frc = getFeedbackParticipantComment(fr.getId());
        assertEquals("Edited comment", frc.commentText.getValue());
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals("student1InIESFPTCourse@gmail.tmt", frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
        assertTrue(frc.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testSaveAndUpdateFeedbackParticipantCommentsOnResponseInClosedSession() {
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("Closed feedback session", "IESFPTCourse", 2);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ2InClosedSession");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertNotNull("Feedback response not found in database", fr);

        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";

        ______TS("Save new comment on response");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ADD_TEXT + "-1-0", "New comment",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        RedirectResult result = getRedirectResult(getAction(submissionParams));

        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, result.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "Closed+feedback+session"),
                result.getDestinationWithParams());

        FeedbackResponseCommentAttributes frc = getFeedbackParticipantComment(fr.getId());
        assertEquals("New comment", frc.commentText.getValue());
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals("student1InIESFPTCourse@gmail.tmt", frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
        assertTrue(frc.isVisibilityFollowingFeedbackQuestion);
        // Verifies that comment is searchable
        ArrayList<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(instructor);
        FeedbackResponseCommentSearchResultBundle bundle = frcDb.search("\"New comment\"", instructors);
        assertEquals(1, bundle.numberOfResults);
        verifySearchResults(bundle, frc);

        ______TS("Update response comment");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipient,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT + "-1-0", "Edited comment",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID + "-1-0", frc.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        result = getRedirectResult(getAction(submissionParams));

        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, result.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE,
                        false,
                        "student1InIESFPTCourse%40gmail.tmt",
                        "IESFPTCourseinstr",
                        "IESFPTCourse",
                        "Closed+feedback+session"),
                result.getDestinationWithParams());
        frc = getFeedbackParticipantComment(fr.getId());
        assertEquals("Edited comment", frc.commentText.getValue());
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals("student1InIESFPTCourse@gmail.tmt", frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
        assertTrue(frc.isVisibilityFollowingFeedbackQuestion);
        // Verifies that comment is searchable
        bundle = frcDb.search("\"Edited comment\"", instructors);
        assertEquals(1, bundle.numberOfResults);
        verifySearchResults(bundle, frc);
    }
}

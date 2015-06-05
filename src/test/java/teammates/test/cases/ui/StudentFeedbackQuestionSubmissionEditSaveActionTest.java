package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentFeedbackQuestionSubmissionEditSaveAction;

public class StudentFeedbackQuestionSubmissionEditSaveActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackSessionsDb feedbackSessionDb = new FeedbackSessionsDb();

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb
                .getFeedbackQuestion(session1InCourse1.feedbackSessionName, session1InCourse1.courseId, 1);

        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb
                .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email, student1InCourse1.email);

        assertNotNull(feedbackResponse);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
        };

        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "0"
        };

        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "0"
        };

        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "0"
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("edit existing answer");

        feedbackResponse = feedbackResponsesDb
                .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email, student1InCourse1.email);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, feedbackResponse.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                feedbackResponse.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0",
                feedbackResponse.getId()
        };

        StudentFeedbackQuestionSubmissionEditSaveAction saveAction = getAction(submissionParams);
        RedirectResult pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, pageResult.getStatusMessage());

        feedbackResponse = feedbackResponsesDb
                .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email,
                                     feedbackResponse.recipientEmail);
        assertEquals("Qn Answer", feedbackResponse.responseMetaData.getValue());

        ______TS("delete answer");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                feedbackResponse.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0",
                feedbackResponse.getId()
        };

        saveAction = getAction(submissionParams);
        pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, pageResult.getStatusMessage());
        assertNull(feedbackResponsesDb
                       .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email,
                                            feedbackResponse.recipientEmail));

        ______TS("skip question");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                feedbackResponse.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        saveAction = getAction(submissionParams);
        pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, pageResult.getStatusMessage());
        assertNull(feedbackResponsesDb
                       .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email,
                                            feedbackResponse.recipientEmail));

        ______TS("new response");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                feedbackResponse.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0",
                "new response"
        };

        saveAction = getAction(submissionParams);
        pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, pageResult.getStatusMessage());

        /*
         * Attention:
         * Be aware of these two:
         * 1. feedbackResponse = feedbackResponsesDb.getFeedbackResponse(...)
         *    assertEquals(feedbackResponse... , ...);
         * 2. assertEquals( feedbackResponsesDb.getFeedbackResponse(...)...  , ... );
         *
         * Try to use second way to prevent turning feedbackResponse into null due to some test cases
         * Or add another variable as a copy of the original feedbackResponse
         */
        feedbackResponse = feedbackResponsesDb
                .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email, feedbackResponse.recipientEmail);
        assertEquals("new response", feedbackResponse.getResponseDetails().getAnswerString());

        ______TS("invalid feedback recipient");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                "invalid response recipient",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Qn answer"
        };

        saveAction = getAction(submissionParams);
        pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertTrue(pageResult.isError);
        assertNull(feedbackResponsesDb
                       .getFeedbackResponse(feedbackQuestion.getId(), student1InCourse1.email,
                                            "invalid response recipient"));

        ______TS("Closed Session");
        
        session1InCourse1.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        feedbackSessionDb.updateFeedbackSession(session1InCourse1);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, feedbackResponse.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
                feedbackResponse.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
                feedbackQuestion.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0",
                feedbackResponse.getId()
        };

        saveAction = getAction(submissionParams);
        pageResult = (RedirectResult) saveAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
        assertTrue(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, pageResult.getStatusMessage());

        // currently unable to test this due to Issue 3570, can add back if it is resolved to test uncovered branch
//        ______TS("Grace Period");
//
//        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
//
//        gaeSimulation.loginAsStudent(student4InCourse1.googleId);
//
//        FeedbackSessionAttributes gracePeriodSession = dataBundle.feedbackSessions.get("gracePeriodSession");
//        
//        gracePeriodSession.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
//        feedbackSessionDb.updateFeedbackSession(gracePeriodSession);
//
//        feedbackQuestion = feedbackQuestionsDb
//                .getFeedbackQuestion(gracePeriodSession.feedbackSessionName, gracePeriodSession.courseId, 2);
//
//        feedbackResponse = feedbackResponsesDb
//                .getFeedbackResponse(feedbackQuestion.getId(), student4InCourse1.email, 
//                                     "Team 1.2");
//
//        submissionParams = new String[]{
//                Const.ParamsNames.COURSE_ID, feedbackResponse.courseId,
//                Const.ParamsNames.FEEDBACK_SESSION_NAME,
//                gracePeriodSession.feedbackSessionName,
//                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1",
//                feedbackQuestion.getId(),
//                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
//                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0",
//                feedbackResponse.recipientEmail,
//                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1",
//                feedbackQuestion.questionType.toString(),
//                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Qn Answer",
//                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0",
//                feedbackResponse.getId()
//        };
//
//        saveAction = getAction(submissionParams);
//        pageResult = (RedirectResult) saveAction.executeAndPostProcess();
//
//        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, pageResult.destination);
//        assertFalse(pageResult.isError);
//        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, pageResult.getStatusMessage());
    }

    private StudentFeedbackQuestionSubmissionEditSaveAction getAction(String... params) throws Exception {
        return (StudentFeedbackQuestionSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}

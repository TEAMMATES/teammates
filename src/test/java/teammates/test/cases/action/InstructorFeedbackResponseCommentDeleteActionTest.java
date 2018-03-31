package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResponseCommentDeleteAction;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * SUT: {@link InstructorFeedbackResponseCommentDeleteAction}.
 */
public class InstructorFeedbackResponseCommentDeleteActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        removeAndRestoreTypicalDataBundle();

        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment = typicalBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("Typical successful case");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        InstructorFeedbackResponseCommentDeleteAction action = getAction(submissionParams);
        AjaxResult result = getAjaxResult(action);

        InstructorFeedbackResponseCommentAjaxPageData data =
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());

        ______TS("Non-existent feedback response comment");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);

        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());

        ______TS("Instructor is not feedback response comment giver");

        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");

        questionNumber = 2;
        feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        giverEmail = "student2InCourse1@gmail.tmt";
        feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail,
                                                                   receiverEmail);
        feedbackResponseComment = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);

        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());
    }

    @Override
    protected InstructorFeedbackResponseCommentDeleteAction getAction(String... params) {
        return (InstructorFeedbackResponseCommentDeleteAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        int questionNumber = 2;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ2S1C1");

        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.giverEmail, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId())
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);

        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
}

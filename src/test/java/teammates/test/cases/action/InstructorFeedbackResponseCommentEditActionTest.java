package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResponseCommentEditAction;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * SUT: {@link InstructorFeedbackResponseCommentEditAction}.
 */
public class InstructorFeedbackResponseCommentEditActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse =
                feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful csae: not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("Typical successful case for unpublished session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };

        InstructorFeedbackResponseCommentEditAction action = getAction(submissionParams);
        AjaxResult result = getAjaxResult(action);
        InstructorFeedbackResponseCommentAjaxPageData data =
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Null show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Empty show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, ""
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Typical successful case for unpublished session public to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "OWN_TEAM_MEMBERS",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER_TEAM_MEMBERS",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "STUDENTS",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Non-existent feedback response comment id");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };

        try {
            action = getAction(submissionParams);
            result = getAjaxResult(action);
        } catch (AssertionError e) {
            assertEquals("FeedbackResponseComment should not be null", e.getMessage());
        }

        ______TS("Instructor is not feedback response comment giver");

        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };
        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Typical successful case for published session");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        FeedbackSessionAttributes fs =
                FeedbackSessionsLogic.inst().getFeedbackSession(feedbackResponseComment.feedbackSessionName,
                                                                feedbackResponseComment.courseId);
        FeedbackSessionsLogic.inst().publishFeedbackSession(fs);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText
                                                                + " (Edited for published session)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS"
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);
        assertEquals("", result.getStatusMessage());
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertTrue(data.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, data.errorMessage);
    }

    @Override
    protected InstructorFeedbackResponseCommentEditAction getAction(String... params) {
        return (InstructorFeedbackResponseCommentEditAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = fqDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = frDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment = typicalBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = frcDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "comment",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        // this person is not the giver. so not accessible
        verifyUnaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}

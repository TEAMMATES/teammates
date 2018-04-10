package teammates.test.cases.action;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

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
import teammates.ui.controller.InstructorFeedbackResponseCommentAddAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * SUT: {@link InstructorFeedbackResponseCommentAddAction}.
 */
public class InstructorFeedbackResponseCommentAddActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();

        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        int questionNumber = 1;
        FeedbackQuestionAttributes question = feedbackQuestionsDb.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = feedbackResponsesDb.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response"
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("typical successful case for unpublished session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        InstructorFeedbackResponseCommentAddAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);
        InstructorFeedbackResponseCommentAjaxPageData data =
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("typical successful case for unpublished session empty giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Empty giver permissions",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("typical successful case for unpublished session shown to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Null comment permissions",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Empty comment permissions",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment shown to giver",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment shown to receiver",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment shown to own team members",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "OWN_TEAM_MEMBERS",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment shown to receiver team members",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER_TEAM_MEMBERS",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment shown to students",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "STUDENTS",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("typical successful case for published session");

        FeedbackSessionsLogic.inst().publishFeedbackSession(session);
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response, published session",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        result = getShowPageResult(action);
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        action = getAction(submissionParams);
        AjaxResult ajaxResult = getAjaxResult(action);
        assertEquals("", ajaxResult.getStatusMessage());
        data = (InstructorFeedbackResponseCommentAjaxPageData) ajaxResult.data;
        assertTrue(data.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, data.errorMessage);

    }

    @Override
    protected InstructorFeedbackResponseCommentAddAction getAction(String... params) {
        return (InstructorFeedbackResponseCommentAddAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = frDb.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);
        FeedbackResponseCommentAttributes comment = FeedbackResponseCommentAttributes
                .builder(fs.getCourseId(), fs.getFeedbackSessionName(), giverEmail, new Text(""))
                .withFeedbackQuestionId(question.getId())
                .withFeedbackResponseId(response.getId())
                .build();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, comment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, comment.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
                Const.ParamsNames.COMMENT_ID, "1-1-1-1"
        };

        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        // remove the comment
        frcDb.deleteEntity(comment);
    }
}

package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.FeedbackParticipantFeedbackResponseCommentDeleteAction;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

/**
 * SUT: {@link FeedbackParticipantFeedbackResponseCommentDeleteAction}.
 */
public class FeedbackParticipantFeedbackResponseCommentDeleteActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.FEEDBACK_PARTICIPANT_FEEDBACK_RESPONSE_COMMENT_DELETE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/FeedbackParticipantFeedbackResponseCommentDeleteTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First Session", "idOfCourse1", questionNumber);

        String giverEmail = "instructor1@course1.tmt";
        String receiverEmail = "instructor1@course1.tmt";
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                dataBundle.feedbackResponseComments.get("comment1FromInstructor1");

        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
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

        ______TS("Typical successful case when feedback participant is an instructor");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
        };

        FeedbackParticipantFeedbackResponseCommentDeleteAction action = getAction(submissionParams);
        AjaxResult result = getAjaxResult(action);

        FeedbackResponseCommentAjaxPageData data =
                (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());

        ______TS("Non-existent feedback response comment");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        action = getAction(submissionParams);
        result = getAjaxResult(action);

        data = (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());

        ______TS("Typical successful case when feedback participant is a student");

        questionNumber = 3;
        feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First Session", "idOfCourse1", questionNumber);
        giverEmail = "student1InCourse1@gmail.tmt";
        receiverEmail = "student1InCourse1@gmail.tmt";
        feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);

        feedbackResponseComment = dataBundle.feedbackResponseComments.get("comment1FromStudent1");

        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
        };
        result = getAjaxResult(getAction(submissionParams));
        data = (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());

    }

    @Override
    protected FeedbackParticipantFeedbackResponseCommentDeleteAction getAction(String... params) {
        return (FeedbackParticipantFeedbackResponseCommentDeleteAction) gaeSimulation.getActionObject(getActionUri(),
                params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        ______TS("Instructor as feedback participant");
        int questionNumber = 1;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromInstructor1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ1");

        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId())
        };

        InstructorAttributes instructor1InCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        verifyUnaccessibleWithoutLogin(submissionParams, instructor1InCourse1, student);
        verifyUnaccessibleForUnregisteredUsers(submissionParams, instructor1InCourse1, student);
        verifyUnaccessibleForStudents(submissionParams, instructor1InCourse1, student);

        InstructorAttributes differentInstructorInSameCourse = dataBundle.instructors.get("instructor2InCourse1");
        gaeSimulation.loginAsInstructor(differentInstructorInSameCourse.googleId);
        verifyCannotAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams, instructor1InCourse1);

        ______TS("Moderator can delete comment");
        InstructorAttributes moderator = dataBundle.instructors.get("instructor2InCourse1");
        String[] submissionParamsForModeration = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }

    @Test
    protected void testAccessControlsForCommentByStudent() {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        ______TS("Student as feedback participant");

        int questionNumber = 3;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ3");

        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId())
        };

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        verifyUnaccessibleWithoutLogin(submissionParams, instructor, student1InCourse1);
        verifyUnaccessibleForUnregisteredUsers(submissionParams, instructor, student1InCourse1);

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams, student1InCourse1);

        ______TS("Moderator can delete comment");

        InstructorAttributes moderator = dataBundle.instructors.get("instructor1InCourse1");
        String[] submissionParamsForModeration = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }
}

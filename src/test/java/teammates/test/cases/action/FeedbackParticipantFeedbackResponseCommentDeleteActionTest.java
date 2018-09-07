package teammates.test.cases.action;

import org.testng.annotations.BeforeMethod;
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
        // test data is refreshed before each test case
    }

    @BeforeMethod
    protected void refreshTestData() {
        dataBundle = loadDataBundle("/FeedbackParticipantFeedbackResponseCommentDeleteTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    public void testExecuteAndPostProcess() {
        // See each independent test case
    }

    @Test
    public void testFailureCases() {

        FeedbackResponseCommentAttributes feedbackResponseComment = getCommentFromInstructor1AsFeedbackParticipant();
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
        };

        verifyAssumptionFailure();
        verifyAssumptionFailure(submissionParams);

        ______TS("Non-existent feedback response comment: fails silently");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        AjaxResult result = getAjaxResult(getAction(submissionParams));
        FeedbackResponseCommentAjaxPageData data = (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
    }

    @Test
    public void testDeleteActionForInstructorAsFeedbackParticipant() {

        FeedbackResponseCommentAttributes feedbackResponseComment = getCommentFromInstructor1AsFeedbackParticipant();
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        FeedbackParticipantFeedbackResponseCommentDeleteAction action = getAction(submissionParams);
        AjaxResult result = getAjaxResult(action);

        FeedbackResponseCommentAjaxPageData data =
                (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());
    }

    private FeedbackResponseCommentAttributes getCommentFromInstructor1AsFeedbackParticipant() {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First Session", "idOfCourse1", questionNumber);

        String giverEmail = "instructor1@course1.tmt";
        String receiverEmail = "%GENERAL%";
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                dataBundle.feedbackResponseComments.get("comment1FromInstructor1");
        FeedbackResponseCommentAttributes frc =
                feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.courseId,
                        feedbackResponseComment.createdAt, feedbackResponseComment.commentGiver);
        frc.feedbackResponseId = feedbackResponse.getId();
        return frc;
    }

    @Test
    protected void testDeleteActionForStudentAsFeedbackParticipant() {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        int questionNumber = 3;
        FeedbackQuestionAttributes feedbackQuestion =
                feedbackQuestionsDb.getFeedbackQuestion("First Session", "idOfCourse1", questionNumber);
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes feedbackResponse =
                feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                dataBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };
        AjaxResult result = getAjaxResult(getAction(submissionParams));
        FeedbackResponseCommentAjaxPageData data = (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());
    }

    @Test
    public void testDeleteActionForTeamAsFeedbackParticipant() {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        int questionNumber = 4;
        FeedbackQuestionAttributes feedbackQuestion =
                feedbackQuestionsDb.getFeedbackQuestion("First Session", "idOfCourse1", questionNumber);
        String giverEmail = "Team 1.1</td></div>'\"";
        String receiverEmail = "Team 2.1</td></div>'\"";
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes feedbackResponse =
                feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                dataBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };
        AjaxResult result = getAjaxResult(getAction(submissionParams));
        FeedbackResponseCommentAjaxPageData data = (FeedbackResponseCommentAjaxPageData) result.data;

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
    protected void testAccessControl() throws Exception {
        // See each independent test case
    }

    @Test
    public void testAccessControlsForCommentByInstructor() {

        FeedbackResponseCommentAttributes comment = getCommentFromInstructor1AsFeedbackParticipant();

        ______TS("Moderator can delete comment");

        InstructorAttributes moderator = dataBundle.instructors.get("instructor2InCourse1");
        String[] submissionParamsForModeration = new String[] {
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, comment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }

    @Test
    public void testAccessControlsForCommentByStudent() {

        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        int questionNumber = 3;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ3");

        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString()
        };

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("Moderator can delete comment");

        InstructorAttributes moderator = dataBundle.instructors.get("instructor1InCourse1");
        String[] submissionParamsForModeration = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }

    @Test
    public void testAccessControlsForCommentByTeam() {

        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        int questionNumber = 4;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ4");

        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString()
        };

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student3InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameTeam.googleId);
        verifyCanAccess(submissionParams);

        ______TS("Moderator can delete comment");

        InstructorAttributes moderator = dataBundle.instructors.get("instructor1InCourse1");
        String[] submissionParamsForModeration = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }
}

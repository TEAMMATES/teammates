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
        testFailureCases();
        testDeleteActionForInstructorAsFeedbackParticipant();
        testDeleteActionForStudentAsFeedbackParticipant();
        testDeleteActionForTeamAsFeedbackParticipant();
    }

    private void testFailureCases() {
        FeedbackResponseCommentAttributes feedbackResponseComment = getCommentFromInstructor1();
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

    private void testDeleteActionForInstructorAsFeedbackParticipant() {
        FeedbackResponseCommentAttributes feedbackResponseComment = getCommentFromInstructor1();
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
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());
    }

    private FeedbackResponseCommentAttributes getCommentFromInstructor1() {
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

        return feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
    }

    private void testDeleteActionForStudentAsFeedbackParticipant() {
        ______TS("Typical successful case when feedback participant is a student");

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
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
        };
        AjaxResult result = getAjaxResult(getAction(submissionParams));
        FeedbackResponseCommentAjaxPageData data = (FeedbackResponseCommentAjaxPageData) result.data;

        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("", result.getStatusMessage());
    }

    private void testDeleteActionForTeamAsFeedbackParticipant() {
        ______TS("Typical successful case when feedback participant is a team");

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
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
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
    @Test
    protected void testAccessControl() throws Exception {
        testAccessControlsForCommentByInstructor();
        testAccessControlsForCommentByStudent();
        testAccessControlsForCommentByTeam();
    }

    protected void testAccessControlsForCommentByInstructor() {
        ______TS("Instructor as feedback participant");
        FeedbackResponseCommentAttributes comment = getCommentFromInstructor1();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, comment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
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
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, comment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderator.email
        };
        gaeSimulation.loginAsInstructor(moderator.googleId);
        verifyCanAccess(submissionParamsForModeration);
    }

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

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString()
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

    private void testAccessControlsForCommentByTeam() {
        final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

        ______TS("Team as feedback participant");

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
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString()
        };

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        verifyUnaccessibleWithoutLogin(submissionParams, instructor, student1InCourse1);
        verifyUnaccessibleForUnregisteredUsers(submissionParams, instructor, student1InCourse1);

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student3InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameTeam.googleId);
        verifyCanAccess(submissionParams);

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

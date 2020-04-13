package teammates.test.cases.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.DeleteFeedbackResponseCommentAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.Intent;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    protected void refreshTestData() {
        typicalBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    @Test
    public void testExecute() {
        FeedbackResponseCommentAttributes feedbackResponseComment =
                typicalBundle.feedbackResponseComments.get("comment1FromInstructor1");

        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseComment.getFeedbackResponseId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical successful case, comment deleted");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        DeleteFeedbackResponseCommentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(feedbackResponseComment.getId()));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());

        ______TS("Non-existent feedback response comment, non-existent comment should fail silently");

        submissionParams = new String[] {
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(123123123123123L));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        // See each independent test case
    }

    @Test
    protected void testAccessControlsForCommentByInstructor() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1Q2");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByInstructorAsFeedbackParticipant() {
        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        ______TS("Instructor who give the comment can delete comment");

        InstructorAttributes instructorWhoGiveComment = typicalBundle.instructors.get("instructor1OfCourse1");
        assertEquals(instructorWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsInstructor(instructorWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different instructor of same course cannot delete comment");

        InstructorAttributes differentInstructorInSameCourse = typicalBundle.instructors.get("instructor2OfCourse1");
        assertNotEquals(differentInstructorInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByStudent() {
        int questionNumber = 3;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ3");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Student who give the comment can delete comment");

        StudentAttributes studentWhoGiveComment = typicalBundle.students.get("student1InCourse1");
        assertEquals(studentWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsStudent(studentWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student2InCourse1");
        assertNotEquals(differentStudentInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByTeam() {
        int questionNumber = 4;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ4");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        assertEquals(FeedbackParticipantType.TEAMS, question.getGiverType());
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student3InCourse1");
        assertNotEquals(differentStudentInSameCourse.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = typicalBundle.students.get("student2InCourse1");
        assertEquals(differentStudentInSameTeam.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameTeam.getGoogleId());
        verifyCanAccess(submissionParams);

    }

}

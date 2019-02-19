package teammates.test.cases.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
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

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

    private DataBundle dataBundle;

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
        dataBundle = loadDataBundle("/FeedbackParticipantFeedbackResponseCommentDeleteTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecute() {
        removeAndRestoreTypicalDataBundle();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment = typicalBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical successful case");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        DeleteFeedbackResponseCommentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());

        ______TS("Non-existent feedback response comment");

        submissionParams = new String[] {
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());

        ______TS("Instructor is not feedback response comment giver");

        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");

        questionNumber = 2;
        feedbackQuestion = logic.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        giverEmail = "student2InCourse1@gmail.tmt";
        receiverEmail = "student5InCourse1@gmail.tmt";
        feedbackResponse = logic.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);
        feedbackResponseComment = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        // See each independent test case
    }

    @Test
    protected void testAccessControlsForCommentByInstructor() throws Exception {
        int questionNumber = 2;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ2S1C1");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
        };
        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByStudent() {
        int questionNumber = 3;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ3");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
        };

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

    }

    @Test
    public void testAccessControlsForCommentByTeam() {
        int questionNumber = 4;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("Open Session");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ4");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
        };

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = dataBundle.students.get("student3InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = dataBundle.students.get("student2InCourse1");
        gaeSimulation.loginAsStudent(differentStudentInSameTeam.googleId);
        verifyCanAccess(submissionParams);

    }

}

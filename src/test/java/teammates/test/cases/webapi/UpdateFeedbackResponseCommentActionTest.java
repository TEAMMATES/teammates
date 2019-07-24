package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.UpdateFeedbackResponseCommentAction;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionTest extends BaseActionTest<UpdateFeedbackResponseCommentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse =
                logic.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment =
                typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful csae: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical successful case for unpublished session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Null show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody =
                new FeedbackResponseCommentUpdateRequest(feedbackResponseComment.commentText + " (Edited)", null, null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Empty show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(feedbackResponseComment.commentText + " (Edited)", "", "");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Typical successful case for unpublished session public to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(feedbackResponseComment.commentText + " (Edited)", "", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "RECEIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "OWN_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "RECEIVER_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "STUDENTS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Non-existent feedback response comment id");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        UpdateFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));

        ______TS("Instructor is not feedback response comment giver");

        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertEquals("instructor2@course1.tmt", frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for published session");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        FeedbackSessionsLogic.inst().publishFeedbackSession(
                feedbackResponseComment.feedbackSessionName, feedbackResponseComment.courseId);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                feedbackResponseComment.commentText + " (Edited for published session)", "GIVER,INSTRUCTORS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "", null, null);
        action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
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

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };
        // this person is not the giver. so not accessible
        verifyInaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}

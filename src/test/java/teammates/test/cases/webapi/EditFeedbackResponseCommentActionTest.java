package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.webapi.action.EditFeedbackResponseCommentAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentSaveRequest;

/**
 * SUT: {@link EditFeedbackResponseCommentAction}.
 */
public class EditFeedbackResponseCommentActionTest extends BaseActionTest<EditFeedbackResponseCommentAction> {

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

        FeedbackResponseCommentSaveRequest requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        EditFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Null show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(feedbackResponseComment.commentText + " (Edited)", null, null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Empty show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(feedbackResponseComment.commentText + " (Edited)", "", "");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Typical successful case for unpublished session public to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(feedbackResponseComment.commentText + " (Edited)", "", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "RECEIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "OWN_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "RECEIVER_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "STUDENTS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Non-existent feedback response comment id");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        EditFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));

        ______TS("Instructor is not feedback response comment giver");

        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited)", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertEquals("instructor2@course1.tmt", frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for published session");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        FeedbackSessionAttributes fs =
                FeedbackSessionsLogic.inst().getFeedbackSession(feedbackResponseComment.feedbackSessionName,
                                                                feedbackResponseComment.courseId);
        FeedbackSessionsLogic.inst().publishFeedbackSession(fs);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
                feedbackResponseComment.commentText + " (Edited for published session)", "GIVER,INSTRUCTORS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.getId());
        assertEquals(feedbackResponseComment.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentSaveRequest(
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
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

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
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "comment",
        };
        // this person is not the giver. so not accessible
        verifyInaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}

package teammates.test.cases.webapi;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.ui.webapi.action.CreateFeedbackResponseCommentAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionTest extends BaseActionTest<CreateFeedbackResponseCommentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        int questionNumber = 1;
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = logic.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("typical successful case for unpublished session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        FeedbackResponseCommentUpdateRequest requestBody =
                new FeedbackResponseCommentUpdateRequest("Comment to first response", null, "GIVER,INSTRUCTORS");
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> frcList =
                getInstructorComments(response.getId(), "Comment to first response");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);

        ______TS("typical successful case for unpublished session empty giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Empty giver permissions", null, "");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("typical successful case for unpublished session shown to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Null comment permissions", null, null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Empty comment permissions", "", "");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to giver", "GIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to receiver", "RECEIVER", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody =
                new FeedbackResponseCommentUpdateRequest("Comment shown to own team members", "OWN_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "Comment shown to receiver team members", "RECEIVER_TEAM_MEMBERS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to students", "STUDENTS", null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("typical successful case for published session");

        FeedbackSessionsLogic.inst().publishFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "Comment to first response, published session", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frcList = getInstructorComments(response.getId(), "Comment to first response, published session");
        assertEquals(1, frcList.size());
        frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("", null, null);
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
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = logic.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);
        FeedbackResponseCommentAttributes comment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(fs.getCourseId())
                .withFeedbackSessionName(fs.getFeedbackSessionName())
                .withCommentGiver(giverEmail)
                .withCommentText("")
                .withFeedbackQuestionId(question.getId())
                .withFeedbackResponseId(response.getId())
                .build();

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
        };

        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    /**
     * Filters instructor comments according to comment text from all comments on a response.
     *
     * @param responseId response id of response
     * @param commentText comment text
     * @return instructor comments
     */
    private List<FeedbackResponseCommentAttributes> getInstructorComments(String responseId, String commentText) {
        FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
        return frcDb.getFeedbackResponseCommentsForResponse(responseId)
                .stream()
                .filter(comment -> comment.commentText.equals(commentText))
                .collect(Collectors.toList());
    }

}

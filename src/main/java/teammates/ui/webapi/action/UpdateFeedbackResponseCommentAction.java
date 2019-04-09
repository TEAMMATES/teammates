package teammates.ui.webapi.action;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * Updates a feedback response comment.
 */
public class UpdateFeedbackResponseCommentAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String courseId = frc.courseId;
        String feedbackResponseId = frc.feedbackResponseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        if (instructor != null && frc.commentGiver.equals(instructor.email)) { // giver, allowed by default
            return;
        }

        String feedbackSessionName = frc.feedbackSessionName;
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    @Override
    public ActionResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String feedbackResponseId = frc.feedbackResponseId;

        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        // Edit comment text
        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            return new JsonResult(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = frc.courseId;
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        FeedbackResponseCommentAttributes.UpdateOptions.Builder commentUpdateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(feedbackResponseCommentId)
                        .withCommentText(commentText)
                        .withLastEditorEmail(instructor.email)
                        .withLastEditorAt(Instant.now());

        // edit visibility settings
        String showCommentTo = comment.getShowCommentTo();
        String showGiverNameTo = comment.getShowGiverNameTo();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            List<FeedbackParticipantType> showCommentToList = new ArrayList<>();
            for (String viewer : showCommentToArray) {
                showCommentToList.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
            commentUpdateOptions.withShowCommentTo(showCommentToList);
        }
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            List<FeedbackParticipantType> showGiverNameToList = new ArrayList<>();
            for (String viewer : showGiverNameToArray) {
                showGiverNameToList.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
            commentUpdateOptions.withShowGiverNameTo(showGiverNameToList);
        }

        FeedbackResponseCommentAttributes updatedComment = null;
        try {
            updatedComment = logic.updateFeedbackResponseComment(commentUpdateOptions.build());
            logic.putDocument(updatedComment);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new FeedbackResponseCommentData(updatedComment));
    }

}

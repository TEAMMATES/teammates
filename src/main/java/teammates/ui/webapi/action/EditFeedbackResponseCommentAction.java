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

/**
 * Edits a feedback response comment.
 */
public class EditFeedbackResponseCommentAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String feedbackResponseId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        if (instructor != null && frc.commentGiver.equals(instructor.email)) { // giver, allowed by default
            return;
        }
        gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String feedbackResponseId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        // Edit comment text
        String commentText = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        if (commentText.trim().isEmpty()) {
            return new JsonResult(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
        }

        FeedbackResponseCommentAttributes.UpdateOptions.Builder commentUpdateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(feedbackResponseCommentId)
                        .withCommentText(commentText)
                        .withLastEditorEmail(instructor.email)
                        .withLastEditorAt(Instant.now());

        // edit visibility settings
        String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO);
        String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO);
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

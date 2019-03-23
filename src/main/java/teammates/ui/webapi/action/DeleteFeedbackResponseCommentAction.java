package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Deletes a feedback response comment.
 */
public class DeleteFeedbackResponseCommentAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            return;
        }

        String courseId = frc.courseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        if (instructor != null && frc.commentGiver.equals(instructor.email)) { // giver, allowed by default
            return;
        }

        // Case 1: the delete request comes from instructor

        if (instructor != null) {
            String feedbackSessionName = frc.feedbackSessionName;
            String feedbackResponseId = frc.feedbackResponseId;
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);

            gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            return;
        }

        // Case 2: the delete request comes from the feedback response giver

        if (!frc.isCommentFromFeedbackParticipant) {
            throw new UnauthorizedAccessException("Comment [" + frc.getId() + "] not given by feedback participant.");
        }

        switch (frc.commentGiverType) {
        case INSTRUCTORS:
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            gateKeeper.verifyOwnership(frc, instructor.email);
            break;
        case STUDENTS:
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            gateKeeper.verifyOwnership(frc, student.email);
            break;
        case TEAMS:
            StudentAttributes studentOfTeam = logic.getStudentForGoogleId(courseId, userInfo.id);
            if (studentOfTeam == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            gateKeeper.verifyOwnership(frc, studentOfTeam.team);
            break;
        default:
            Assumption.fail("Invalid comment giver type");
            break;
        }
    }

    @Override
    public ActionResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        logic.deleteFeedbackResponseComment(feedbackResponseCommentId);

        return new JsonResult("Successfully deleted feedback response comment.");
    }

}

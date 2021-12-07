package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;

/**
 * Restore a feedback session from the recycle bin.
 */
class RestoreFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session is not in recycle bin");
        }

        try {
            logic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        FeedbackSessionAttributes restoredFs = getNonNullFeedbackSession(feedbackSessionName, courseId);
        FeedbackSessionData output = new FeedbackSessionData(restoredFs);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }

}

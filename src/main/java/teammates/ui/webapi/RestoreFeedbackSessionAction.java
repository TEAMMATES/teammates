package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;

/**
 * Restore a feedback session from the recycle bin.
 */
public class RestoreFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = sqlLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);

            gateKeeper.verifyAccessible(
                    sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            FeedbackSessionAttributes feedbackSession =
                    logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);

            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        }
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = sqlLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
            if (feedbackSession == null) {
                throw new EntityNotFoundException("Feedback session is not in recycle bin");
            }

            try {
                sqlLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }

            FeedbackSession restoredFs = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            FeedbackSessionData output = new FeedbackSessionData(restoredFs);
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
            output.setPrivileges(privilege);

            return new JsonResult(output);
        } else {
            return executeOldFeedbackSession(courseId, feedbackSessionName);
        }
    }

    private JsonResult executeOldFeedbackSession(String courseId, String feedbackSessionName) {
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

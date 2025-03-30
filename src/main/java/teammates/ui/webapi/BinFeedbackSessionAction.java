package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.FeedbackSessionData;

/**
 * Move the feedback session to the recycle bin.
 */
public class BinFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            gateKeeper.verifyAccessible(
                    sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
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
            try {
                FeedbackSession fs = sqlLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
                return new JsonResult(new FeedbackSessionData(fs));
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else {
            return oldFeedbackSession(courseId, feedbackSessionName);
        }
    }

    private JsonResult oldFeedbackSession(String courseId, String feedbackSessionName) {
        try {
            logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        FeedbackSessionAttributes recycleBinFs = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        return new JsonResult(new FeedbackSessionData(recycleBinFs));
    }

}

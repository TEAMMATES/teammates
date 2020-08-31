package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;

/**
 * Restore a feedback session from the recycle bin.
 */
public class RestoreFeedbackSessionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback session is not in recycle bin"));
        }

        try {
            logic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        FeedbackSessionAttributes restoredFs = getNonNullFeedbackSession(feedbackSessionName, courseId);
        return new JsonResult(new FeedbackSessionData(restoredFs));
    }

}

package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;

/**
 * Publish a feedback session.
 */
public class PublishFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        gateKeeper.verifyAccessible(instructor, feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        if (feedbackSession.isPublished()) {
            // If feedback session was already published to begin with, return early
            return new JsonResult(new FeedbackSessionData(feedbackSession));
        }

        try {
            FeedbackSession publishFeedbackSession = sqlLogic.publishFeedbackSession(feedbackSessionName, courseId);

            if (publishFeedbackSession.isPublishedEmailEnabled()) {
                taskQueuer.scheduleFeedbackSessionPublishedEmail(courseId, feedbackSessionName);
            }

            return new JsonResult(new FeedbackSessionData(publishFeedbackSession));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

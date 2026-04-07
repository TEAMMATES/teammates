package teammates.ui.webapi;

import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnexpectedServerException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;

/**
 * Unpublish a feedback session.
 */
public class UnpublishFeedbackSessionAction extends Action {

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
        if (!feedbackSession.isPublished()) {
            // If feedback session was not published to begin with, return early
            return new JsonResult(new FeedbackSessionData(feedbackSession));
        }

        try {
            FeedbackSession unpublishFeedbackSession =
                    sqlLogic.unpublishFeedbackSession(feedbackSessionName, courseId);

            if (unpublishFeedbackSession.isPublishedEmailEnabled()) {
                // Generate and queue unpublished emails to send-email-queue
                List<EmailWrapper> emailsToBeSent =
                        sqlEmailGenerator.generateFeedbackSessionUnpublishedEmails(unpublishFeedbackSession);
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                unpublishFeedbackSession.setPublishedEmailSent(false);
                sqlLogic.adjustFeedbackSessionEmailStatusAfterUpdate(unpublishFeedbackSession);
            }
            return new JsonResult(new FeedbackSessionData(unpublishFeedbackSession));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            // There should not be any invalid parameter here
            throw new UnexpectedServerException(e.getMessage(), e);
        }
    }
}

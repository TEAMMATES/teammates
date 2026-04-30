package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
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
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        Instructor instructor = sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());

        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        try {
            FeedbackSession unpublishFeedbackSession =
                    sqlLogic.unpublishFeedbackSession(feedbackSessionId);

            sendUnpublishedEmails(unpublishFeedbackSession);
            return new JsonResult(new FeedbackSessionData(unpublishFeedbackSession));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidFeedbackSessionStateException e) {
            throw new InvalidOperationException(e);
        }
    }

    private void sendUnpublishedEmails(FeedbackSession feedbackSession) {
        if (feedbackSession.isPublishedEmailEnabled()) {
            List<EmailWrapper> emailsToBeSent =
                    emailGenerator.generateFeedbackSessionUnpublishedEmails(feedbackSession);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
        }
    }
}

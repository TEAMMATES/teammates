package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;

/**
 * Publish a feedback session.
 */
public class PublishFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        Instructor instructor = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), authContext.id());

        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        try {
            FeedbackSession publishFeedbackSession = logic.publishFeedbackSession(feedbackSessionId);

            sendPublishedEmails(publishFeedbackSession);
            return new JsonResult(new FeedbackSessionData(publishFeedbackSession));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidFeedbackSessionStateException e) {
            throw new InvalidOperationException(e);
        }
    }

    private void sendPublishedEmails(FeedbackSession feedbackSession) {
        if (feedbackSession.isPublishedEmailEnabled()) {
            List<EmailWrapper> emailsToBeSent =
                    emailGenerator.generateFeedbackSessionPublishedEmails(feedbackSession);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
            feedbackSession.setPublishedEmailSent(true);
        }
    }
}

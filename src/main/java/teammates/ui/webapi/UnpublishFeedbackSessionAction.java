package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;

/**
 * Unpublish a feedback session.
 */
public class UnpublishFeedbackSessionAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        gateKeeper.verifyInstructorHasPrivilegeInFeedbackSession(requestContext, feedbackSessionId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        try {
            FeedbackSession unpublishFeedbackSession =
                    logic.unpublishFeedbackSession(feedbackSessionId);

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
            emailQueueService.enqueueStandard(emailsToBeSent);
        }
    }
}

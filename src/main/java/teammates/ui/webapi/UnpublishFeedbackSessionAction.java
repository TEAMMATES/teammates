package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.Const;
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
                    logic.unpublishFeedbackSessionAndEnqueueEmails(feedbackSessionId);
            return new JsonResult(new FeedbackSessionData(unpublishFeedbackSession));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidFeedbackSessionStateException e) {
            throw new InvalidOperationException(e);
        }
    }
}

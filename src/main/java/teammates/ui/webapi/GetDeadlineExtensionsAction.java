package teammates.ui.webapi;

import java.util.Set;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionsData;

/**
 * Gets the deadline extensions for a feedback session.
 */
public class GetDeadlineExtensionsAction extends Action {

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

        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), authContext.id()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        Set<DeadlineExtension> deadlineExtensions;
        try {
            deadlineExtensions = logic.getDeadlineExtensions(feedbackSessionId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        DeadlineExtensionsData responseData = new DeadlineExtensionsData(deadlineExtensions);

        return new JsonResult(responseData);
    }
}

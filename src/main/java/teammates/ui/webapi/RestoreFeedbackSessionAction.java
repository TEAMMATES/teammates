package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionSet;
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
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("The feedback session does not exist.");
        }

        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession;

        try {
            feedbackSession = logic.restoreFeedbackSessionFromRecycleBin(feedbackSessionId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        FeedbackSessionData output = new FeedbackSessionData(feedbackSession);

        Instructor instructor = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSession.getName());
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }
}

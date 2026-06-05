package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
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

        Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());
        gateKeeper.verifyInstructorCanAccessSession(instructor, feedbackSession);
        gateKeeper.verifyAccessible(instructor, Const.InstructorPermissions.CAN_MODIFY_SESSION);
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

        Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());
        InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSession.getName());
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }
}

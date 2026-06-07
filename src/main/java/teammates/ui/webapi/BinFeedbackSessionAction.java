package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;

/**
 * Move the feedback session to the recycle bin.
 */
public class BinFeedbackSessionAction extends Action {

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
        gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        try {
            FeedbackSession fs = logic.moveFeedbackSessionToRecycleBin(feedbackSessionId);
            FeedbackSessionViewData output = new FeedbackSessionViewData(new FeedbackSessionData(fs));
            Instructor instructor = getInstructorFromRequest(fs.getCourseId());
            if (instructor != null) {
                output.setInstructorPermissions(getPermissions(fs, instructor));
            }
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSession fs, Instructor instructor) {
        boolean canModifySession =
                logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, fs.getName(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        boolean canViewSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, fs.getName(),
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        return new InstructorFeedbackSessionPermissionsData(canModifySession,
                canSubmitSessionInSections, canViewSessionInSections);
    }
}

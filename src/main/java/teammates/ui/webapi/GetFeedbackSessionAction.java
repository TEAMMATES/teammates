package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;

/**
 * Get a feedback session.
 */
public class GetFeedbackSessionAction extends RegKeyAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        gateKeeper.verifyFeedbackSessionAccessible(requestContext, feedbackSessionId);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        FeedbackSessionViewData response = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));

        User user = getUserFromRequest(feedbackSession.getCourseId());
        if (user instanceof Instructor instructor) {
            response.setInstructorPermissions(getPermissions(feedbackSession, instructor));
        }

        boolean canViewFullDetails = requestContext.isAdmin()
                || user instanceof Instructor instructor
                        && (logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_VIEW_SESSION)
                                || logic.hasInstructorPermissionsForSectionInAnySection(instructor,
                                        feedbackSession.getId(), Const.InstructorPermissions.CAN_VIEW_SESSION));

        if (!canViewFullDetails) {
            response.getFeedbackSession().hideInformation();
        }

        return new JsonResult(response);
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSession feedbackSession,
            Instructor instructor) {
        boolean canModifySession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, feedbackSession.getId(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION);
        boolean canViewSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, feedbackSession.getId(),
                        Const.InstructorPermissions.CAN_VIEW_SESSION);
        return new InstructorFeedbackSessionPermissionsData(
                canModifySession,
                canSubmitSession,
                canViewSession);
    }
}

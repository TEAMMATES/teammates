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
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;

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

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        try {
            FeedbackSession unpublishFeedbackSession =
                    logic.unpublishFeedbackSession(feedbackSessionId);

            sendUnpublishedEmails(unpublishFeedbackSession);
            FeedbackSessionViewData output = new FeedbackSessionViewData(
                    new FeedbackSessionData(unpublishFeedbackSession));
            Instructor instructor = getInstructorFromRequest(unpublishFeedbackSession.getCourseId());
            if (instructor != null) {
                output.setInstructorPermissions(getPermissions(unpublishFeedbackSession, instructor));
            }
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidFeedbackSessionStateException e) {
            throw new InvalidOperationException(e);
        }
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSession unpublishFeedbackSession,
            Instructor instructor) {
        boolean canModifySession =
                logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, unpublishFeedbackSession.getName(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        boolean canViewSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                 || logic.hasInstructorPermissionsForSectionInAnySection(instructor, unpublishFeedbackSession.getName(),
                 Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        return new InstructorFeedbackSessionPermissionsData(
                canModifySession, canSubmitSessionInSections, canViewSessionInSections);
    }

    private void sendUnpublishedEmails(FeedbackSession feedbackSession) {
        if (feedbackSession.isPublishedEmailEnabled()) {
            List<EmailWrapper> emailsToBeSent =
                    emailGenerator.generateFeedbackSessionUnpublishedEmails(feedbackSession);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
        }
    }
}

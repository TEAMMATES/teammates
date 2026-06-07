package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * Updates a feedback session.
 */
public class UpdateFeedbackSessionAction extends Action {

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
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        try {
            FeedbackSession feedbackSession = logic.updateFeedbackSession(feedbackSessionId, updateRequest);
            FeedbackSessionViewData output = new FeedbackSessionViewData(new FeedbackSessionData(feedbackSession));
            Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());
            if (instructor != null) {
                output.setInstructorPermissions(getPermissions(feedbackSession, instructor));
            }
            return new JsonResult(output);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSession feedbackSession, Instructor instructor) {
        boolean canModifySession =
                logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, feedbackSession.getName(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        boolean canViewSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                 || logic.hasInstructorPermissionsForSectionInAnySection(instructor, feedbackSession.getName(),
                 Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        return new InstructorFeedbackSessionPermissionsData(
                canModifySession, canSubmitSessionInSections, canViewSessionInSections);
    }
}

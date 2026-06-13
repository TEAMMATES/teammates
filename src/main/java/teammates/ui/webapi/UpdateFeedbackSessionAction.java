package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * Updates a feedback session.
 */
public class UpdateFeedbackSessionAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        gateKeeper.verifyInstructorHasPrivilegeInFeedbackSession(requestContext, feedbackSessionId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        try {
            FeedbackSession feedbackSession = logic.updateFeedbackSession(feedbackSessionId, updateRequest);
            return new JsonResult(new FeedbackSessionData(feedbackSession));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }
}

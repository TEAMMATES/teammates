package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.request.DeadlineExtensionsUpdateRequest;

/**
 * Updates the deadline extensions for a feedback session.
 */
public class UpdateDeadlineExtensionsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        gateKeeper.verifyInstructorHasPrivilegeInFeedbackSession(requestContext, feedbackSessionId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        boolean notifyAboutDeadlines = getBooleanRequestParamValue(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES);
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        DeadlineExtensionsUpdateRequest updateRequest =
                getAndValidateRequestBody(DeadlineExtensionsUpdateRequest.class);
        List<UpdateExtensionsResult> updateResults;
        try {
            updateResults = notifyAboutDeadlines
                    ? logic.updateDeadlineExtensionsAndNotify(feedbackSession, updateRequest.getUserDeadlines())
                    : logic.updateDeadlineExtensions(feedbackSession, updateRequest.getUserDeadlines());
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        DeadlineExtensionsData responseData = new DeadlineExtensionsData(updateResults);

        return new JsonResult(responseData);
    }
}

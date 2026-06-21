package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.UserSessionResultsData;

/**
 * Gets user-scoped feedback session results for instructor/student result views.
 */
public class GetUserSessionResultsAction extends RegKeyAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        boolean isPreview = getBooleanRequestParamValue(Const.ParamsNames.IS_PREVIEW);

        if (isPreview) {
            gateKeeper.verifyCanPreviewUserSessionResults(requestContext, feedbackSessionId);
        } else {
            gateKeeper.verifyCanViewUserSessionResults(requestContext, feedbackSessionId, userId);
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        boolean isPreview = getBooleanRequestParamValue(Const.ParamsNames.IS_PREVIEW);
        User user = getUser();

        SessionResultsBundle bundle;
        try {
            bundle = logic.getSessionResultsForUser(feedbackSessionId, user, isPreview);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
        return new JsonResult(UserSessionResultsData.initForUser(bundle, user));
    }

    private User getUser() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        User user = logic.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

}

package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.DeadlineExtension;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionData;

/**
 * Gets the deadline extension for a specific user in a feedback session.
 */
public class GetDeadlineExtensionAction extends RegKeyAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        gateKeeper.verifyCanViewDeadlineExtension(requestContext, feedbackSessionId, userId);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        DeadlineExtension de = logic.getDeadlineExtension(feedbackSessionId, userId);
        if (de == null) {
            throw new EntityNotFoundException("No deadline extension found for this user in this session.");
        }

        return new JsonResult(new DeadlineExtensionData(de));
    }
}

package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;

/**
 * Gets all feedback session links for a user.
 */
public class GetSessionLinksAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        try {
            return new JsonResult(logic.getSessionLinks(userId));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}

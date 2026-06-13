package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.SessionLinksBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.SessionLinksData;

/**
 * Gets all feedback session links for a user.
 */
public class GetSessionLinksAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        try {
            SessionLinksBundle sessionLinks = logic.getSessionLinks(userId);
            return new JsonResult(new SessionLinksData(sessionLinks));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}

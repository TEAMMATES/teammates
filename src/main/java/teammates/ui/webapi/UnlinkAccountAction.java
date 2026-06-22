package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;

/**
 * Unlinks the account associated with the user profile without deleting
 * either entity, allowing the profile to be linked to a different account.
 */
public class UnlinkAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        try {
            logic.unlinkAccountAndNotify(userId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult("Account unlinked successfully.");
    }

}

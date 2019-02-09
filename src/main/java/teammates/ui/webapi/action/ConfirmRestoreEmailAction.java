package teammates.ui.webapi.action;

import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;

/**
 * Action specifically created for confirming email and sending recovery link.
 */
public class ConfirmRestoreEmailAction extends Action{
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {

    }

    @Override
    public ActionResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.RESTORE_EMAIL);

    }
}

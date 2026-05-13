package teammates.ui.webapi;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action specifically created for returning user cookie value.
 */
public class GetUserCookieAction extends Action {

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        throw new UnauthorizedAccessException("This resource can only be accessed with backdoor key.");
    }

    @Override
    public JsonResult execute() {
        // TODO: Directly get existing account with accountId. Account should not be created here.
        // Warning: createOrGetAccountForEmail may fail silently here for tests if the email is invalid.
        String user = getNonNullRequestParamValue(Const.ParamsNames.USER_ID);
        Account account = accountsLogic.createOrGetAccountForEmail(user);
        UserInfoCookie uic = new UserInfoCookie(user, account.getId());
        return new JsonResult(StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
    }

}

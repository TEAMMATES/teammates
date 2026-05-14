package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action specifically created for returning user cookie value.
 */
public class GetUserCookieAction extends Action {

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
        String user = getNonNullRequestParamValue(Const.ParamsNames.USER_ID);
        String accountId = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_ID);
        UUID accountUuid = UUID.fromString(accountId);
        Account account = logic.getAccount(accountUuid);

        if (account == null) {
            throw new EntityNotFoundException("Account does not exist for " + accountId);
        }

        UserInfoCookie uic = new UserInfoCookie(user, accountUuid);
        return new JsonResult(StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
    }

}

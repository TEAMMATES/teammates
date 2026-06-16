package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
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
        String email = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_EMAIL);
        UUID accountId = logic.createOrGetAccount(Provider.TEAMMATES_DEV, email, null, email).getId();
        UserInfoCookie uic = new UserInfoCookie(accountId);
        return new JsonResult(StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
    }

}

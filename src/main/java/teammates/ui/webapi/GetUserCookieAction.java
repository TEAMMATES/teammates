package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
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
        // TODO: Fetch account by accountId and update relevant call sites such as the back door.
        String user = getNonNullRequestParamValue(Const.ParamsNames.USER_ID);
        Account account = logic.getAccountForGoogleId(user);

        assert account != null : "Account should not be null for a valid user id.";

        UUID accountId = account.getId();
        UserInfoCookie uic = new UserInfoCookie(user, accountId);
        return new JsonResult(StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
    }

}

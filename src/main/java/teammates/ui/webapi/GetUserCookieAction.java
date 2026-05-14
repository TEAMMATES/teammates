package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
import teammates.ui.exception.InvalidHttpParameterException;
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
        UUID accountId = getOrCreateAccountId(user);
        UserInfoCookie uic = new UserInfoCookie(user, accountId);
        return new JsonResult(StringHelper.encrypt(JsonUtils.toCompactJson(uic)));
    }

    private UUID getOrCreateAccountId(String userId) {
        Account existingAccount = logic.getAccountForGoogleId(userId);
        if (existingAccount != null) {
            return existingAccount.getId();
        }

        String email = isValidEmail(userId) ? userId : getUniqueFallbackEmail();
        Account newAccount = new Account(userId, "Test User", email);
        try {
            logic.createAccount(newAccount);
            return newAccount.getId();
        } catch (EntityAlreadyExistsException e) {
            throw new IllegalStateException("Failed to create existing account for email: " + email, e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpParameterException(e);
        }
    }

    private boolean isValidEmail(String email) {
        return FieldValidator.getInvalidityInfoForEmail(email).isEmpty();
    }

    private String getUniqueFallbackEmail() {
        return "test.user." + UUID.randomUUID() + Const.TEST_EMAIL_DOMAIN;
    }

}

package teammates.logic.api;

import jakarta.servlet.http.HttpServletRequest;
import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.AutomatedRequestAuth;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.User;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.webapi.AuthType;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final UserProvision instance = new UserProvision();

    private static final AuthContext ALL_ACCESS_AUTH_CONTEXT = new AuthContext(
            AuthType.ALL_ACCESS,
            null,
            null,
            true,
            true);

    private static final AuthContext AUTOMATED_SERVICE_AUTH_CONTEXT = new AuthContext(
            AuthType.AUTOMATED_SERVICE,
            null,
            null,
            false,
            false);

    private static final AuthContext PUBLIC_AUTH_CONTEXT = new AuthContext(
            AuthType.PUBLIC,
            null,
            null,
            false,
            false);

    private final UsersLogic usersLogic = UsersLogic.inst();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    UserProvision() {
        // prevent initialization
    }

    public static UserProvision inst() {
        return instance;
    }

    /**
     * Populates the authentication context of a user based on the request
     * information.
     */
    public AuthContext getAuthContextFromRequest(HttpServletRequest req) throws UnauthorizedAccessException {
        Account account = getAccountFromRequest(req);

        if (isBackdoorRequest(req)) {
            return ALL_ACCESS_AUTH_CONTEXT;
        } else if (isTrustedAutomatedCronOrWorkerRequest(req)) {
            return AUTOMATED_SERVICE_AUTH_CONTEXT;
        } else if (isLoggedInUser(account)) {
            assert account != null;
            return handleLoggedInAuthContext(req, account);
        } else if (isRegKeyRequest(req)) {
            return handleRegkeyUser(req);
        } else {
            return PUBLIC_AUTH_CONTEXT;
        }
    }

    /**
     * Gets the information of a user from an AuthContext.
     */
    public UserInfo getUserInfo(AuthContext authContext) {
        if (authContext == null || authContext.account() == null) {
            return null;
        }

        Account account = authContext.account();

        UserInfo userInfo = new UserInfo(account.getGoogleId(), account.getId());
        userInfo.isAdmin = authContext.isAdmin();
        userInfo.isInstructor = usersLogic.isInstructorInAnyCourse(account.getGoogleId());
        userInfo.isStudent = usersLogic.isStudentInAnyCourse(account.getGoogleId());
        userInfo.isMaintainer = authContext.isMaintainer();
        return userInfo;
    }

    protected boolean isBackdoorRequest(HttpServletRequest req) {
        return Config.BACKDOOR_KEY.equals(req.getHeader(Const.HeaderNames.BACKDOOR_KEY));
    }

    protected boolean isTrustedAutomatedCronOrWorkerRequest(HttpServletRequest req) {
        return AutomatedRequestAuth.isTrustedCronOrWorkerRequest(req);
    }

    protected boolean isMasqueradeRequest(HttpServletRequest req) {
        String userParam = req.getParameter(Const.ParamsNames.USER_ID);
        return userParam != null;
    }

    protected boolean isRegKeyRequest(HttpServletRequest req) {
        String regKey = req.getParameter(Const.ParamsNames.REGKEY);
        return regKey != null;
    }

    protected boolean isAdminUser(Account account) {
        return account != null
                && account.getEmail() != null
                && Config.getAppAdmins().contains(account.getEmail());
    }

    protected boolean isMaintainerUser(Account account) {
        return account != null
                && account.getEmail() != null
                && Config.getAppMaintainers().contains(account.getEmail());
    }

    protected boolean isLoggedInUser(Account account) {
        return account != null;
    }

    private Account getAccountFromRequest(HttpServletRequest req) {
        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        if (uic != null && uic.isValid()) {
            return accountsLogic.getAccount(uic.getAccountId());
        }
        return null;
    }

    /**
     * Handles the case where the request is from a logged in user (including
     * masquerade).
     * 
     * <p>
     * If the request is a masquerade request, returns an AuthContext with
     * MASQUERADE auth type and the account of the user being masqueraded as.
     *
     * Otherwise, returns an AuthContext with LOGGED_IN auth type and the account of
     * the logged in user.
     * 
     * @throws UnauthorizedAccessException if the request is an invalid attempt to
     *                                     masquerade
     */
    private AuthContext handleLoggedInAuthContext(HttpServletRequest req, Account account)
            throws UnauthorizedAccessException {
        AuthType authType = AuthType.LOGGED_IN;
        Account effectiveAccount = account;

        if (isMasqueradeRequest(req)) {
            authType = AuthType.MASQUERADE;
            if (!isAdminUser(account)) {
                throw new UnauthorizedAccessException(
                        String.format("Masquerade failed: user %s does not have admin privilege", account.getEmail()));
            }

            String userId = req.getParameter(Const.ParamsNames.USER_ID);
            effectiveAccount = accountsLogic.getAccountForGoogleId(userId);
            if (effectiveAccount == null) {
                throw new UnauthorizedAccessException(
                        String.format("Masquerade failed: no account found for user id %s", userId));
            }
        }

        return new AuthContext(
                authType,
                effectiveAccount,
                null,
                isAdminUser(effectiveAccount),
                isMaintainerUser(effectiveAccount));
    }

    /**
     * Handles the case where the request contains a registration key.
     * 
     * <p>
     * If the registration key is valid, returns an AuthContext with the associated
     * user and REG_KEY auth type.
     *
     * @throws UnauthorizedAccessException if there is no user associated with the
     *                                     registration key or if the associated
     *                                     user already has an account
     */

    private AuthContext handleRegkeyUser(HttpServletRequest req) throws UnauthorizedAccessException {
        String regKey = req.getParameter(Const.ParamsNames.REGKEY);
        User regKeyUser = usersLogic.getUserByRegistrationKey(regKey);

        if (regKeyUser == null) {
            throw new UnauthorizedAccessException("Invalid registration key: no user found for regkey");
        }

        if (regKeyUser.getAccount() != null) {
            throw new UnauthorizedAccessException("Login is required to access this resource");
        }

        return new AuthContext(
                AuthType.REG_KEY,
                null,
                regKeyUser,
                false,
                false);
    }
}

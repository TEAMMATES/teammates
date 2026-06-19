package teammates.logic.api;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.User;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.webapi.AuthType;

/**
 * Allows mocking of the {@link UserProvision} API used in production.
 *
 * <p>Instead of getting user information from the authentication service,
 * the API will return pre-determined information instead.
 */
public class MockUserProvision extends UserProvision {
    private static final AuthContext PUBLIC_AUTH_CONTEXT = new AuthContext(AuthType.PUBLIC, null, null, false, false);
    private static final AuthContext AUTOMATED_SERVICE_AUTH_CONTEXT =
            new AuthContext(AuthType.AUTOMATED_SERVICE, null, null, false, false);

    private Logic logic = Logic.inst();
    private Account loggedInAccount;
    private boolean isLoggedIn;
    private boolean loggedInUserIsAdmin;
    private boolean isAutomatedServiceMode;
    private boolean isMaintainer;
    private boolean isAdmin;

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    private AuthContext loginUser(Account account, boolean isAdmin, boolean isMaintainer) {
        this.isLoggedIn = true;
        this.loggedInAccount = account;
        this.loggedInUserIsAdmin = isAdmin;
        this.isAdmin = isAdmin;
        this.isMaintainer = isMaintainer;
        return createAccountAuthContext(AuthType.LOGGED_IN, account, isAdmin, isMaintainer);
    }

    /**
     * Login as a user without admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginUser(Account account) {
        return loginUser(account, false, false);
    }

    /**
     * Login as a user with admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsAdmin(Account account) {
        return loginUser(account, true, false);
    }

    /**
     * Login as a user with maintainer rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsMaintainer(Account account) {
        return loginUser(account, false, true);
    }

    /**
     * Logs in as an automated service (cron/worker).
     */
    public void loginAsAutomatedService() {
        isAutomatedServiceMode = true;
    }

    public boolean isAutomatedServiceMode() {
        return isAutomatedServiceMode;
    }

    /**
     * Removes the logged-in user information.
     */
    public void logoutUser() {
        isLoggedIn = false;
        loggedInUserIsAdmin = false;
        isAutomatedServiceMode = false;
        loggedInAccount = null;
    }

    @Override
    public AuthContext getAuthContextFromRequest(HttpServletRequest req) throws UnauthorizedAccessException {
        if (isAutomatedServiceMode) {
            return AUTOMATED_SERVICE_AUTH_CONTEXT;
        }

        if (!isLoggedIn) {
            String regKey = req.getParameter(Const.ParamsNames.REGKEY);
            if (regKey != null) {
                User regKeyUser = logic.getUserByRegistrationKey(regKey);
                return new AuthContext(AuthType.REG_KEY, null, regKeyUser, false, false);
            }
            return PUBLIC_AUTH_CONTEXT;
        }

        if (isMasqueradeRequest(req)) {
            if (!loggedInUserIsAdmin) {
                throw new UnauthorizedAccessException(
                        String.format("Masquerade failed: user %s does not have admin privilege",
                                loggedInAccount.getEmail()));
            }

            UUID masqueradeAccountUuid = getValidMasqueradeAccountId(req);
            Account masqueradeAccount = logic.getAccount(masqueradeAccountUuid);
            if (masqueradeAccount == null) {
                throw new UnauthorizedAccessException(
                        String.format("Masquerade failed: no account found for account id %s", masqueradeAccountUuid));
            }
            return new AuthContext(AuthType.MASQUERADE, masqueradeAccount, null, isAdmin, isMaintainer);
        }

        return createAccountAuthContext(AuthType.LOGGED_IN, loggedInAccount, isAdmin, isMaintainer);
    }

    @Override
    public UserInfo getUserInfo(AuthContext authContext) {
        if (authContext == null || authContext.account() == null) {
            return null;
        }

        Account account = authContext.account();
        UserInfo userInfo = new UserInfo(account.getId(), account.getEmail());
        userInfo.isAdmin = authContext.isAdmin();
        userInfo.isMaintainer = authContext.isMaintainer();
        return userInfo;
    }

    private AuthContext createAccountAuthContext(
            AuthType authType, Account account, boolean isAdmin, boolean isMaintainer) {
        return new AuthContext(authType, account, null, isAdmin, isMaintainer);
    }

}

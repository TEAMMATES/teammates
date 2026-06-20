package teammates.ui.servlets;

import static teammates.common.util.HttpResponseHelper.logAndPrintError;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.UrlHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;
import teammates.ui.exception.InvalidAuthStateException;
import teammates.ui.loginmethodhandlers.AuthResult;
import teammates.ui.loginmethodhandlers.AuthState;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthState state;
        try {
            state = getAuthStateFromCallback(req);
        } catch (InvalidAuthStateException e) {
            rejectLogin(req, resp, "Invalid authentication state: " + e.getMessage());
            return;
        }

        LoginMethod method = state.loginMethod();

        if (!Config.isSupportedLoginMethod(method)) {
            rejectLogin(req, resp, "Valid but unsupported login method: " + method);
            return;
        }

        LoginMethodHandler loginHandler = getLoginHandler(method);
        Cookie cookie;
        String logMessage;
        String nextUrl = UrlHelper.getSafeRedirectUrl(state.nextUrl());
        try {
            AuthResult authResult = loginHandler.handleCallback(req, state);
            cookie = getLoginCookie(authResult);
            logMessage = "Login successful";
        } catch (Exception e) {
            cookie = invalidateLogin(req);
            logMessage = "Login failed";
            nextUrl = UrlHelper.DEFAULT_REDIRECT_URL;
            logAndPrintError(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred during login: " + e.getMessage());
        }

        String redirectUrl = resp.encodeRedirectURL(nextUrl);
        log.info("Going to redirect to: " + redirectUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, logMessage);

        resp.addCookie(cookie);
        resp.sendRedirect(redirectUrl);
    }

    private Cookie getLoginCookie(AuthResult authResult) {
        try {
            HibernateUtil.beginTransaction();
            Account account = accountsLogic.createOrGetAccount(
                    authResult.provider(), authResult.subject(), authResult.tenantId(), authResult.email());
            HibernateUtil.commitTransaction();

            UserInfoCookie userInfoCookie = new UserInfoCookie(account.getId());
            return getLoginCookie(userInfoCookie);
        } catch (Exception e) {
            log.warning("Failed to create or get account for " + authResult.email(), e);
            HibernateUtil.rollbackTransaction();
            throw e;
        }
    }

    private void rejectLogin(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException {
        resp.addCookie(invalidateLogin(req));
        logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, message);
    }

    private Cookie invalidateLogin(HttpServletRequest req) {
        req.getSession().invalidate();
        return getLoginInvalidationCookie();
    }

    /**
     * Extracts and validates the encrypted state parameter from the OAuth2 callback.
     *
     * @return the decrypted AuthState object.
     * @throws InvalidAuthStateException if the state parameter is missing or invalid.
     */
    private AuthState getAuthStateFromCallback(HttpServletRequest req)
            throws IOException, InvalidAuthStateException {
        String encryptedState = req.getParameter("state");
        if (encryptedState == null) {
            throw new InvalidAuthStateException("Missing or invalid state parameter");
        }

        try {
            String decryptedState = StringHelper.decrypt(encryptedState);
            return JsonUtils.fromJson(decryptedState, AuthState.class);
        } catch (Exception e) {
            throw new InvalidAuthStateException("Failed to parse state parameter");
        }
    }
}

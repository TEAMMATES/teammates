package teammates.ui.servlets;

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
import teammates.ui.exception.AuthException;
import teammates.ui.loginmethodhandlers.AuthResult;
import teammates.ui.loginmethodhandlers.AuthState;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    private final AccountsLogic accountsLogic;

    public OAuth2CallbackServlet() {
        this(AccountsLogic.inst());
    }

    OAuth2CallbackServlet(AccountsLogic accountsLogic) {
        this.accountsLogic = accountsLogic;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleCallback(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleCallback(req, resp);
    }

    /**
     * Handles the main callback logic.
     */
    private void handleCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthState state;
        try {
            state = getValidAuthStateFromCallback(req);
        } catch (AuthException e) {
            log.warning("Invalid state parameter in OAuth2 callback", e);
            rejectLogin(req, resp, HttpStatus.SC_BAD_REQUEST);
            return;
        }

        LoginMethod method = state.loginMethod();

        if (!Config.isSupportedLoginMethod(method)) {
            rejectLogin(req, resp, HttpStatus.SC_BAD_REQUEST);
            return;
        }

        LoginMethodHandler loginHandler = getLoginHandler(method);
        Cookie cookie;
        try {
            AuthResult authResult = loginHandler.handleCallback(req, state);
            cookie = getLoginCookie(authResult);
        } catch (AuthException e) {
            log.warning("Failed to handle OAuth2 callback", e);
            rejectLogin(req, resp, HttpStatus.SC_BAD_REQUEST);
            return;
        } catch (Exception e) {
            log.severe("Unexpected error during OAuth2 callback", e);
            rejectLogin(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        String nextUrl = UrlHelper.getSafeRedirectUrl(state.nextUrl());
        String redirectUrl = resp.encodeRedirectURL(nextUrl);
        log.info("Going to redirect to: " + redirectUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Login successful");

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

    private void rejectLogin(HttpServletRequest req, HttpServletResponse resp, int status) throws IOException {
        req.getSession().invalidate();
        resp.addCookie(getLoginInvalidationCookie());
        resp.sendError(status);
    }

    /**
     * Extracts and validates the encrypted state parameter from the OAuth2 callback.
     *
     * @return the decrypted AuthState object.
     * @throws AuthException if the state parameter is missing or invalid.
     */
    private AuthState getValidAuthStateFromCallback(HttpServletRequest req)
            throws IOException, AuthException {
        String encryptedState = req.getParameter("state");
        if (encryptedState == null) {
            throw new AuthException("Missing or invalid state parameter");
        }

        AuthState state;
        try {
            String decryptedState = StringHelper.decrypt(encryptedState);
            state = JsonUtils.fromJson(decryptedState, AuthState.class);
        } catch (Exception e) {
            throw new AuthException("Failed to parse state parameter", e);
        }

        if (state == null || StringHelper.isEmpty(state.sessionId()) || state.loginMethod() == null) {
            throw new AuthException("Missing required state fields");
        }
        return state;
    }
}

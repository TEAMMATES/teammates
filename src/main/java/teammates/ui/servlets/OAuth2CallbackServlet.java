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
        AuthState state = getAuthStateFromCallback(req, resp);
        if (state == null) {
            return;
        }

        LoginMethodHandler loginHandler;
        if (Config.isDevServerLoginEnabled()) {
            loginHandler = getLoginHandler(LoginMethod.DEV_SERVER);
        } else {
            loginHandler = getLoginHandler(LoginMethod.GOOGLE);
        }

        AuthResult authResult = loginHandler.handleCallback(req, resp, state);
        if (authResult == null) {
            return;
        }

        Cookie cookie;
        String logMessage;
        if (authResult.isValid()) {
            try {
                HibernateUtil.beginTransaction();
                Account account = accountsLogic.createOrGetAccount(
                        authResult.getProvider(), authResult.getSubject(), authResult.getTenantId(), authResult.getEmail());
                HibernateUtil.commitTransaction();

                UserInfoCookie uic = new UserInfoCookie(account.getId());
                cookie = getLoginCookie(uic);
                logMessage = "Login successful";
            } catch (Exception e) {
                HibernateUtil.rollbackTransaction();
                log.warning("Failed to create or get account for " + authResult.getTenantId(), e);
                req.getSession().invalidate();

                cookie = getLoginInvalidationCookie();
                logMessage = "Login failed";
            }
        } else {
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
            logMessage = "Login failed";
        }

        String nextUrl = UrlHelper.getSafeRedirectUrl(state.getNextUrl());
        String redirectUrl = resp.encodeRedirectURL(nextUrl);
        log.info("Going to redirect to: " + redirectUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, logMessage);

        resp.addCookie(cookie);
        resp.sendRedirect(redirectUrl);
    }

    /**
     * Extracts and validates the encrypted state parameter from the OAuth2 callback.
     *
     * @return the decrypted AuthState object, or null if the state parameter is invalid.
     */
    private AuthState getAuthStateFromCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String encryptedState = req.getParameter("state");
        if (encryptedState == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Missing or invalid state parameter");
            return null;
        }

        try {
            String decryptedState = StringHelper.decrypt(encryptedState);
            return JsonUtils.fromJson(decryptedState, AuthState.class);
        } catch (Exception e) {
            if (Config.isDevServerLoginEnabled()) {
                try {
                    return JsonUtils.fromJson(encryptedState, AuthState.class);
                } catch (Exception devServerException) {
                    logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Failed to parse state parameter");
                    return null;
                }
            }

            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Failed to parse state parameter");
            return null;
        }
    }

}

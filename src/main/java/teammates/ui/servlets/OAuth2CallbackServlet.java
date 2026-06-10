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
        LoginMethod loginMethod = getLoginMethodFromCallback(req, resp);
        if (loginMethod == null) {
            return;
        }

        LoginMethodHandler handler = getLoginMethodHandler(loginMethod);
        if (handler == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Unsupported login method: " + loginMethod);
            return;
        }

        AuthResult authResult = handler.getAuthResult(req, resp);

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
                log.warning("Failed to create or get account for " + authResult.getEmail(), e);
                req.getSession().invalidate();

                cookie = getLoginInvalidationCookie();
                logMessage = "Login failed";
            }
        } else {
            req.getSession().invalidate();

            cookie = getLoginInvalidationCookie();
            logMessage = "Login failed";
        }

        String redirectUrl = resp.encodeRedirectURL(UrlHelper.getSanitizedRedirectUrl(authResult.getNextUrl()));
        log.info("Going to redirect to: " + redirectUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, logMessage);

        resp.addCookie(cookie);
        resp.sendRedirect(redirectUrl);
    }

    /**
     * Extracts and validates the login method from the HTTP servlet request.
     *
     * @return the login method, or null if it is invalid or not supported.
     */
    private LoginMethod getLoginMethodFromCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String encyptedState = req.getParameter("state");
        if (encyptedState == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Missing or invalid state parameter");
            return null;
        }

        AuthState state;
        try {
            String decryptedState = StringHelper.decrypt(encyptedState);
            state = JsonUtils.fromJson(decryptedState, AuthState.class);
        } catch (Exception e) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Failed to parse state parameter");
            return null;
        }

        LoginMethod loginMethod;
        try {
            loginMethod = state.getMethod();
        } catch (IllegalArgumentException e) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid login method: " + state.getMethod());
            return null;
        }

        if (!Config.isSupportedLoginMethod(loginMethod)) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST,
                    "Valid but unsupported login method: " + state.getMethod());
            return null;
        }
        return loginMethod;
    }

}

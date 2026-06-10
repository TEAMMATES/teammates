package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.UrlHelper;
import teammates.logic.core.AccountsLogic;
import teammates.ui.loginmethodhandlers.LoginMethodHandler;
import teammates.ui.output.LoginMethod;

/**
 * Servlet that handles login.
 */
public class LoginServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    private static AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }

        nextUrl = UrlHelper.getSanitizedRedirectUrl(nextUrl);

        if (!isLoginNeeded(req)) {
            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to next URL");
            String redirectUrl = resp.encodeRedirectURL(nextUrl);
            resp.sendRedirect(redirectUrl);
            return;
        }

        LoginMethod loginMethod = getLoginMethodFromLoginRequest(req, resp);
        if (loginMethod == null) {
            return;
        }

        LoginMethodHandler handler = getLoginMethodHandler(loginMethod);
        if (handler == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Unsupported login method: " + loginMethod);
            return;
        }
        handler.handleLogin(req, resp, nextUrl);
    }

    private boolean isLoginNeeded(HttpServletRequest req) {
        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        if (uic == null || !uic.isValid()) {
            return true;
        }

        try {
            HibernateUtil.beginTransaction();
            boolean isAccountInvalid = accountsLogic.getAccount(uic.getAccountId()) == null;
            HibernateUtil.commitTransaction();
            return isAccountInvalid;
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            log.warning("Failed to verify account from cookie", e);
            return true;
        }
    }

    private LoginMethod getLoginMethodFromLoginRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String method = req.getParameter("method");
        if (method == null) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Missing login method");
            return null;
        }

        LoginMethod loginMethod;
        try {
            loginMethod = LoginMethod.fromString(method);
        } catch (IllegalArgumentException e) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Invalid login method: " + method);
            return null;
        }

        if (!Config.isSupportedLoginMethod(loginMethod)) {
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Valid but unsupported login method: " + method);
            return null;
        }
        return loginMethod;
    }

}

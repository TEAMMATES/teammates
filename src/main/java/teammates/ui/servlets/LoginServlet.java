package teammates.ui.servlets;

import static teammates.common.util.HttpResponseHelper.logAndPrintError;

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
        String nextUrl = UrlHelper.getSafeRedirectUrl(req.getParameter("nextUrl"));

        if (!isLoginNeeded(req)) {
            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to next URL");
            String redirectUrl = resp.encodeRedirectURL(nextUrl);
            resp.sendRedirect(redirectUrl);
            return;
        }

        LoginMethodHandler loginHandler;
        if (Config.isDevServerLoginEnabled()) {
            loginHandler = getLoginHandler(LoginMethod.DEV_SERVER);
        } else {
            loginHandler = getLoginHandler(LoginMethod.GOOGLE);
        }

        try {
            String redirectUrl = loginHandler.handleLogin(req, nextUrl);
            redirectUrl = resp.encodeRedirectURL(redirectUrl);
            resp.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logAndPrintError(req, resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred during login: " + e.getMessage());
        }
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

}

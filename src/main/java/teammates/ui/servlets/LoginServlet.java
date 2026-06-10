package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
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

        nextUrl = getSanitizedRedirectUrl(nextUrl);

        if (!isLoginNeeded(req)) {
            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to next URL");
            String redirectUrl = resp.encodeRedirectURL(nextUrl);
            resp.sendRedirect(redirectUrl);
            return;
        }

        LoginMethod loginMethod = getLoginMethodFromRequest(req, resp);
        if (loginMethod == null) {
            return;
        }

        switch (loginMethod) {
        case DEV_SERVER:
            if (!Config.isDevServerLoginEnabled()) {
                resp.sendError(HttpStatus.SC_FORBIDDEN);
                return;
            }
            handleDevServerLogin(req, resp, nextUrl);
            break;
        case GOOGLE:
            handleGoogleLogin(req, resp, nextUrl);
            break;
        default:
            // Should not reach here.
            logAndPrintError(req, resp, HttpStatus.SC_BAD_REQUEST, "Unexpected error with login method: " + loginMethod);
        }
    }

    private void handleGoogleLogin(HttpServletRequest req, HttpServletResponse resp, String nextUrl) throws IOException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.GOOGLE);
        GoogleAuthorizationCodeRequestUrl authorizationUrl = getGoogleAuthorizationFlow().newAuthorizationUrl();
        authorizationUrl.setRedirectUri(getRedirectUri(req));
        authorizationUrl.setState(StringHelper.encrypt(JsonUtils.toCompactJson(state)));

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Google sign-in page");

        resp.sendRedirect(authorizationUrl.build());
    }

    private void handleDevServerLogin(HttpServletRequest req, HttpServletResponse resp, String nextUrl) throws IOException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.DEV_SERVER);
        String redirectUrl = resp.encodeRedirectURL("/devServerLogin?state="
                + getEncodedQueryParam(JsonUtils.toCompactJson(state)));
        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to dev server login page");

        resp.sendRedirect(redirectUrl);
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

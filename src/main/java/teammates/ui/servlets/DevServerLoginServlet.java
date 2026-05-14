package teammates.ui.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.HibernateUtil;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Account;

/**
 * Servlet that handles dev server login.
 */
public class DevServerLoginServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();
    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        if (!Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", Const.WebPageURIs.LOGIN + "?nextUrl=" + nextUrl.replace("&", "%26"));
            return;
        }

        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        boolean isLoginNeeded = uic == null || !uic.isValid();
        if (!isLoginNeeded) {
            resp.sendRedirect(nextUrl);
            return;
        }

        String html = FileHelper.readResourceFile("devServerLoginPage.html");
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.print(html);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
            return;
        }

        String email = req.getParameter("email");
        if (email == null) {
            return;
        }

        // TODO: Redirect to OAuth2CallbackServlet for account creation,
        // HibernateUtil shouldn't be used here
        Account account;
        try {
            HibernateUtil.beginTransaction();
            account = accountsLogic.createOrGetAccountForEmail(email);
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            log.severe("Error occurred while logging into dev server with email: " + email, e);
            return;
        }

        UserInfoCookie uic = new UserInfoCookie(email, account.getId());
        Cookie cookie = getLoginCookie(uic);
        resp.addCookie(cookie);

        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        resp.sendRedirect(nextUrl);
    }

}

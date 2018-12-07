package teammates.ui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * Servlet to handle Logout.
 */
@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter(Const.ParamsNames.NEXT_URL);
        if (nextUrl == null) {
            nextUrl = "/";
        }
        String expectedId = req.getParameter(Const.ParamsNames.HINT);
        String actualId = req.getParameter(Const.ParamsNames.USER_ID);
        String logoutUrl = new GateKeeper().getLogoutUrl(nextUrl);
        if (expectedId == null || actualId == null) {
            resp.sendRedirect(logoutUrl);
            return;
        }
        try {
            req.setAttribute(Const.ParamsNames.HINT, StringHelper.decrypt(expectedId));
            req.setAttribute(Const.ParamsNames.USER_ID, StringHelper.decrypt(actualId));
            req.setAttribute(Const.ParamsNames.NEXT_URL, logoutUrl);
            req.getRequestDispatcher(Const.ViewURIs.GOOGLE_ACCOUNT_HINT).forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(logoutUrl);
        }
    }

}

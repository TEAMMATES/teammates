package teammates.ui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Servlet to handle Login
 */
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

    @Override
    public final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    public final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final GateKeeper gateKeeper = new GateKeeper();
        final UserType user = gateKeeper.getCurrentUser();

        final boolean isInstructor = req.getParameter(Const.ParamsNames.LOGIN_INSTRUCTOR) != null;
        final boolean isStudent = req.getParameter(Const.ParamsNames.LOGIN_STUDENT) != null;
        final boolean isAdmin = req.getParameter(Const.ParamsNames.LOGIN_ADMIN) != null;

        if (isInstructor) {
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
            } else {
                resp.sendRedirect(gateKeeper.getLoginUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
            }
        } else if (isStudent) {
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.STUDENT_HOME_PAGE);
            } else {
                resp.sendRedirect(gateKeeper.getLoginUrl(Const.ActionURIs.STUDENT_HOME_PAGE));
            }
        } else if (isAdmin) { // TODO: do we need this branch?
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.ADMIN_HOME_PAGE);
            } else {
                resp.sendRedirect(gateKeeper.getLoginUrl(Const.ActionURIs.ADMIN_HOME_PAGE));
            }
        } else {
            resp.sendRedirect(Const.ViewURIs.ERROR_PAGE);
        }
    }

    private boolean isMasqueradeMode(final UserType user) {
        return user != null;
    }
}

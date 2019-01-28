package teammates.ui.webapi.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Maps some selected legacy URLs to new one. This is primarily for URLs send via email.
 *
 * <p>This class does not check for parameter validity.
 */
@SuppressWarnings("serial")
public class LegacyUrlMapper extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        String redirectUrl;
        String key;

        switch (uri) {
        case Const.LegacyURIs.INSTRUCTOR_COURSE_JOIN:
            key = req.getParameter(Const.ParamsNames.REGKEY);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_COURSE_JOIN:
        case Const.LegacyURIs.STUDENT_COURSE_JOIN_NEW:
            key = req.getParameter(Const.ParamsNames.REGKEY);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE:
        case Const.LegacyURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE:
        case Const.LegacyURIs.STUDENT_FEEDBACK_RESULTS_PAGE:
        case Const.LegacyURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE:
            // TODO
            redirectUrl = "/";
            break;
        default:
            redirectUrl = "/";
            break;
        }

        resp.sendRedirect(redirectUrl);
    }

}

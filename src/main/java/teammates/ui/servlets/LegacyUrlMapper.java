package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Maps some selected legacy URLs to new one. This is primarily for URLs send via email.
 *
 * <p>This class does not check for parameter validity.
 */
@Deprecated
public class LegacyUrlMapper extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        String baseRedirectUrl;
        String redirectUrl;
        String key;
        String courseId;
        String fsName;

        switch (uri) {
        case Const.LegacyURIs.INSTRUCTOR_COURSE_JOIN:
            baseRedirectUrl = Const.WebPageURIs.JOIN_PAGE;
            key = req.getParameter(Const.ParamsNames.REGKEY);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withEntityType(Const.EntityType.INSTRUCTOR)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_COURSE_JOIN:
        case Const.LegacyURIs.STUDENT_COURSE_JOIN_NEW:
            baseRedirectUrl = Const.WebPageURIs.JOIN_PAGE;
            key = req.getParameter(Const.ParamsNames.REGKEY);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withEntityType(Const.EntityType.STUDENT)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_HOME_PAGE:
            baseRedirectUrl = Const.WebPageURIs.STUDENT_HOME_PAGE;
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                    .toString();
            break;
        case Const.LegacyURIs.INSTRUCTOR_HOME_PAGE:
            baseRedirectUrl = Const.WebPageURIs.INSTRUCTOR_HOME_PAGE;
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE:
            baseRedirectUrl = Const.WebPageURIs.SESSION_SUBMISSION_PAGE;
            key = req.getParameter(Const.ParamsNames.REGKEY);
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                    .withRegistrationKey(key)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        case Const.LegacyURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE:
            baseRedirectUrl = Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE;
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_FEEDBACK_RESULTS_PAGE:
            baseRedirectUrl = Const.WebPageURIs.SESSION_RESULTS_PAGE;
            key = req.getParameter(Const.ParamsNames.REGKEY);
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                    .withRegistrationKey(key)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        case Const.LegacyURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE:
            baseRedirectUrl = Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE;
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        default:
            baseRedirectUrl = "/";
            redirectUrl = "/";
            log.warning("Unmapped legacy URL: " + uri);
            break;
        }

        log.request(req, HttpStatus.SC_MOVED_PERMANENTLY,
                "Redirect legacy URL from " + uri + " to " + baseRedirectUrl);

        resp.sendRedirect(redirectUrl);
    }

}

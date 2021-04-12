package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Maps some selected legacy URLs to new one. This is primarily for URLs send via email.
 *
 * <p>This class does not check for parameter validity.
 */
@Deprecated
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
        String courseId;
        String fsName;

        switch (uri) {
        case Const.LegacyURIs.INSTRUCTOR_COURSE_JOIN:
            key = req.getParameter(Const.ParamsNames.REGKEY);
            String institute = req.getParameter(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
            AppUrl newUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withEntityType(Const.EntityType.INSTRUCTOR);
            if (institute != null) {
                newUrl = newUrl
                        .withInstructorInstitution(institute)
                        .withInstitutionMac(StringHelper.generateSignature(institute));
            }
            redirectUrl = newUrl.toString();
            break;
        case Const.LegacyURIs.STUDENT_COURSE_JOIN:
        case Const.LegacyURIs.STUDENT_COURSE_JOIN_NEW:
            key = req.getParameter(Const.ParamsNames.REGKEY);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(key)
                    .withEntityType(Const.EntityType.STUDENT)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_HOME_PAGE:
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                    .toString();
            break;
        case Const.LegacyURIs.INSTRUCTOR_HOME_PAGE:
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE:
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
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        case Const.LegacyURIs.STUDENT_FEEDBACK_RESULTS_PAGE:
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
            courseId = req.getParameter(Const.ParamsNames.COURSE_ID);
            fsName = req.getParameter(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                    .withCourseId(courseId)
                    .withSessionName(fsName)
                    .toString();
            break;
        default:
            redirectUrl = "/";
            break;
        }

        resp.sendRedirect(redirectUrl);
    }

}

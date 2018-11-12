package teammates.ui.newcontroller;

import javax.servlet.http.Cookie;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;

/**
 * Action: gets user authentication information.
 *
 * <p>This does not log in or log out the user.
 */
public class GetAuthInfoAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Login information is available to everyone
    }

    @Override
    public ActionResult execute() {
        String frontendUrl = getRequestParamValue("frontendUrl");
        if (frontendUrl == null) {
            frontendUrl = "";
        }

        AuthInfo output;
        if (userInfo == null) {
            output = new AuthInfo(
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.STUDENT_HOME_PAGE),
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.INSTRUCTOR_HOME_PAGE),
                    gateKeeper.getLoginUrl(frontendUrl + Const.WebPageURIs.ADMIN_HOME_PAGE)
            );
        } else {
            output = new AuthInfo(userInfo, authType == AuthType.MASQUERADE,
                    gateKeeper.getLogoutUrl(frontendUrl + "/web"));
        }

        String csrfToken = StringHelper.encrypt(req.getSession().getId());
        String existingCsrfToken = HttpRequestHelper.getCookieValueFromRequest(req, Const.CsrfConfig.TOKEN_COOKIE_NAME);
        if (!csrfToken.equals(existingCsrfToken)) {
            Cookie csrfTokenCookie = new Cookie(Const.CsrfConfig.TOKEN_COOKIE_NAME, csrfToken);
            csrfTokenCookie.setSecure(!Config.isDevServer());
            csrfTokenCookie.setPath("/");
            resp.addCookie(csrfTokenCookie);
        }

        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetAuthInfoAction}.
     */
    public static class AuthInfo extends ActionResult.ActionOutput {

        private final String studentLoginUrl;
        private final String instructorLoginUrl;
        private final String adminLoginUrl;
        private final UserInfo user;
        private final boolean masquerade;
        private final String logoutUrl;

        public AuthInfo(String studentLoginUrl, String instructorLoginUrl, String adminLoginUrl) {
            this.studentLoginUrl = studentLoginUrl;
            this.instructorLoginUrl = instructorLoginUrl;
            this.adminLoginUrl = adminLoginUrl;
            this.user = null;
            this.masquerade = false;
            this.logoutUrl = null;
        }

        public AuthInfo(UserInfo user, boolean masquerade, String logoutUrl) {
            this.studentLoginUrl = null;
            this.instructorLoginUrl = null;
            this.adminLoginUrl = null;
            this.user = user;
            this.masquerade = masquerade;
            this.logoutUrl = logoutUrl;
        }

        public String getStudentLoginUrl() {
            return studentLoginUrl;
        }

        public String getInstructorLoginUrl() {
            return instructorLoginUrl;
        }

        public String getAdminLoginUrl() {
            return adminLoginUrl;
        }

        public UserInfo getUser() {
            return user;
        }

        public boolean isMasquerade() {
            return masquerade;
        }

        public String getLogoutUrl() {
            return logoutUrl;
        }

    }

}

package teammates.ui.webapi.action;

import javax.servlet.http.Cookie;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.AuthInfo;

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
            String googleId = userInfo.getId();
            AccountAttributes accountInfo = logic.getAccount(googleId);
            String institute = null;
            if (accountInfo != null) {
                institute = accountInfo.getInstitute();
            }
            output = new AuthInfo(userInfo, institute, authType == AuthType.MASQUERADE);
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

}

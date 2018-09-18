package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Config;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: gets user authentication information.
 *
 * <p>This does not log in or log out the user.
 */
public class AuthInfoAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.UNAUTHENTICATED;
    }

    @Override
    public boolean checkSpecificAccessControl() {
        // Login information is available to everyone
        return true;
    }

    @Override
    public ActionResult execute(HttpServletResponse resp) {
        GateKeeper gateKeeper = new GateKeeper();
        UserType user = gateKeeper.getCurrentUser();
        String frontendUrl = req.getParameter("frontendUrl");

        Map<String, Object> output = new HashMap<>();
        if (user == null) {
            output.put("studentLoginUrl", gateKeeper.getLoginUrl(frontendUrl + "/web/student"));
            output.put("instructorLoginUrl", gateKeeper.getLoginUrl(frontendUrl + "/web/instructor"));
            output.put("adminLoginUrl", gateKeeper.getLoginUrl(frontendUrl + "/web/admin"));
        } else {
            output.put("user", user);
            output.put("logoutUrl", gateKeeper.getLogoutUrl(frontendUrl + "/web"));
        }

        String xsrfToken = StringHelper.encrypt(req.getSession().getId());
        String existingXsrfToken = req.getHeader("X-XSRF-TOKEN");
        if (!xsrfToken.equals(existingXsrfToken)) {
            Cookie xsrfTokenCookie = new Cookie("XSRF-TOKEN", xsrfToken);
            xsrfTokenCookie.setSecure(!Config.isDevServer());
            xsrfTokenCookie.setPath("/");
            resp.addCookie(xsrfTokenCookie);
        }

        return new JsonResult(output);
    }

}

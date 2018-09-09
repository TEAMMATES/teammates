package teammates.ui.newcontroller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.JsonUtils;
import teammates.logic.api.GateKeeper;

/**
 * Gets user's authentication information.
 */
@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet {

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> output = new HashMap<>();
        try {
            GateKeeper gateKeeper = new GateKeeper();
            UserType user = gateKeeper.getCurrentUser();
            String frontendUrl = req.getParameter("frontendUrl");

            if (user == null) {
                output.put("studentLoginUrl", gateKeeper.getLoginUrl(frontendUrl + "/web/student"));
                output.put("instructorLoginUrl", gateKeeper.getLoginUrl(frontendUrl + "/web/instructor"));
            } else {
                output.put("user", user);
                output.put("logoutUrl", gateKeeper.getLogoutUrl(frontendUrl));
            }
        } catch (Throwable t) {
            resp.setStatus(500);
            output.put("message", t.getMessage());
        }
        resp.getWriter().print(JsonUtils.toJson(output));
    }

}

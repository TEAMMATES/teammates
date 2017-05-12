package teammates.ui.automated;

import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.TeammatesException;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.Logger;

/**
 * Receives automated requests from the App Engine server and executes the matching automated action.
 */
@SuppressWarnings("serial")
public class AutomatedServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            AutomatedAction action = new AutomatedActionFactory().getAction(req, resp);

            String url = HttpRequestHelper.getRequestedUrl(req);
            // Do not log task queue worker actions to prevent excessive logging
            if (!url.startsWith("/worker/")) {
                Map<String, String[]> params = HttpRequestHelper.getParameterMap(req);
                // no logged-in user for automated servlet
                LogMessageGenerator logGenerator = new LogMessageGenerator();
                log.info(logGenerator.generateBasicActivityLogMessage(url, params, action.getActionMessage(), null));
            }

            action.execute();
        } catch (Exception e) {
            String requestUrl = req.getRequestURL().toString();
            String requestParams = HttpRequestHelper.printRequestParameters(req);
            log.severe("Exception occured while performing " + requestUrl + "|||"
                       + requestParams + "|||" + TeammatesException.toStringWithStackTrace(e));
        }
    }

}

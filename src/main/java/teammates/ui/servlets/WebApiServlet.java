package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.ActionResult;

/**
 * Servlet that handles all requests from the web application.
 */
public class WebApiServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    private void invokeServlet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int statusCode = 0;
        Action action = null;

        try {
            action = ActionFactory.getAction(req, req.getMethod());
            ActionResult result = executeWithTransaction(action, req);

            statusCode = result.getStatusCode();
            result.send(resp);
        } catch (Throwable t) {
            statusCode = WebApiServletExceptionHandler.handleException(resp, t);
        } finally {
            RequestLogUser userInfo = new RequestLogUser();
            String requestBody = null;
            String actionClass = null;
            if (action != null) {
                if (action.hasDefinedRequestBody()) {
                    requestBody = action.getRequestBody();
                }
                actionClass = action.getClass().getSimpleName();
                userInfo = action.getUserInfoForLogging();
            }

            log.request(req, statusCode, actionClass, userInfo, requestBody, actionClass);
        }
    }

    private ActionResult executeWithTransaction(Action action, HttpServletRequest req)
            throws InvalidOperationException, InvalidHttpRequestBodyException, UnauthorizedAccessException {
        try {
            HibernateUtil.beginTransaction();
            action.init(req);
            action.checkAccessControl();

            ActionResult result = action.execute();
            HibernateUtil.commitTransaction();
            return result;
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            throw e;
        }
    }

}

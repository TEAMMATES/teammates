package teammates.ui.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.ui.pagedata.PageData;

/**
 * A result that shows a page in the Browser. These are usually implemented as
 * JSP pages.
 */
public class ShowPageResult extends ActionResult {

    /** The data that will be used to render the page. */
    public PageData data;

    public ShowPageResult(
            String destination,
            AccountAttributes account,
            List<StatusMessage> status) {
        super(destination, account, status);
    }

    public ShowPageResult(
            String destination,
            AccountAttributes account,
            PageData data,
            List<StatusMessage> status) {
        super(destination, account, status);
        this.data = data;
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        addStatusMessagesToPageData(req);
        req.setAttribute("data", data);

        /* These two are required for the 'status message' section of the page
         * Although these two are also sent as parameters in the URL,
         *  they should be set as attributes too, because the status message
         *  section is a {@code jsp:include} and cannot see parameters encoded
         *  in the URL
         */
        req.setAttribute(Const.ParamsNames.ERROR, Boolean.toString(isError));

        req.getRequestDispatcher(getDestinationWithParams()).forward(req, resp);
    }

    /**
     * Adds the list of status messages (if any) to the page data.
     * @param req HttpServletRequest object
     */
    private void addStatusMessagesToPageData(HttpServletRequest req) {
        @SuppressWarnings("unchecked")
        List<StatusMessage> statusMessagesToUser =
                (List<StatusMessage>) req.getSession().getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);

        // If the list of status messages can be found in the session and it is not empty,
        // means there are status messages to be shown to the user, add them to the page data.
        if (statusMessagesToUser != null && !statusMessagesToUser.isEmpty()) {
            req.getSession().removeAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);
            data.setStatusMessagesToUser(statusMessagesToUser);
        }
    }
}

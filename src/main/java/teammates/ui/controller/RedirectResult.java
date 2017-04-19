package teammates.ui.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.StatusMessage;
import teammates.ui.pagedata.PageData;

/** A 'redirect' type result. That is, the Browser will be required to make
 * another request to the specified {@code destination}.
 */
public class RedirectResult extends ActionResult {

    /** The data that will be used to render the page. */
    public PageData data;

    public RedirectResult(
            String destination,
            AccountAttributes account,
            List<StatusMessage> status) {
        super(destination, account, status);
    }

    public RedirectResult(
            String destination,
            AccountAttributes account,
            PageData data,
            List<StatusMessage> status) {
        super(destination, account, status);
        this.data = data;
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(getDestinationWithParams());
    }

}

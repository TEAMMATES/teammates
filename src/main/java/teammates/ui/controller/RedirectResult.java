package teammates.ui.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.StatusMessage;

/** A 'redirect' type result. That is, the Browser will be required to make
 * another request to the specified {@code destination}.
 */
public class RedirectResult extends ActionResult {

    public RedirectResult(
            String destination,
            AccountAttributes account,
            List<StatusMessage> status) {
        super(destination, account, status);
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(getDestinationWithParams());
    }

}

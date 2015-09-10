package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.StatusMessage;
import teammates.common.util.Utils;

/** A 'redirect' type result. That is, the Browser will be required to make 
 * another request to the specified {@code destination}.
 */
public class RedirectResult extends ActionResult {
    static Logger log = Utils.getLogger();
    
    public RedirectResult(
            String destination, 
            AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            List<StatusMessage> status) {
        super(destination, account, parametersFromPreviousRequest, status);
    }


    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(getDestinationWithParams());
    }

}

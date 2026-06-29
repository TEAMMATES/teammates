package teammates.ui.webapi;

import teammates.common.datatransfer.SessionKeyAccessResult;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.SessionKeyAccessData;
import teammates.ui.request.SessionKeyAccessRequest;

/**
 * Preflight access check for student session links.
 */
public class SessionKeyAccessAction extends PublicAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        SessionKeyAccessRequest requestBody = getAndValidateRequestBody(SessionKeyAccessRequest.class);
        SessionKeyAccessResult result = logic.getSessionKeyAccessResult(requestContext.getAccount(),
                requestBody.getKey());
        return new JsonResult(new SessionKeyAccessData(result.decision(), result.message()));
    }
}

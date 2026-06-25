package teammates.ui.webapi;

import teammates.common.datatransfer.SessionKeyAccessResult;
import teammates.ui.output.SessionKeyAccessData;

/**
 * Preflight access check for student session links.
 */
public class GetSessionKeyAccessAction extends PublicAction {

    @Override
    public JsonResult execute() {
        SessionKeyAccessResult result = logic.getSessionKeyAccessResult(req);
        return new JsonResult(new SessionKeyAccessData(result.decision(), result.message()));
    }
}

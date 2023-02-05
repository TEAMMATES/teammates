package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.SupportRequestData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: Gets a support request by ID.
 */
public class GetSupportRequestAction extends AdminOnlyAction {
    
    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String id = getNonNullRequestParamValue(Const.ParamsNames.SUPPORT_REQUEST_ID);

        SupportRequestAttributes supportRequest = logic.getSupportRequest(id);

        if (supportRequest == null) {
            throw new EntityNotFoundException("Support request does not exist.");
        }

        return new JsonResult(new SupportRequestData(supportRequest));
    }
}

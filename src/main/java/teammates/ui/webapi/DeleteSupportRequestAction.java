package teammates.ui.webapi;

import teammates.common.util.Const;

public class DeleteSupportRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String id = getNonNullRequestParamValue(Const.ParamsNames.SUPPORT_REQUEST_ID);
        logic.deleteSupportRequest(id);
        return new JsonResult("Support request has been delete.");
    }
}

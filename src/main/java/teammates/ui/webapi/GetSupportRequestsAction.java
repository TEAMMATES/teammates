package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.ui.output.SupportRequestsData;

public class GetSupportRequestsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        List<SupportRequestAttributes> supportRequests = logic.getAllSupportRequests();
        return new JsonResult(new SupportRequestsData(supportRequests));
    }
}

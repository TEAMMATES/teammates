package teammates.ui.webapi;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.util.JsonUtils;

import java.util.List;

public class QueryErrorLogsAction extends AdminOnlyAction{
    @Override
    ActionResult execute() {
        List<ErrorLogEntry> errorLogs = logsProcessor.getErrorLogs(24);
        return new JsonResult(JsonUtils.toJson(errorLogs));
    }
}

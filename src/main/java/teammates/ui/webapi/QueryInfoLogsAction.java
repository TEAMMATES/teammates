package teammates.ui.webapi;

import com.google.cloud.logging.LogEntry;
import teammates.common.util.JsonUtils;

import java.util.List;

public class QueryInfoLogsAction extends AdminOnlyAction{
    @Override
    ActionResult execute() {
        List<LogEntry> infoLogs = logsProcessor.getInfoLogs();
        return new JsonResult(JsonUtils.toJson(infoLogs));
    }
}

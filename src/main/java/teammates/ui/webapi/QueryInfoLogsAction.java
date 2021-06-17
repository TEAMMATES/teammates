package teammates.ui.webapi;

import java.util.List;

import com.google.cloud.logging.LogEntry;

import teammates.common.util.JsonUtils;

/**
 * Gets the list of info logs for users.
 */
public class QueryInfoLogsAction extends AdminOnlyAction {
    @Override
    ActionResult execute() {
        List<LogEntry> infoLogs = logsProcessor.getInfoLogs();
        return new JsonResult(JsonUtils.toJson(infoLogs));
    }
}

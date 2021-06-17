package teammates.ui.webapi;

import java.util.List;

import com.google.cloud.logging.LogEntry;

import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Gets the list of info logs for users.
 */
public class QueryInfoLogsAction extends AdminOnlyAction {
    @Override
    ActionResult execute() {
        List<LogEntry> infoLogs = logsProcessor.getInfoLogs();
        Logger log = Logger.getLogger();
        log.info(JsonUtils.toJson(infoLogs));
        return new JsonResult(JsonUtils.toJson(infoLogs));
    }
}

package teammates.ui.webapi;

import java.util.List;

import com.google.cloud.logging.LogEntry;
import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Gets the list of error logs for a user-specified period of time.
 */
public class QueryErrorLogsAction extends AdminOnlyAction {
    @Override
    ActionResult execute() {
        List<LogEntry> errorLogs = logsProcessor.getErrorLogs(24);
        Logger log = Logger.getLogger();
        System.out.println(JsonUtils.toJson(errorLogs));
        return new JsonResult(JsonUtils.toJson(errorLogs));
    }
}

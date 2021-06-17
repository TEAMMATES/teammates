package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Gets the list of error logs for a user-specified period of time.
 */
public class QueryErrorLogsAction extends AdminOnlyAction {
    @Override
    ActionResult execute() {
        List<ErrorLogEntry> errorLogs = logsProcessor.getErrorLogs(24);
        Logger log = Logger.getLogger();
        log.info(JsonUtils.toJson(errorLogs));
        return new JsonResult(JsonUtils.toJson(errorLogs));
    }
}

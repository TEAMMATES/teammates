package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;

/**
 * Action: Sync feedback session logs from GCloud logging service.
 */
public class FeedbackSessionLogsUpdateAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public ActionResult execute() {
        long latestLogTimestamp = logic.getLatestLogTimestamp();

        List<FeedbackSessionLogEntryAttributes> logEntries =
                logsProcessor.getFeedbackSessionLogs(null, null, latestLogTimestamp, Long.MAX_VALUE, null);

        try {
            logic.createFeedbackSessionLogs(logEntries);
        } catch (InvalidParametersException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }

}

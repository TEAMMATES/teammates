package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.exception.InvalidParametersException;

import teammates.storage.entity.FeedbackSessionLogEntry;

public class FeedbackSessionLogsUpdateAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        long latestLogTimestamp = logic.getLatestLogTimestamp();

        List<FeedbackSessionLogEntry> logEntries =
                logsProcessor.getFeedbackSessionLogs(null, null, latestLogTimestamp, Long.MAX_VALUE, null);

        try {
            List<FeedbackSessionLogEntryAttributes> createdEntries =
                    logic.createFeedbackSessionLogs(logEntries.stream()
                            .map(FeedbackSessionLogEntryAttributes::valueOf)
                            .collect(Collectors.toList()));
            System.out.println(createdEntries);
        } catch (InvalidParametersException e) {
            e.printStackTrace();
        }

        return new JsonResult("Successful");
    }

}

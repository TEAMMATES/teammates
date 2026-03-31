package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;

/**
 * Deletes stale feedback session activity logs from the database.
 */
public class CleanupFeedbackSessionLogsAction extends AdminOnlyAction {

    static final Duration RETENTION_PERIOD = Duration.ofDays(90);

    @Override
    public JsonResult execute() {
        Instant cutoffTime = Instant.now().minus(RETENTION_PERIOD);
        sqlLogic.deleteFeedbackSessionLogsOlderThan(cutoffTime);
        return new JsonResult("Successful");
    }
}

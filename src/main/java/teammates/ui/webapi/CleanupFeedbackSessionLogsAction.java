package teammates.ui.webapi;

import java.time.Clock;
import java.time.Instant;

import teammates.common.util.Const;

/**
 * Deletes stale feedback session activity logs from the database.
 */
public class CleanupFeedbackSessionLogsAction extends AdminOnlyAction {

    private Clock clock = Clock.systemUTC();

    @Override
    public JsonResult execute() {
        Instant cutoffTime = Instant.now(clock).minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD);
        logic.deleteFeedbackSessionLogsOlderThan(cutoffTime);
        return new JsonResult("Successful");
    }
}

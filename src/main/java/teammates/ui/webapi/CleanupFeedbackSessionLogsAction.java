package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.util.Const;

/**
 * Deletes stale feedback session activity logs from the database.
 */
public class CleanupFeedbackSessionLogsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        Instant cutoffTime = Instant.now().minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD);
        sqlLogic.deleteFeedbackSessionLogsOlderThan(cutoffTime);
        return new JsonResult("Successful");
    }
}

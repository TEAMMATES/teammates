package teammates.ui.webapi;

import teammates.common.util.Logger;

/**
 * Cron job: record the total number of responses with timestamp.
 */
public class RecordResponseCountAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    ActionResult execute() {
        try {
            logic.createFeedbackResponseRecord(
                    logic.getTotalFeedBackResponseCount(), (int) (System.currentTimeMillis() / 1000));
        } catch (Exception e) {
            log.warning("record feedback response failed " + e.getMessage());

            return new JsonResult("Failed");
        }

        return new JsonResult("Successful");
    }
}

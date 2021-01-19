package teammates.ui.webapi;

import teammates.common.util.Logger;

public class RecordResponseCountAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    ActionResult execute() {
        log.info(String.valueOf(logic.getTotalFeedBackResponseCount()) + String.valueOf(System.currentTimeMillis() / 1000));
        return new JsonResult("Successful");
    }
}

package teammates.ui.webapi;

import java.util.Set;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseRecord;
import teammates.ui.output.FeedbackResponseRecordData;

/**
 * Get response records.
 */
public class GetResponseStatsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    JsonResult execute() {
        String durationStr = getNonNullRequestParamValue(Const.ParamsNames.DURATION);
        String intervalStr = getNonNullRequestParamValue(Const.ParamsNames.INTERVAL);
        Set<FeedbackResponseRecord> feedbackResponseRecords =
                logic.getResponseRecords(Long.parseLong(durationStr), Long.parseLong(intervalStr));
        return new JsonResult(new FeedbackResponseRecordData(feedbackResponseRecords));
    }

    @Override
    void checkSpecificAccessControl() {
        //Only allows admins to call this api
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }
}

package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackResponseRecordsData;

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
        List<FeedbackResponseRecordAttributes> feedbackResponseRecords =
                logic.getResponseRecords(Long.parseLong(durationStr), Long.parseLong(intervalStr));
        return new JsonResult(new FeedbackResponseRecordsData(feedbackResponseRecords));
    }

    @Override
    void checkSpecificAccessControl() {
        //Only allows admins to call this api
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }
}

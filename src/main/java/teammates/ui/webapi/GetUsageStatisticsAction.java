package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import teammates.common.util.Const;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.ui.output.UsageStatisticsRangeData;

/**
 * Gets usage statistics for a specified time period.
 */
public class GetUsageStatisticsAction extends Action {

    private static final Duration MAX_SEARCH_WINDOW = Duration.ofDays(184L); // covering six whole months

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isMaintainer && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Only Maintainers or Admin are allowed to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
        long startTime;
        try {
            startTime = Long.parseLong(startTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid startTime parameter", e);
        }
        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
        long endTime;
        try {
            endTime = Long.parseLong(endTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid endTime parameter", e);
        }

        if (startTime >= endTime) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        if (endTime > Instant.now().toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time must not exceed the current time.");
        }

        if (endTime - startTime > MAX_SEARCH_WINDOW.toMillis()) {
            throw new InvalidHttpParameterException("The search window must not exceed "
                    + MAX_SEARCH_WINDOW.toDays() + " full days.");
        }

        List<UsageStatistics> usageStatisticsInRange =
                sqlLogic.getUsageStatisticsForTimeRange(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime));

        UsageStatisticsRangeData output = new UsageStatisticsRangeData(usageStatisticsInRange);
        return new JsonResult(output);
    }

}

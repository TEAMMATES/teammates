package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.UsageStatisticsRangeData;

/**
 * Gets usage statistics for a specified time period.
 *
 * <p>Statistics are calculated on-the-fly by counting entities created in hourly buckets.
 */
public class GetUsageStatisticsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!requestContext.isMaintainer() && !requestContext.isAdmin()) {
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

        Instant rangeStart = Instant.ofEpochMilli(startTime);
        Instant rangeEnd = Instant.ofEpochMilli(endTime);

        try {
            return new JsonResult(new UsageStatisticsRangeData(logic.getUsageStatistics(rangeStart, rangeEnd)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpParameterException(e);
        }
    }

}

package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * The API output format of a list of {@link UsageStatisticsAttributes} and {@link UsageStatistics}.
 */
public class UsageStatisticsRangeData extends ApiOutput {

    private final List<UsageStatisticsData> result;

    public UsageStatisticsRangeData(
            List<UsageStatisticsAttributes> usageStatistics,
            List<UsageStatistics> sqlUsageStatistics) {
        List<UsageStatisticsData> allData = new ArrayList<>();

        allData.addAll(usageStatistics.stream().map(UsageStatisticsData::new).collect(Collectors.toList()));
        allData.addAll(sqlUsageStatistics.stream().map(UsageStatisticsData::new).collect(Collectors.toList()));

        this.result = allData;
    }

    public List<UsageStatisticsData> getResult() {
        return result;
    }

}

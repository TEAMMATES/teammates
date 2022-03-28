package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;

/**
 * The API output format of a list of {@link UsageStatisticsAttributes}.
 */
public class UsageStatisticsRangeData extends ApiOutput {

    private final List<UsageStatisticsData> result;

    public UsageStatisticsRangeData(List<UsageStatisticsAttributes> usageStatistics) {
        this.result = usageStatistics.stream().map(UsageStatisticsData::new).collect(Collectors.toList());
    }

    public List<UsageStatisticsData> getResult() {
        return result;
    }

}

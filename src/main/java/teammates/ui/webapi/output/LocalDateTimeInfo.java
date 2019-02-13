package teammates.ui.webapi.output;

import teammates.common.util.TimeHelper;

/**
 * The API output format of a {@code LocalDateTimeInfo} to hold information for resolving DST overlaps/gaps.
 */
public class LocalDateTimeInfo extends ApiOutput {
    private long resolvedTimestamp;
    private TimeHelper.LocalDateTimeAmbiguityStatus resolvedStatus;

    private Long earlierInterpretationTimestamp;
    private Long laterInterpretationTimestamp;

    /**
     * Constructs {@link LocalDateTimeInfo} with UNAMBIGUOUS status.
     */
    public static LocalDateTimeInfo unambiguous(long resolvedTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.UNAMBIGUOUS;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfo} with GAP status.
     */
    public static LocalDateTimeInfo gap(long resolvedTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.GAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfo} with OVERLAP status.
     */
    public static LocalDateTimeInfo overlap(long resolvedTimestamp,
                                            long earlierInterpretationTimestamp, long laterInterpretationTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.OVERLAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        localDateTimeInfo.earlierInterpretationTimestamp = earlierInterpretationTimestamp;
        localDateTimeInfo.laterInterpretationTimestamp = laterInterpretationTimestamp;

        return localDateTimeInfo;
    }

    public long getResolvedTimestamp() {
        return resolvedTimestamp;
    }

    public TimeHelper.LocalDateTimeAmbiguityStatus getResolvedStatus() {
        return resolvedStatus;
    }

    public Long getEarlierInterpretationTimestamp() {
        return earlierInterpretationTimestamp;
    }

    public Long getLaterInterpretationTimestamp() {
        return laterInterpretationTimestamp;
    }
}

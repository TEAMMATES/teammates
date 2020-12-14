package teammates.ui.output;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import javax.annotation.Nullable;

/**
 * The API output format of a {@code LocalDateTimeInfo} to hold information for resolving DST overlaps/gaps.
 */
public class LocalDateTimeInfo extends ApiOutput {
    private long resolvedTimestamp;
    private LocalDateTimeAmbiguityStatus resolvedStatus;
    @Nullable
    private Long earlierInterpretationTimestamp;
    @Nullable
    private Long laterInterpretationTimestamp;

    /**
     * Constructs {@link LocalDateTimeInfo} with UNAMBIGUOUS status.
     */
    public static LocalDateTimeInfo unambiguous(long resolvedTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = LocalDateTimeAmbiguityStatus.UNAMBIGUOUS;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfo} with GAP status.
     */
    public static LocalDateTimeInfo gap(long resolvedTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = LocalDateTimeAmbiguityStatus.GAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfo} with OVERLAP status.
     */
    public static LocalDateTimeInfo overlap(long resolvedTimestamp,
                                            long earlierInterpretationTimestamp, long laterInterpretationTimestamp) {
        LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

        localDateTimeInfo.resolvedStatus = LocalDateTimeAmbiguityStatus.OVERLAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        localDateTimeInfo.earlierInterpretationTimestamp = earlierInterpretationTimestamp;
        localDateTimeInfo.laterInterpretationTimestamp = laterInterpretationTimestamp;

        return localDateTimeInfo;
    }

    public long getResolvedTimestamp() {
        return resolvedTimestamp;
    }

    public LocalDateTimeAmbiguityStatus getResolvedStatus() {
        return resolvedStatus;
    }

    public Long getEarlierInterpretationTimestamp() {
        return earlierInterpretationTimestamp;
    }

    public Long getLaterInterpretationTimestamp() {
        return laterInterpretationTimestamp;
    }

    /**
     * Represents the ambiguity status for a {@link LocalDateTime} at a given time {@code zone},
     * brought about by Daylight Saving Time (DST).
     */
    public enum LocalDateTimeAmbiguityStatus {
        /**
         * The local date time can be unambiguously resolved to a single instant.
         * It has only one valid interpretation.
         */
        UNAMBIGUOUS,

        /**
         * The local date time falls within the gap period when clocks spring forward at the start of DST.
         * Strictly speaking, it is non-existent, and needs to be readjusted to be valid.
         */
        GAP,

        /**
         * The local date time falls within the overlap period when clocks fall back at the end of DST.
         * It has more than one valid interpretation.
         */
        OVERLAP;

        /**
         * Gets the ambiguity status for a {@link LocalDateTime} at a given time {@code zone}.
         */
        public static LocalDateTimeAmbiguityStatus of(LocalDateTime localDateTime, ZoneId zone) {
            if (localDateTime == null || zone == null) {
                return null;
            }

            List<ZoneOffset> offsets = zone.getRules().getValidOffsets(localDateTime);
            if (offsets.size() == 1) {
                return UNAMBIGUOUS;
            }
            if (offsets.isEmpty()) {
                return GAP;
            }
            return OVERLAP;
        }
    }
}

package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.storage.entity.UsageStatistics;

/**
 * The data transfer object for {@link UsageStatistics} entities.
 */
public final class UsageStatisticsAttributes extends EntityAttributes<UsageStatistics> {

    private final Instant startTime;
    private final int timePeriod;
    private int numResponses;

    private UsageStatisticsAttributes(Instant startTime, int timePeriod) {
        this.startTime = startTime;
        this.timePeriod = timePeriod;
    }

    /**
     * Gets the {@link UsageStatisticsAttributes} instance of the given {@link UsageStatistics}.
     */
    public static UsageStatisticsAttributes valueOf(UsageStatistics us) {
        UsageStatisticsAttributes attributes = new UsageStatisticsAttributes(us.getStartTime(), us.getTimePeriod());

        attributes.numResponses = us.getNumResponses();

        return attributes;
    }

    /**
     * Gets a deep copy of this object.
     */
    public UsageStatisticsAttributes getCopy() {
        UsageStatisticsAttributes attributes =
                new UsageStatisticsAttributes(this.startTime, this.timePeriod);

        attributes.numResponses = this.numResponses;

        return attributes;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public int getNumResponses() {
        return numResponses;
    }

    @Override
    public List<String> getInvalidityInfo() {
        // Nothing to check
        return new ArrayList<>();
    }

    @Override
    public UsageStatistics toEntity() {
        return new UsageStatistics(startTime, timePeriod, numResponses);
    }

    @Override
    public String toString() {
        return "UsageStatisticsAttributes [startTime="
                + startTime + ", timePeriod=" + timePeriod + "]";
    }

    @Override
    public int hashCode() {
        return (startTime.toEpochMilli() + "%" + timePeriod).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            UsageStatisticsAttributes otherStatistics = (UsageStatisticsAttributes) other;
            // Due to the immutable design for the entity, only the two basic fields are necessary
            // to determine the equality of two attributes
            return Objects.equals(this.startTime, otherStatistics.startTime)
                    && Objects.equals(this.timePeriod, otherStatistics.timePeriod);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        // Nothing to sanitize
    }

    /**
     * Returns a builder for {@link UsageStatisticsAttributes}.
     */
    public static Builder builder(Instant startTime, int timePeriod) {
        return new Builder(startTime, timePeriod);
    }

    /**
     * A builder for {@link UsageStatisticsAttributes}.
     *
     * <p>Note that we are using a simplified builder pattern here, as opposed to builders of other attributes,
     * as this entity is designed to be immutable.
     */
    public static class Builder {

        private final UsageStatisticsAttributes usa;

        private Builder(Instant startTime, int timePeriod) {
            assert timePeriod > 0;
            assert startTime.toEpochMilli() <= Instant.now().toEpochMilli();
            usa = new UsageStatisticsAttributes(startTime, timePeriod);
        }

        public Builder withNumResponses(int numResponses) {
            usa.numResponses = numResponses;
            return this;
        }

        public UsageStatisticsAttributes build() {
            return usa;
        }

    }

}

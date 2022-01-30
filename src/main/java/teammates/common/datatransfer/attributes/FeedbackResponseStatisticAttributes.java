package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseStatistic;

public class FeedbackResponseStatisticAttributes extends EntityAttributes<FeedbackResponseStatistic> {
	private Date timeStamp;
	private int count;
	// TODO what is transient
    private transient Instant createdAt;
    private transient Instant updatedAt;

	private FeedbackResponseStatisticAttributes(Date timeStamp, int count) {
		this.timeStamp = timeStamp;
        this.count = count;

        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }


	@Override
	public FeedbackResponseStatistic toEntity() {
		return new FeedbackResponseStatistic(timeStamp, count);
	}
	
	/**
     * Returns a builder for {@link CourseAttributes}.
     */
    public static Builder builder(String courseId) {
        return new Builder(courseId);
    }

	// TODO
    /**
     * Return a builder for {@link FeedbackResponseStatisticAttributes}.
     */
    public static Builder builder(String courseId, String email) {
        return new Builder(courseId, email);
    }

    /**
     * Gets the {@link FeedbackResponseStatisticAttributes} instance of the given {@link FeedbackResponseStatistic}.
     */
    public static FeedbackResponseStatisticAttributes valueOf(FeedbackResponseStatistic statistic) {
        FeedbackResponseStatisticAttributes statisticAttributes =
                new FeedbackResponseStatisticAttributes(statistic.getTimeStamp(), statistic.getCount());

        if (statistic.getCreatedAt() != null) {
            statisticAttributes.createdAt = statistic.getCreatedAt();
        }
        if (statistic.getUpdatedAt() != null) {
            statisticAttributes.updatedAt = statistic.getUpdatedAt();
        }

        return statisticAttributes;
    }

    @Override
    public List<String> getInvalidityInfo() {
        // TODO
    }

    /**
     * Sorts the instructors list alphabetically by name.
     */
	public static void sortByTimeStamp(List<FeedbackResponseStatisticAttributes> statistics) {
		statistics.sort(Comparator.comparing(statistic -> statistic.getTimeStamp()));
	}

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.timeStamp).append(this.count);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackResponseStatisticAttributes otherStatistic = (FeedbackResponseStatisticAttributes) other;
            return Objects.equals(this.timeStamp, otherStatistic.timeStamp)
					&& Objects.equals(this.count, otherStatistic.count);
        } else {
            return false;
        }
    }

	// TODO
    @Override
    public void sanitizeForSaving() {
        
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
    }

    public int getCount() {
        return count;
    }

	public void setCount(int count) {
		this.count = count;
	}
}

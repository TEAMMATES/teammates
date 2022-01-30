package teammates.storage.entity;

import java.time.Instant;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

// TODO read all the annotations
@Cache
@Entity
@Index
public class FeedbackResponseStatistic extends BaseEntity {
	@Id
	private long id;

	/** The 1 minute time frame the statistic is measuring */
	private Instant timeStamp;

	/** Number of responses recorded during the time period */
	private int count;

	@Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;


	// TODO Id of responses for cronjob to verify

	public FeedbackResponseStatistic(Instant timestamp, int count) {
		this.setTimeStamp(timestamp);
		this.setCount(count);
	}

	/**
     * Gets the timestamp that the statistic accounts for.
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timestamp) {
        this.timeStamp = timestamp;
    }

	/**
     * Gets the number of responses recorded.
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

	public Instant getCreatedAt() {
		return createdAt;
	}
	
	/**
     * Sets the createdAt timestamp.
     */
    public void setCreatedAt(Instant created) {
        this.createdAt = created;
        setLastUpdate(created);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

	public void setLastUpdate(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

	/**
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }

}

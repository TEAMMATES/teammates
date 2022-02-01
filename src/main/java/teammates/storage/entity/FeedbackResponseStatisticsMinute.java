package teammates.storage.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class FeedbackResponseStatisticsMinute extends BaseEntity {

    @Id
    // Represents the middle of the minute
    private String time;

    private Integer count;

    @SuppressWarnings("unused")
    private FeedbackResponseStatisticsMinute() {
        // required by Objectify
    }

    /**
     * Instantiates a new account.
     *
     * @param time
     *            the middle of the minute, with ISO 8601 representation.
     * @param count
     *            the number of feedbacck responses in the minute.
     */
    public FeedbackResponseStatisticsMinute(String time, Integer count) {
        this.setTime(time);
        this.setCount(count);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

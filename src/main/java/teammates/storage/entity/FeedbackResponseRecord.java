package teammates.storage.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents a record of {@code FeedbackResponse} count at an instant.
 */
@Entity
@Index
public class FeedbackResponseRecord extends BaseEntity {

    @Id
    private String key;

    @SuppressWarnings("unused")
    private FeedbackResponseRecord() {
        // Required by Objectify
    }

    public FeedbackResponseRecord(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackSessionLogEntry;

public class FeedbackSessionLogEntryAttributes extends EntityAttributes<FeedbackSessionLogEntry> {

    private final String feedbackSessionLogEntryId;
    private String studentEmail;
    private final String courseId;
    private String feedbackSessionName;
    private final String feedbackSessionLogType;
    private final long timestamp;
    private final Instant createdAt;

    private FeedbackSessionLogEntryAttributes(FeedbackSessionLogEntry fslEntry) {
        this.feedbackSessionLogEntryId = fslEntry.getFeedbackSessionLogEntryId();
        this.studentEmail = fslEntry.getStudentEmail();
        this.feedbackSessionName = fslEntry.getFeedbackSessionName();
        this.feedbackSessionLogType = fslEntry.getFeedbackSessionLogType();
        this.timestamp = fslEntry.getTimestamp();
        this.createdAt = fslEntry.getCreatedAt();
        this.courseId = fslEntry.getCourseId();
    }

    public static FeedbackSessionLogEntryAttributes valueOf(FeedbackSessionLogEntry fslEntry) {
        return new FeedbackSessionLogEntryAttributes(fslEntry);
    }

    public String getFeedbackSessionLogEntryId() {
        return feedbackSessionLogEntryId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(studentEmail), errors);

        return errors;
    }

    @Override
    public FeedbackSessionLogEntry toEntity() {
        return new FeedbackSessionLogEntry(studentEmail, courseId, feedbackSessionName, feedbackSessionLogType, timestamp);
    }

    @Override
    public void sanitizeForSaving() {
        this.studentEmail = SanitizationHelper.sanitizeEmail(this.studentEmail);
        this.feedbackSessionName = SanitizationHelper.sanitizeName(feedbackSessionName);
    }

    @Override
    public int hashCode() {
        return this.feedbackSessionLogEntryId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionLogEntryAttributes otherEntry = (FeedbackSessionLogEntryAttributes) other;
            return Objects.equals(this.feedbackSessionLogEntryId, otherEntry.feedbackSessionLogEntryId);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, FeedbackSessionLogEntryAttributes.class);
    }
}

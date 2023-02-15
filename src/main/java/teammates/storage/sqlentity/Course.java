package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "Courses")
public class Course extends BaseEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String timeZone;

    @Column(nullable = false)
    private String institute;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<FeedbackSession> feedbackSessions = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    protected Course() {
        // required by Hibernate
    }

    public Course(String id, String name, String timeZone, String institute) {
        this.setId(id);
        this.setName(name);
        this.setTimeZone(StringUtils.defaultIfEmpty(timeZone, Const.DEFAULT_TIME_ZONE));
        this.setInstitute(institute);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(getId()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);

        return errors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = SanitizationHelper.sanitizeTitle(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    public List<FeedbackSession> getFeedbackSessions() {
        return feedbackSessions;
    }

    public void setFeedbackSessions(List<FeedbackSession> feedbackSessions) {
        this.feedbackSessions = feedbackSessions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + ", timeZone=" + timeZone + ", institute=" + institute
                + ", feedbackSessions=" + feedbackSessions + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Course otherCourse = (Course) other;
            return Objects.equals(this.id, otherCourse.id);
        } else {
            return false;
        }
    }
}

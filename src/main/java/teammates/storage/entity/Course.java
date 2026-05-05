package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a course.
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

    @OneToMany(mappedBy = "course")
    private Set<FeedbackSession> feedbackSessions = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Section> sections = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

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

    /**
     * Adds a section to the Course.
     */
    public void addSection(Section section) {
        this.sections.add(section);
    }

    /**
     * Adds a feedback session to the Course.
     */
    public void addFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSessions.add(feedbackSession);
        feedbackSession.setCourse(this);
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

    public Set<FeedbackSession> getFeedbackSessions() {
        return feedbackSessions;
    }

    public void setFeedbackSessions(Set<FeedbackSession> feedbackSessions) {
        this.feedbackSessions = feedbackSessions;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
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

    public boolean isCourseDeleted() {
        return this.deletedAt != null;
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + ", timeZone=" + timeZone + ", institute=" + institute
                + ", feedbackSessions=" + feedbackSessions + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Course other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

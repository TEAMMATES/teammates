package teammates.storage.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import teammates.common.datatransfer.InstructorPermissionSet;

/**
 * Represents the session-in-section-level permissions of an instructor with a custom role.
 *
 * <p>Only the subset of permissions applicable at the session level are stored. A row exists
 * only for instructors whose role is custom and who have session-specific overrides.
 */
@Entity
@Table(name = "InstructorSessionPrivileges", uniqueConstraints = {
        @UniqueConstraint(name = "Unique instructorId, sectionId and sessionId",
                columnNames = { "instructorId", "sectionId", "sessionId" })
})
public class InstructorSessionPrivilege extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "instructorId", nullable = false)
    private Instructor instructor;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID instructorId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sectionId", nullable = false)
    private Section section;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID sectionId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sessionId", nullable = false)
    private FeedbackSession feedbackSession;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private boolean canViewSession;

    @Column(nullable = false)
    private boolean canSubmitSession;

    @Column(nullable = false)
    private boolean canModifySessionComments;

    protected InstructorSessionPrivilege() {
        // required by Hibernate
    }

    public InstructorSessionPrivilege(Instructor instructor, Section section, FeedbackSession feedbackSession,
            InstructorPermissionSet privileges) {
        this.setId(UUID.randomUUID());
        this.setInstructor(instructor);
        this.setSection(section);
        this.setFeedbackSession(feedbackSession);
        this.setPrivileges(privileges);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public UUID getInstructorId() {
        return instructorId;
    }

    /**
     * Sets the instructor as well as the instructorId.
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        this.instructorId = instructor == null ? null : instructor.getId();
    }

    public Section getSection() {
        return section;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    /**
     * Sets the section as well as the sectionId.
     */
    public void setSection(Section section) {
        this.section = section;
        this.sectionId = section == null ? null : section.getId();
    }

    public FeedbackSession getFeedbackSession() {
        return feedbackSession;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Sets the feedback session as well as the sessionId.
     */
    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
        this.sessionId = feedbackSession == null ? null : feedbackSession.getId();
    }

    /**
     * Returns the stored permissions as an {@link InstructorPermissionSet}.
     */
    public InstructorPermissionSet getPrivileges() {
        InstructorPermissionSet privileges = new InstructorPermissionSet();
        privileges.setCanViewSession(canViewSession);
        privileges.setCanSubmitSession(canSubmitSession);
        privileges.setCanModifySessionComments(canModifySessionComments);
        return privileges;
    }

    /**
     * Copies the session-level permissions from the given {@link InstructorPermissionSet}.
     */
    public void setPrivileges(InstructorPermissionSet privileges) {
        this.canViewSession = privileges.isCanViewSession();
        this.canSubmitSession = privileges.isCanSubmitSession();
        this.canModifySessionComments = privileges.isCanModifySessionComments();
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InstructorSessionPrivilege other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InstructorSessionPrivilege [id=" + id + ", instructorId=" + instructorId
                + ", sectionId=" + sectionId + ", sessionId=" + sessionId
                + ", createdAt=" + getCreatedAt() + "]";
    }
}

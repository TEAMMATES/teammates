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
 * Represents the section-level permissions of an instructor with a custom role.
 *
 * <p>Only the subset of permissions applicable at the section level are stored. A row exists
 * only for instructors whose role is custom and who have section-specific overrides.
 */
@Entity
@Table(name = "InstructorSectionPrivileges", uniqueConstraints = {
        @UniqueConstraint(name = "Unique instructorId and sectionId", columnNames = { "instructorId", "sectionId" })
})
public class InstructorSectionPrivilege extends BaseEntity {
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

    @Column(nullable = false)
    private boolean canViewStudentInSections;

    @Column(nullable = false)
    private boolean canViewSessionInSections;

    @Column(nullable = false)
    private boolean canSubmitSessionInSections;

    @Column(nullable = false)
    private boolean canModifySessionCommentsInSections;

    protected InstructorSectionPrivilege() {
        // required by Hibernate
    }

    public InstructorSectionPrivilege(Instructor instructor, Section section, InstructorPermissionSet privileges) {
        this.setId(UUID.randomUUID());
        this.setInstructor(instructor);
        this.setSection(section);
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

    /**
     * Returns the stored permissions as an {@link InstructorPermissionSet}.
     */
    public InstructorPermissionSet getPrivileges() {
        InstructorPermissionSet privileges = new InstructorPermissionSet();
        privileges.setCanViewStudentInSections(canViewStudentInSections);
        privileges.setCanViewSessionInSections(canViewSessionInSections);
        privileges.setCanSubmitSessionInSections(canSubmitSessionInSections);
        privileges.setCanModifySessionCommentsInSections(canModifySessionCommentsInSections);
        return privileges;
    }

    /**
     * Copies the section-level permissions from the given {@link InstructorPermissionSet}.
     */
    public void setPrivileges(InstructorPermissionSet privileges) {
        this.canViewStudentInSections = privileges.isCanViewStudentInSections();
        this.canViewSessionInSections = privileges.isCanViewSessionInSections();
        this.canSubmitSessionInSections = privileges.isCanSubmitSessionInSections();
        this.canModifySessionCommentsInSections = privileges.isCanModifySessionCommentsInSections();
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

        if (!(o instanceof InstructorSectionPrivilege other)) {
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
        return "InstructorSectionPrivilege [id=" + id + ", instructorId=" + instructorId
                + ", sectionId=" + sectionId + ", createdAt=" + getCreatedAt() + "]";
    }
}

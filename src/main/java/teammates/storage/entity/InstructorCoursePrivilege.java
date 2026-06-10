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
 * Represents the course-level permissions of an instructor with a custom role.
 *
 * <p>A row exists only for instructors whose role is custom. For predefined roles, the
 * privileges are derived from the role and no row is stored.
 */
@Entity
@Table(name = "InstructorCoursePrivileges", uniqueConstraints = {
        @UniqueConstraint(name = "Unique instructorId", columnNames = { "instructorId" })
})
public class InstructorCoursePrivilege extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "instructorId", nullable = false)
    private Instructor instructor;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID instructorId;

    @Column(nullable = false)
    private boolean canModifyCourse;

    @Column(nullable = false)
    private boolean canModifyInstructor;

    @Column(nullable = false)
    private boolean canModifySession;

    @Column(nullable = false)
    private boolean canModifyStudent;

    @Column(nullable = false)
    private boolean canViewStudent;

    @Column(nullable = false)
    private boolean canViewSession;

    @Column(nullable = false)
    private boolean canSubmitSession;

    @Column(nullable = false)
    private boolean canModifySessionComments;

    protected InstructorCoursePrivilege() {
        // required by Hibernate
    }

    public InstructorCoursePrivilege(Instructor instructor, InstructorPermissionSet privileges) {
        this.setId(UUID.randomUUID());
        this.setInstructor(instructor);
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

    /**
     * Returns the stored permissions as an {@link InstructorPermissionSet}.
     */
    public InstructorPermissionSet getPrivileges() {
        InstructorPermissionSet privileges = new InstructorPermissionSet();
        privileges.setCanModifyCourse(canModifyCourse);
        privileges.setCanModifyInstructor(canModifyInstructor);
        privileges.setCanModifySession(canModifySession);
        privileges.setCanModifyStudent(canModifyStudent);
        privileges.setCanViewStudent(canViewStudent);
        privileges.setCanViewSession(canViewSession);
        privileges.setCanSubmitSession(canSubmitSession);
        privileges.setCanModifySessionComments(canModifySessionComments);
        return privileges;
    }

    /**
     * Copies the permissions from the given {@link InstructorPermissionSet}.
     */
    public void setPrivileges(InstructorPermissionSet privileges) {
        this.canModifyCourse = privileges.isCanModifyCourse();
        this.canModifyInstructor = privileges.isCanModifyInstructor();
        this.canModifySession = privileges.isCanModifySession();
        this.canModifyStudent = privileges.isCanModifyStudent();
        this.canViewStudent = privileges.isCanViewStudent();
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

        if (!(o instanceof InstructorCoursePrivilege other)) {
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
        return "InstructorCoursePrivilege [id=" + id + ", instructorId=" + instructorId
                + ", createdAt=" + getCreatedAt() + "]";
    }
}

package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.ui.output.InstructorPermissionRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Represents an Instructor entity.
 */
@Entity
@Table(name = "Instructors")
public class Instructor extends User {
    @Column(nullable = false)
    private String registrationKey;

    @Column(nullable = false)
    private boolean isDisplayedToStudents;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstructorPermissionRole role;

    @Column(nullable = false)
    private String instructorPrivileges;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected Instructor() {
        // required by Hibernate
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    public boolean isDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public void setDisplayedToStudents(boolean displayedToStudents) {
        isDisplayedToStudents = displayedToStudents;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public InstructorPermissionRole getRole() {
        return role;
    }

    public void setRole(InstructorPermissionRole role) {
        this.role = role;
    }

    public String getInstructorPrivileges() {
        return instructorPrivileges;
    }

    public void setInstructorPrivileges(String instructorPrivileges) {
        this.instructorPrivileges = instructorPrivileges;
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

    @Override
    public String toString() {
        return "Instructor [id=" + super.getId() + ", registrationKey=" + registrationKey
                + ", isDisplayedToStudents=" + isDisplayedToStudents + ", displayName=" + displayName
                + ", role=" + role + ", instructorPrivileges=" + instructorPrivileges
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // Instructor Id uniquely identifies an Instructor
        return super.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            Instructor otherInstructor = (Instructor) obj;

            return Objects.equals(super.getId(), otherInstructor.getId());
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getInvalidityInfo() {
        // TODO Auto-generated method stub
        return null;
    }
}

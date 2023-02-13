package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import teammates.ui.output.InstructorPermissionRole;

/**
 * Represents an Instructor entity.
 */
@Entity
@Table(name = "Instructors")
public class Instructor extends User {
    @OneToOne(mappedBy = "id")
    @Column(nullable = false)
    private int id;

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

    private Instructor(InstructorBuilder builder) {
        this.setId(builder.id);
        this.setRegistrationKey(builder.registrationKey);
        this.setDisplayedToStudents(builder.isDisplayedToStudents);
        this.setDisplayName(builder.displayName);
        this.setRole(builder.role);
        this.setInstructorPrivileges(builder.instructorPrivileges);

        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }

        this.setUpdatedAt(updatedAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "Instructor [id=" + id + ", registrationKey=" + registrationKey
                + ", isDisplayedToStudents=" + isDisplayedToStudents + ", displayName=" + displayName
                + ", role=" + role + ", instructorPrivileges=" + instructorPrivileges
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // Instructor Id uniquely identifies an Instructor
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            Instructor otherInstructor = (Instructor) obj;

            return Objects.equals(this.id, otherInstructor.id);
        } else {
            return false;
        }
    }

    /**
     * Builder for Instructor.
     */
    public static class InstructorBuilder {
        private int id;
        private String registrationKey;
        private boolean isDisplayedToStudents;
        private String displayName;
        private InstructorPermissionRole role;
        private String instructorPrivileges;

        public InstructorBuilder(int id) {
            this.id = id;
        }

        public InstructorBuilder withRegistrationKey(String registrationKey) {
            this.registrationKey = registrationKey;
            return this;
        }

        public InstructorBuilder withIsDisplayedToStudents(boolean isDisplayedToStudents) {
            this.isDisplayedToStudents = isDisplayedToStudents;
            return this;
        }

        public InstructorBuilder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public InstructorBuilder withRole(InstructorPermissionRole role) {
            this.role = role;
            return this;
        }

        public InstructorBuilder withInstructorPrivileges(String instructorPrivileges) {
            this.instructorPrivileges = instructorPrivileges;
            return this;
        }

        public Instructor build() {
            return new Instructor(this);
        }
    }
}

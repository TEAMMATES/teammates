package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.JsonUtils;
import teammates.ui.output.InstructorPermissionRole;

/**
 * Represents an Instructor entity.
 */
@Entity
@Table(name = "Instructors")
// Might have to change this. Should follow User's PK. Same for Student
@PrimaryKeyJoinColumn(name = "userId")
public class Instructor { // TODO: extends User
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String registrationKey;

    @Column(nullable = false)
    private boolean isDisplayedToStudents;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstructorPermissionRole role;

    @Column(columnDefinition = "json", nullable = false)
    private Map<String, String> instructorPrivilegesAsText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected Instructor() {
        // required by Hibernate
    }

    private Instructor(InstructorBuilder builder) {
        this.setId(builder.id);
        this.setUserId(builder.userId);
        this.setRegistrationKey(builder.registrationKey);
        this.setDisplayName(builder.displayName);
        this.setRole(builder.role);
        this.setInstructorPrivilegesAsText(builder.instructorPrivilegesAsText);

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Map<String, String> getInstructorPrivilegesAsText() {
        return instructorPrivilegesAsText;
    }

    public void setInstructorPrivilegesAsText(Map<String, String> instructorPrivilegesAsText) {
        this.instructorPrivilegesAsText = instructorPrivilegesAsText;
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

    // TODO: Update
    @Override
    public String toString() {
        return JsonUtils.toJson(this, InstructorAttributes.class);
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
        private int userId;
        private String registrationKey;
        private boolean isDisplayedToStudents;
        private String displayName;
        private InstructorPermissionRole role;
        private Map<String, String> instructorPrivilegesAsText;

        public InstructorBuilder(int id) {
            this.id = id;
        }

        public InstructorBuilder withUserId(int userId) {
            this.userId = userId;
            return this;
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

        public InstructorBuilder withInstructorPrivilegesAsText(
                Map<String, String> instructorPrivilegesAsText) {
            this.instructorPrivilegesAsText = instructorPrivilegesAsText;
            return this;
        }

        public Instructor build() {
            return new Instructor(this);
        }
    }
}

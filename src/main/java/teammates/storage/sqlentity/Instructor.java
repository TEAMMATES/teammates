package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Represents an Instructor.
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

    protected Instructor() {
        // required by Hibernate
    }

    public Instructor(String registrationKey, boolean isDisplayedToStudents, String displayName,
            InstructorPermissionRole role, String instructorPrivileges) {
        this.registrationKey = registrationKey;
        this.isDisplayedToStudents = isDisplayedToStudents;
        this.displayName = displayName;
        this.role = role;
        this.instructorPrivileges = instructorPrivileges;
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
        this.displayName = SanitizationHelper.sanitizeName(displayName);
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

    @Override
    public String toString() {
        return "Instructor [id=" + super.getId() + ", registrationKey=" + registrationKey
                + ", isDisplayedToStudents=" + isDisplayedToStudents + ", displayName=" + displayName
                + ", role=" + role + ", instructorPrivileges=" + instructorPrivileges
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(super.getCourse().getId()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(super.getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(super.getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(displayName), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForRole(role.name()), errors);

        return errors;
    }
}

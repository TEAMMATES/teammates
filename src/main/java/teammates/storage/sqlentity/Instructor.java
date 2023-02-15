package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
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

    @Override
    public String toString() {
        return "Instructor [id=" + super.getId() + ", registrationKey=" + registrationKey
                + ", isDisplayedToStudents=" + isDisplayedToStudents + ", displayName=" + displayName
                + ", role=" + role + ", instructorPrivileges=" + instructorPrivileges
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
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
        if (role == null) {
            role = InstructorPermissionRole
                    .valueOf(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        } else {
            role = InstructorPermissionRole
                    .valueOf(SanitizationHelper.sanitizeName(role.name()));
        }

        if (displayName == null) {
            displayName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            displayName = SanitizationHelper.sanitizeName(displayName);
        }

        if (instructorPrivileges == null) {
            instructorPrivileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER).toString();
        }
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

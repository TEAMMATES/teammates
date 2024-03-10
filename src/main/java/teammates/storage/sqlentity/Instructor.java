package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents an Instructor.
 */
@Entity
@Table(name = "Instructors")
public class Instructor extends User {
    @Column(nullable = false)
    private boolean isDisplayedToStudents;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstructorPermissionRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = InstructorPrivilegesConverter.class)
    private InstructorPrivileges privileges;

    protected Instructor() {
        // required by Hibernate
    }

    public Instructor(Course course, String name, String email, boolean isDisplayedToStudents,
            String displayName, InstructorPermissionRole role, InstructorPrivileges privileges) {
        super(course, name, email);
        this.setDisplayedToStudents(isDisplayedToStudents);
        this.setDisplayName(displayName);
        this.setRole(role);
        this.setPrivileges(privileges);
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

    public InstructorPrivileges getPrivileges() {
        return privileges;
    }

    public void setPrivileges(InstructorPrivileges instructorPrivileges) {
        this.privileges = instructorPrivileges;
    }

    @Override
    public String getTeamName() {
        return Const.USER_TEAM_FOR_INSTRUCTOR;
    }

    @Override
    public String getSectionName() {
        return Const.DEFAULT_SECTION;
    }

    @Override
    public Section getSection() {
        return null;
    }

    @Override
    public String toString() {
        return "Instructor [id=" + super.getId() + ", isDisplayedToStudents=" + isDisplayedToStudents
                + ", displayName=" + displayName + ", role=" + role + ", instructorPrivileges=" + privileges
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(super.getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(super.getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(displayName), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForRole(role.getRoleName()), errors);

        return errors;
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getRegKey())
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toString();
    }

    /**
     * Returns true if the instructor has co-owner privilege.
     */
    public boolean hasCoownerPrivileges() {
        return privileges.hasCoownerPrivileges();
    }

    /**
     * Returns a list of sections this instructor has the specified privilege.
     */
    public Map<String, InstructorPermissionSet> getSectionsWithPrivilege(String privilegeName) {
        return this.privileges.getSectionsWithPrivilege(privilegeName);
    }

    /**
     * Returns true if the instructor has the given privilege in the course.
     */
    public boolean isAllowedForPrivilege(String privilegeName) {
        return this.privileges.isAllowedForPrivilege(privilegeName);
    }

    /**
     * Returns true if the instructor has the given privilege in the given section for the given feedback session.
     */
    public boolean isAllowedForPrivilege(String sectionName, String sessionName, String privilegeName) {
        return privileges.isAllowedForPrivilege(sectionName, sessionName, privilegeName);
    }

    /**
     * Returns true if the instructor has the given privilege in the given section.
     */
    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        return privileges.isAllowedForPrivilege(sectionName, privilegeName);
    }

    /**
     * Returns true if privilege for session is present for any section.
     */
    public boolean isAllowedForPrivilegeAnySection(String sessionName, String privilegeName) {
        return privileges.isAllowedForPrivilegeAnySection(sessionName, privilegeName);
    }
}

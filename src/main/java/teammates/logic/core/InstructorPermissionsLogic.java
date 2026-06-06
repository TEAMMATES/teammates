package teammates.logic.core;

import java.util.Map;
import java.util.Objects;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;

/**
 * Handles the logic related to instructor permissions.
 */
public final class InstructorPermissionsLogic {
    private static final InstructorPermissionsLogic instance = new InstructorPermissionsLogic();

    private InstructorPermissionsLogic() {
        // prevent instantiation
    }

    public static InstructorPermissionsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // no dependencies to initialize
    }

    /**
     * Checks if the given instructor has the specified permission.
     */
    public boolean hasPermissions(Instructor instructor, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(permissionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified section-level permission.
     */
    public boolean hasPermissionsForSection(Instructor instructor, String sectionName, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(sectionName, "Section name cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(permissionName, sectionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permission.
     */
    public boolean hasPermissionsForSessionInSection(Instructor instructor, String sectionName,
            String feedbackSessionName, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(sectionName, "Section name cannot be null");
        Objects.requireNonNull(feedbackSessionName, "Feedback session name cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(permissionName, sectionName, feedbackSessionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permission in any section.
     */
    public boolean hasPermissionsForSectionInAnySection(Instructor instructor,
            String sessionName, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (privileges.isAllowedForPrivilegeAnySection(sessionName, permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a map of sections with the specified permission for the given instructor.
     */
    public Map<String, InstructorPermissionSet> getSectionsWithPermission(Instructor instructor, String permissionName) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(permissionName, "Permission name cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);
        return privileges.getSectionsWithPrivilege(permissionName);
    }

    /**
     * Returns the InstructorPrivileges for the given instructor.
     *
     * <p>
     * For instructors with predefined roles, the privileges are determined by their
     * role.
     * For instructors with the custom role, the privileges are determined by their
     * stored privileges.
     */
    public InstructorPrivileges getInstructorPrivileges(Instructor instructor) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPermissionRole role = instructor.getRole();

        switch (role) {
        case INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.COOWNER);
        case INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.MANAGER);
        case INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.OBSERVER);
        case INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.TUTOR);
        case INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            return instructor.getPrivileges();
        default:
            throw new IllegalStateException("Unexpected instructor role: " + role);
        }

    }
}

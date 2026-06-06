package teammates.common.datatransfer;

import teammates.common.util.Const;

/**
 * Instructor Permission Role.
 *
 * {@link Const.InstructorPermissionRoleNames}
 */
public enum InstructorPermissionRole {
    /**
     * Co-owner.
     */
    INSTRUCTOR_PERMISSION_ROLE_COOWNER(Const.InstructorPermissionRoleNames.COOWNER),

    /**
     * Manager.
     */
    INSTRUCTOR_PERMISSION_ROLE_MANAGER(Const.InstructorPermissionRoleNames.MANAGER),

    /**
     * Observer.
     */
    INSTRUCTOR_PERMISSION_ROLE_OBSERVER(Const.InstructorPermissionRoleNames.OBSERVER),

    /**
     * Tutor.
     */
    INSTRUCTOR_PERMISSION_ROLE_TUTOR(Const.InstructorPermissionRoleNames.TUTOR),

    /**
     * Custom.
     */
    INSTRUCTOR_PERMISSION_ROLE_CUSTOM(Const.InstructorPermissionRoleNames.CUSTOM);

    private String roleName;

    InstructorPermissionRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * Get enum from string.
     */
    public static InstructorPermissionRole getEnum(String role) {
        switch (role) {
        case Const.InstructorPermissionRoleNames.COOWNER:
            return INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        case Const.InstructorPermissionRoleNames.MANAGER:
            return INSTRUCTOR_PERMISSION_ROLE_MANAGER;
        case Const.InstructorPermissionRoleNames.OBSERVER:
            return INSTRUCTOR_PERMISSION_ROLE_OBSERVER;
        case Const.InstructorPermissionRoleNames.TUTOR:
            return INSTRUCTOR_PERMISSION_ROLE_TUTOR;
        case Const.InstructorPermissionRoleNames.CUSTOM:
            return INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        default:
            return INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        }
    }
}

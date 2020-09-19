package teammates.ui.output;

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
    INSTRUCTOR_PERMISSION_ROLE_COOWNER(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER),

    /**
     * Manager.
     */
    INSTRUCTOR_PERMISSION_ROLE_MANAGER(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER),

    /**
     * Observer.
     */
    INSTRUCTOR_PERMISSION_ROLE_OBSERVER(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),

    /**
     * Tutor.
     */
    INSTRUCTOR_PERMISSION_ROLE_TUTOR(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR),

    /**
     * Custom.
     */
    INSTRUCTOR_PERMISSION_ROLE_CUSTOM(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);

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
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            return INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            return INSTRUCTOR_PERMISSION_ROLE_MANAGER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            return INSTRUCTOR_PERMISSION_ROLE_OBSERVER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            return INSTRUCTOR_PERMISSION_ROLE_TUTOR;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            return INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        default:
            return INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        }
    }
}

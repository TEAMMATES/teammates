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
    COOWNER(Const.InstructorPermissionRoleNames.COOWNER),

    /**
     * Manager.
     */
    MANAGER(Const.InstructorPermissionRoleNames.MANAGER),

    /**
     * Observer.
     */
    OBSERVER(Const.InstructorPermissionRoleNames.OBSERVER),

    /**
     * Tutor.
     */
    TUTOR(Const.InstructorPermissionRoleNames.TUTOR),

    /**
     * Custom.
     */
    CUSTOM(Const.InstructorPermissionRoleNames.CUSTOM);

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
            return COOWNER;
        case Const.InstructorPermissionRoleNames.MANAGER:
            return MANAGER;
        case Const.InstructorPermissionRoleNames.OBSERVER:
            return OBSERVER;
        case Const.InstructorPermissionRoleNames.TUTOR:
            return TUTOR;
        case Const.InstructorPermissionRoleNames.CUSTOM:
            return CUSTOM;
        default:
            return CUSTOM;
        }
    }
}

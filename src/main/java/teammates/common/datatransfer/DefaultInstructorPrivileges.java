package teammates.common.datatransfer;

import java.util.UUID;

import teammates.common.util.Const;

/**
 * Default privilege presets for instructor roles.
 */
public final class DefaultInstructorPrivileges {

    static final InstructorPermissionSet PRIVILEGES_MANAGER = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_OBSERVER = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_TUTOR = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_ALL = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_NONE = new InstructorPermissionSet();

    static {
        PRIVILEGES_MANAGER.setCanModifyCourse(false);
        PRIVILEGES_MANAGER.setCanModifyInstructor(true);
        PRIVILEGES_MANAGER.setCanModifySession(true);
        PRIVILEGES_MANAGER.setCanModifyStudent(true);
        PRIVILEGES_MANAGER.setCanViewSession(true);
        PRIVILEGES_MANAGER.setCanSubmitSession(true);

        PRIVILEGES_OBSERVER.setCanModifyCourse(false);
        PRIVILEGES_OBSERVER.setCanModifyInstructor(false);
        PRIVILEGES_OBSERVER.setCanModifySession(false);
        PRIVILEGES_OBSERVER.setCanModifyStudent(false);
        PRIVILEGES_OBSERVER.setCanViewSession(true);
        PRIVILEGES_OBSERVER.setCanSubmitSession(false);

        PRIVILEGES_TUTOR.setCanModifyCourse(false);
        PRIVILEGES_TUTOR.setCanModifyInstructor(false);
        PRIVILEGES_TUTOR.setCanModifySession(false);
        PRIVILEGES_TUTOR.setCanModifyStudent(false);
        PRIVILEGES_TUTOR.setCanViewSession(true);
        PRIVILEGES_TUTOR.setCanSubmitSession(true);

        PRIVILEGES_ALL.setCanModifyCourse(true);
        PRIVILEGES_ALL.setCanModifyInstructor(true);
        PRIVILEGES_ALL.setCanModifySession(true);
        PRIVILEGES_ALL.setCanModifyStudent(true);
        PRIVILEGES_ALL.setCanViewSession(true);
        PRIVILEGES_ALL.setCanSubmitSession(true);

        PRIVILEGES_NONE.setCanModifyCourse(false);
        PRIVILEGES_NONE.setCanModifyInstructor(false);
        PRIVILEGES_NONE.setCanModifySession(false);
        PRIVILEGES_NONE.setCanModifyStudent(false);
        PRIVILEGES_NONE.setCanViewSession(false);
        PRIVILEGES_NONE.setCanSubmitSession(false);
    }

    private DefaultInstructorPrivileges() {
        // Utility class.
    }

    /**
     * Returns default privileges for coowner role.
     */
    public static InstructorPrivileges buildDefaultPrivileges(UUID instructorId, String instrRole) {
        return new InstructorPrivileges(instructorId, instrRole);
    }

    /**
     * Returns default privileges for the given role.
     */
    public static InstructorPermissionSet getDefaultPrivileges(String instrRole) {
        return switch (instrRole) {
        case Const.InstructorPermissionRoleNames.COOWNER -> PRIVILEGES_ALL;
        case Const.InstructorPermissionRoleNames.MANAGER -> PRIVILEGES_MANAGER;
        case Const.InstructorPermissionRoleNames.OBSERVER -> PRIVILEGES_OBSERVER;
        case Const.InstructorPermissionRoleNames.TUTOR -> PRIVILEGES_TUTOR;
        case Const.InstructorPermissionRoleNames.CUSTOM -> PRIVILEGES_NONE;
        default -> throw new IllegalArgumentException("Invalid instructor role: " + instrRole);
        };
    }

}

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
        PRIVILEGES_MANAGER.setCanViewStudentInSections(true);
        PRIVILEGES_MANAGER.setCanViewSessionInSections(true);
        PRIVILEGES_MANAGER.setCanSubmitSessionInSections(true);
        PRIVILEGES_MANAGER.setCanModifySessionCommentsInSections(true);

        PRIVILEGES_OBSERVER.setCanModifyCourse(false);
        PRIVILEGES_OBSERVER.setCanModifyInstructor(false);
        PRIVILEGES_OBSERVER.setCanModifySession(false);
        PRIVILEGES_OBSERVER.setCanModifyStudent(false);
        PRIVILEGES_OBSERVER.setCanViewStudentInSections(true);
        PRIVILEGES_OBSERVER.setCanViewSessionInSections(true);
        PRIVILEGES_OBSERVER.setCanSubmitSessionInSections(false);
        PRIVILEGES_OBSERVER.setCanModifySessionCommentsInSections(false);

        PRIVILEGES_TUTOR.setCanModifyCourse(false);
        PRIVILEGES_TUTOR.setCanModifyInstructor(false);
        PRIVILEGES_TUTOR.setCanModifySession(false);
        PRIVILEGES_TUTOR.setCanModifyStudent(false);
        PRIVILEGES_TUTOR.setCanViewStudentInSections(true);
        PRIVILEGES_TUTOR.setCanViewSessionInSections(true);
        PRIVILEGES_TUTOR.setCanSubmitSessionInSections(true);
        PRIVILEGES_TUTOR.setCanModifySessionCommentsInSections(false);

        PRIVILEGES_ALL.setCanModifyCourse(true);
        PRIVILEGES_ALL.setCanModifyInstructor(true);
        PRIVILEGES_ALL.setCanModifySession(true);
        PRIVILEGES_ALL.setCanModifyStudent(true);
        PRIVILEGES_ALL.setCanViewStudentInSections(true);
        PRIVILEGES_ALL.setCanViewSessionInSections(true);
        PRIVILEGES_ALL.setCanSubmitSessionInSections(true);
        PRIVILEGES_ALL.setCanModifySessionCommentsInSections(true);

        PRIVILEGES_NONE.setCanModifyCourse(false);
        PRIVILEGES_NONE.setCanModifyInstructor(false);
        PRIVILEGES_NONE.setCanModifySession(false);
        PRIVILEGES_NONE.setCanModifyStudent(false);
        PRIVILEGES_NONE.setCanViewStudentInSections(false);
        PRIVILEGES_NONE.setCanViewSessionInSections(false);
        PRIVILEGES_NONE.setCanSubmitSessionInSections(false);
        PRIVILEGES_NONE.setCanModifySessionCommentsInSections(false);
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

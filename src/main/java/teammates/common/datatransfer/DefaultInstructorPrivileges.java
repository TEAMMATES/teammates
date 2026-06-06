package teammates.common.datatransfer;

/**
 * Default privilege presets for instructor roles.
 */
public final class DefaultInstructorPrivileges {

    static final InstructorPermissionSet PRIVILEGES_COOWNER = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_MANAGER = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_OBSERVER = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_TUTOR = new InstructorPermissionSet();
    static final InstructorPermissionSet PRIVILEGES_CUSTOM = new InstructorPermissionSet();

    static {
        PRIVILEGES_COOWNER.setCanModifyCourse(true);
        PRIVILEGES_COOWNER.setCanModifyInstructor(true);
        PRIVILEGES_COOWNER.setCanModifySession(true);
        PRIVILEGES_COOWNER.setCanModifyStudent(true);
        PRIVILEGES_COOWNER.setCanViewStudentInSections(true);
        PRIVILEGES_COOWNER.setCanViewSessionInSections(true);
        PRIVILEGES_COOWNER.setCanSubmitSessionInSections(true);
        PRIVILEGES_COOWNER.setCanModifySessionCommentsInSections(true);

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

        PRIVILEGES_CUSTOM.setCanModifyCourse(false);
        PRIVILEGES_CUSTOM.setCanModifyInstructor(false);
        PRIVILEGES_CUSTOM.setCanModifySession(false);
        PRIVILEGES_CUSTOM.setCanModifyStudent(false);
        PRIVILEGES_CUSTOM.setCanViewStudentInSections(false);
        PRIVILEGES_CUSTOM.setCanViewSessionInSections(false);
        PRIVILEGES_CUSTOM.setCanSubmitSessionInSections(false);
        PRIVILEGES_CUSTOM.setCanModifySessionCommentsInSections(false);
    }

    private DefaultInstructorPrivileges() {
        // Utility class.
    }

}

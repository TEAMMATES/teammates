package teammates.ui.output;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;

/**
 * The output format for privilege of an instructor.
 */
public class InstructorPrivilegeData extends ApiOutput {
    private boolean canModifyCourse;
    private boolean canModifySession;
    private boolean canModifyStudent;
    private boolean canModifyInstructor;

    private boolean canViewStudentInSections;

    private boolean canModifySessionCommentsInSections;
    private boolean canViewSessionInSections;
    private boolean canSubmitSessionInSections;

    public void setCanModifyCourse(boolean canModifyCourse) {
        this.canModifyCourse = canModifyCourse;
    }

    public void setCanModifySession(boolean canModifySession) {
        this.canModifySession = canModifySession;
    }

    public void setCanModifyStudent(boolean canModifyStudent) {
        this.canModifyStudent = canModifyStudent;
    }

    public void setCanModifyInstructor(boolean canModifyInstructor) {
        this.canModifyInstructor = canModifyInstructor;
    }

    public void setCanModifySessionCommentsInSections(boolean canModifySessionCommentsInSections) {
        this.canModifySessionCommentsInSections = canModifySessionCommentsInSections;
    }

    public void setCanViewSessionInSections(boolean canViewSessionInSections) {
        this.canViewSessionInSections = canViewSessionInSections;
    }

    public void setCanViewStudentInSections(boolean canViewStudentInSections) {
        this.canViewStudentInSections = canViewStudentInSections;
    }

    public void setCanSubmitSessionInSections(boolean canSubmitSessionInSections) {
        this.canSubmitSessionInSections = canSubmitSessionInSections;
    }

    public boolean isCanModifyCourse() {
        return canModifyCourse;
    }

    public boolean isCanModifyInstructor() {
        return canModifyInstructor;
    }

    public boolean isCanModifySession() {
        return canModifySession;
    }

    public boolean isCanModifySessionCommentsInSections() {
        return canModifySessionCommentsInSections;
    }

    public boolean isCanModifyStudent() {
        return canModifyStudent;
    }

    public boolean isCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public boolean isCanViewSessionInSections() {
        return canViewSessionInSections;
    }

    public boolean isCanViewStudentInSections() {
        return canViewStudentInSections;
    }

    /**
     * Constructs all privileges in course level.
     */
    public void constructCourseLevelPrivilege(InstructorPrivileges privileges) {
        setCanModifyInstructor(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        setCanModifyStudent(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        setCanModifySession(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        setCanModifyCourse(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        setCanViewStudentInSections(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        setCanViewSessionInSections(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        setCanSubmitSessionInSections(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        setCanModifySessionCommentsInSections(privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    /**
     * Constructs privileges in section level.
     */
    public void constructSectionLevelPrivilege(InstructorPrivileges privileges, String sectionName) {
        setCanViewStudentInSections(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        setCanViewSessionInSections(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        setCanSubmitSessionInSections(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        setCanModifySessionCommentsInSections(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    /**
     * Constructs privileges in session level.
     */
    public void constructSessionLevelPrivilege(InstructorPrivileges privileges, String sectionName,
                                               String sessionName) {
        setCanViewSessionInSections(privileges.isAllowedForPrivilege(
                sectionName, sessionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        setCanSubmitSessionInSections(privileges.isAllowedForPrivilege(
                sectionName, sessionName, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        setCanModifySessionCommentsInSections(privileges.isAllowedForPrivilege(
                sectionName, sessionName, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

}

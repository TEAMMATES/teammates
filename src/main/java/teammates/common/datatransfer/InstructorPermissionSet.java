package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.Map;

import teammates.common.util.Const;

/**
 * Represents a set of allowed action to be performed by an instructor.
 */
public class InstructorPermissionSet {

    private boolean canModifyCourse;
    private boolean canModifyInstructor;
    private boolean canModifySession;
    private boolean canModifyStudent;
    private boolean canViewStudentInSections;
    private boolean canViewSessionInSections;
    private boolean canSubmitSessionInSections;
    private boolean canModifySessionCommentsInSections;

    public boolean isCanModifyCourse() {
        return canModifyCourse;
    }

    public void setCanModifyCourse(boolean canModifyCourse) {
        this.canModifyCourse = canModifyCourse;
    }

    public boolean isCanModifyInstructor() {
        return canModifyInstructor;
    }

    public void setCanModifyInstructor(boolean canModifyInstructor) {
        this.canModifyInstructor = canModifyInstructor;
    }

    public boolean isCanModifySession() {
        return canModifySession;
    }

    public void setCanModifySession(boolean canModifySession) {
        this.canModifySession = canModifySession;
    }

    public boolean isCanModifyStudent() {
        return canModifyStudent;
    }

    public void setCanModifyStudent(boolean canModifyStudent) {
        this.canModifyStudent = canModifyStudent;
    }

    public boolean isCanViewStudentInSections() {
        return canViewStudentInSections;
    }

    public void setCanViewStudentInSections(boolean canViewStudentInSections) {
        this.canViewStudentInSections = canViewStudentInSections;
    }

    public boolean isCanViewSessionInSections() {
        return canViewSessionInSections;
    }

    public void setCanViewSessionInSections(boolean canViewSessionInSections) {
        this.canViewSessionInSections = canViewSessionInSections;
    }

    public boolean isCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public void setCanSubmitSessionInSections(boolean canSubmitSessionInSections) {
        this.canSubmitSessionInSections = canSubmitSessionInSections;
    }

    public boolean isCanModifySessionCommentsInSections() {
        return canModifySessionCommentsInSections;
    }

    public void setCanModifySessionCommentsInSections(boolean canModifySessionCommentsInSections) {
        this.canModifySessionCommentsInSections = canModifySessionCommentsInSections;
    }

    InstructorPermissionSet getCopy() {
        InstructorPermissionSet copy = new InstructorPermissionSet();
        copy.setCanModifyCourse(canModifyCourse);
        copy.setCanModifyInstructor(canModifyInstructor);
        copy.setCanModifySession(canModifySession);
        copy.setCanModifyStudent(canModifyStudent);
        copy.setCanViewStudentInSections(canViewStudentInSections);
        copy.setCanViewSessionInSections(canViewSessionInSections);
        copy.setCanSubmitSessionInSections(canSubmitSessionInSections);
        copy.setCanModifySessionCommentsInSections(canModifySessionCommentsInSections);
        return copy;
    }

    boolean get(String privilegeName) {
        switch (privilegeName) {
        case Const.InstructorPermissions.CAN_MODIFY_COURSE:
            return canModifyCourse;
        case Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR:
            return canModifyInstructor;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION:
            return canModifySession;
        case Const.InstructorPermissions.CAN_MODIFY_STUDENT:
            return canModifyStudent;
        case Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS:
            return canViewStudentInSections;
        case Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS:
            return canViewSessionInSections;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS:
            return canSubmitSessionInSections;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS:
            return canModifySessionCommentsInSections;
        default:
            return false;
        }
    }

    void put(String privilegeName, boolean value) {
        switch (privilegeName) {
        case Const.InstructorPermissions.CAN_MODIFY_COURSE:
            canModifyCourse = value;
            break;
        case Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR:
            canModifyInstructor = value;
            break;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION:
            canModifySession = value;
            break;
        case Const.InstructorPermissions.CAN_MODIFY_STUDENT:
            canModifyStudent = value;
            break;
        case Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS:
            canViewStudentInSections = value;
            break;
        case Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS:
            canViewSessionInSections = value;
            break;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS:
            canSubmitSessionInSections = value;
            break;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS:
            canModifySessionCommentsInSections = value;
            break;
        default:
            break;
        }
    }

    /**
     * Returns the legacy map representation of this permission set structure.
     */
    public Map<String, Boolean> toLegacyMapFormat() {
        Map<String, Boolean> legacyFormat = new HashMap<>();
        legacyFormat.put(Const.InstructorPermissions.CAN_MODIFY_COURSE, canModifyCourse);
        legacyFormat.put(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, canModifyInstructor);
        legacyFormat.put(Const.InstructorPermissions.CAN_MODIFY_SESSION, canModifySession);
        legacyFormat.put(Const.InstructorPermissions.CAN_MODIFY_STUDENT, canModifyStudent);
        legacyFormat.put(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, canViewStudentInSections);
        legacyFormat.put(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, canViewSessionInSections);
        legacyFormat.put(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, canSubmitSessionInSections);
        legacyFormat.put(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                canModifySessionCommentsInSections);
        return legacyFormat;
    }

    static InstructorPermissionSet fromLegacyMapFormat(Map<String, Boolean> legacyMap) {
        InstructorPermissionSet ips = new InstructorPermissionSet();
        ips.setCanModifyCourse(legacyMap.getOrDefault(Const.InstructorPermissions.CAN_MODIFY_COURSE, false));
        ips.setCanModifyInstructor(legacyMap.getOrDefault(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false));
        ips.setCanModifyStudent(legacyMap.getOrDefault(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false));
        ips.setCanModifySession(legacyMap.getOrDefault(Const.InstructorPermissions.CAN_MODIFY_SESSION, false));
        ips.setCanViewStudentInSections(
                legacyMap.getOrDefault(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false));
        ips.setCanViewSessionInSections(
                legacyMap.getOrDefault(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false));
        ips.setCanSubmitSessionInSections(
                legacyMap.getOrDefault(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, false));
        ips.setCanModifySessionCommentsInSections(
                legacyMap.getOrDefault(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, false));
        return ips;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InstructorPermissionSet)) {
            return false;
        }
        if (other == this) {
            return true;
        }

        InstructorPermissionSet rhs = (InstructorPermissionSet) other;
        return canModifyCourse == rhs.isCanModifyCourse()
                && canModifyInstructor == rhs.isCanModifyInstructor()
                && canModifySession == rhs.isCanModifySession()
                && canModifyStudent == rhs.isCanModifyStudent()
                && canViewStudentInSections == rhs.isCanViewStudentInSections()
                && canViewSessionInSections == rhs.isCanViewSessionInSections()
                && canSubmitSessionInSections == rhs.isCanSubmitSessionInSections()
                && canModifySessionCommentsInSections == rhs.isCanModifySessionCommentsInSections();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        result = prime * result + Boolean.hashCode(canModifyCourse);
        result = prime * result + Boolean.hashCode(canModifyInstructor);
        result = prime * result + Boolean.hashCode(canModifySession);
        result = prime * result + Boolean.hashCode(canModifyStudent);
        result = prime * result + Boolean.hashCode(canViewStudentInSections);
        result = prime * result + Boolean.hashCode(canViewSessionInSections);
        result = prime * result + Boolean.hashCode(canSubmitSessionInSections);
        result = prime * result + Boolean.hashCode(canModifySessionCommentsInSections);

        return result;
    }

}

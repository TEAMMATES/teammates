package teammates.common.datatransfer;

import java.util.Objects;

import teammates.common.util.Const;

/**
 * Represents a set of allowed action to be performed by an instructor.
 */
public class InstructorPermissionSet {

    private boolean canModifyCourse;
    private boolean canModifyInstructor;
    private boolean canModifySession;
    private boolean canModifyStudent;
    private boolean canViewStudent;
    private boolean canViewSession;
    private boolean canSubmitSession;
    private boolean canModifySessionComments;

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

    public boolean isCanViewStudent() {
        return canViewStudent;
    }

    public void setCanViewStudent(boolean canViewStudent) {
        this.canViewStudent = canViewStudent;
    }

    public boolean isCanViewSession() {
        return canViewSession;
    }

    public void setCanViewSession(boolean canViewSession) {
        this.canViewSession = canViewSession;
    }

    public boolean isCanSubmitSession() {
        return canSubmitSession;
    }

    public void setCanSubmitSession(boolean canSubmitSession) {
        this.canSubmitSession = canSubmitSession;
    }

    public boolean isCanModifySessionComments() {
        return canModifySessionComments;
    }

    public void setCanModifySessionComments(boolean canModifySessionComments) {
        this.canModifySessionComments = canModifySessionComments;
    }

    InstructorPermissionSet getCopy() {
        InstructorPermissionSet copy = new InstructorPermissionSet();
        copy.setCanModifyCourse(canModifyCourse);
        copy.setCanModifyInstructor(canModifyInstructor);
        copy.setCanModifySession(canModifySession);
        copy.setCanModifyStudent(canModifyStudent);
        copy.setCanViewStudent(canViewStudent);
        copy.setCanViewSession(canViewSession);
        copy.setCanSubmitSession(canSubmitSession);
        copy.setCanModifySessionComments(canModifySessionComments);
        return copy;
    }

    /**
     * Returns the value of the specified privilege. Returns false if the privilege name is invalid.
     */
    public boolean get(String privilegeName) {
        switch (privilegeName) {
        case Const.InstructorPermissions.CAN_MODIFY_COURSE:
            return canModifyCourse;
        case Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR:
            return canModifyInstructor;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION:
            return canModifySession;
        case Const.InstructorPermissions.CAN_MODIFY_STUDENT:
            return canModifyStudent;
        case Const.InstructorPermissions.CAN_VIEW_STUDENT:
            return canViewStudent;
        case Const.InstructorPermissions.CAN_VIEW_SESSION:
            return canViewSession;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION:
            return canSubmitSession;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT:
            return canModifySessionComments;
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
        case Const.InstructorPermissions.CAN_VIEW_STUDENT:
            canViewStudent = value;
            break;
        case Const.InstructorPermissions.CAN_VIEW_SESSION:
            canViewSession = value;
            break;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION:
            canSubmitSession = value;
            break;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT:
            canModifySessionComments = value;
            break;
        default:
            break;
        }
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
                && canViewStudent == rhs.isCanViewStudent()
                && canViewSession == rhs.isCanViewSession()
                && canSubmitSession == rhs.isCanSubmitSession()
                && canModifySessionComments == rhs.isCanModifySessionComments();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            canModifyCourse,
            canModifyInstructor,
            canModifySession,
            canModifyStudent,
            canViewStudent,
            canViewSession,
            canSubmitSession,
            canModifySessionComments
        );
    }

}

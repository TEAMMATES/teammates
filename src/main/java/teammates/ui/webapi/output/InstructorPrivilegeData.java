package teammates.ui.webapi.output;

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
}

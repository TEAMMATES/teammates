package teammates.ui.webapi.output;

/**
 * The output format for privilege of an instructor.
 */
public class InstructorPrivilegeData extends ApiOutput {
    private Boolean canModifyCourse;
    private Boolean canModifySession;
    private Boolean canModifyStudent;
    private Boolean canModifyInstructor;

    private Boolean canViewStudentInSections;

    private Boolean canModifySessionCommentsInSections;
    private Boolean canViewSessionInSections;
    private Boolean canSubmitSessionInSections;

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

    public Boolean isCanModifyCourse() {
        return canModifyCourse;
    }

    public Boolean isCanModifyInstructor() {
        return canModifyInstructor;
    }

    public Boolean isCanModifySession() {
        return canModifySession;
    }

    public Boolean isCanModifySessionCommentsInSections() {
        return canModifySessionCommentsInSections;
    }

    public Boolean isCanModifyStudent() {
        return canModifyStudent;
    }

    public Boolean isCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public Boolean isCanViewSessionInSections() {
        return canViewSessionInSections;
    }

    public Boolean isCanViewStudentInSections() {
        return canViewStudentInSections;
    }
}

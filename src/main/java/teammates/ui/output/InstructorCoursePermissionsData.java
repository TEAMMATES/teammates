package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The instructor permissions exposed for course responses.
 */
public class InstructorCoursePermissionsData implements ApiOutput {

    private final boolean canModifyCourse;
    private final boolean canModifyStudent;
    private final boolean canModifyInstructor;

    @JsonCreator
    public InstructorCoursePermissionsData(boolean canModifyCourse, boolean canModifyStudent,
            boolean canModifyInstructor) {
        this.canModifyCourse = canModifyCourse;
        this.canModifyStudent = canModifyStudent;
        this.canModifyInstructor = canModifyInstructor;
    }

    public boolean getCanModifyCourse() {
        return canModifyCourse;
    }

    public boolean getCanModifyStudent() {
        return canModifyStudent;
    }

    public boolean getCanModifyInstructor() {
        return canModifyInstructor;
    }
}

package teammates.ui.output;

import jakarta.annotation.Nullable;

/**
 * The API output format of a course view.
 */
public class CourseViewData implements ApiOutput {

    private final CourseData course;
    @Nullable
    private InstructorCoursePermissionsData instructorPermissions;

    public CourseViewData(CourseData course) {
        this.course = course;
    }

    public CourseViewData(CourseData course, @Nullable InstructorCoursePermissionsData instructorPermissions) {
        this.course = course;
        this.instructorPermissions = instructorPermissions;
    }

    public CourseData getCourse() {
        return course;
    }

    public InstructorCoursePermissionsData getInstructorPermissions() {
        return instructorPermissions;
    }

    public void setInstructorPermissions(InstructorCoursePermissionsData instructorPermissions) {
        this.instructorPermissions = instructorPermissions;
    }
}

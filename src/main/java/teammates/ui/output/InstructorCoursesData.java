package teammates.ui.output;

import java.util.List;
import java.util.Map;

import teammates.storage.entity.Course;

/**
 * The API output for a list of courses for an instructor.
 */
public class InstructorCoursesData implements ApiOutput {

    private final List<CourseData> courses;
    private final Map<String, InstructorCoursePermissionsData> instructorPermissions;

    public InstructorCoursesData(List<Course> coursesList, Map<String, InstructorCoursePermissionsData> permissionsByCourseId) {
        this.courses = coursesList.stream()
                .map(CourseData::new)
                .toList();
        this.instructorPermissions = permissionsByCourseId;
    }

    public List<CourseData> getCourses() {
        return courses;
    }

    public Map<String, InstructorCoursePermissionsData> getInstructorPermissions() {
        return instructorPermissions;
    }
}

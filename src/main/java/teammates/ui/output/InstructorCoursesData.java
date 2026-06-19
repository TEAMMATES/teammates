package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.storage.entity.Course;

/**
 * The API output for a list of courses for an instructor.
 */
public class InstructorCoursesData implements ApiOutput {

    private final List<CourseData> courses;
    private final Map<String, InstructorCoursePermissionsData> instructorPermissions;

    public InstructorCoursesData(Map<Course, InstructorPermissionSet> coursesWithPermissions) {
        this.courses = coursesWithPermissions.keySet().stream()
                .map(CourseData::new)
                .toList();
        this.instructorPermissions = coursesWithPermissions.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getId(),
                        e -> new InstructorCoursePermissionsData(
                                e.getValue().isCanModifyCourse(),
                                e.getValue().isCanModifyStudent(),
                                e.getValue().isCanModifyInstructor())));
    }

    public List<CourseData> getCourses() {
        return courses;
    }

    public Map<String, InstructorCoursePermissionsData> getInstructorPermissions() {
        return instructorPermissions;
    }
}

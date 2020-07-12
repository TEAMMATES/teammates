package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: for an instructor to get his/her list of courses.
 */
public class InstructorGetCoursesAction extends Action {
    // TODO: Write tests

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {

        Map<String, InstructorAttributes> courseInstructor = new HashMap<>();

        List<CourseAttributes> courses = logic.getCoursesForInstructor(userInfo.id);

        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(userInfo.id);
        for (InstructorAttributes instructor : instructorList) {
            courseInstructor.put(instructor.courseId, instructor);
        }

        List<CourseDetails> coursesToDisplay = new ArrayList<>();
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = courseInstructor.get(course.getId());
            boolean isInstructorAllowedToModify = instructor.isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

            coursesToDisplay.add(new CourseDetails(
                    course.getId(), course.getName(), instructor.isArchived, isInstructorAllowedToModify));
        }

        InstructorGetCoursesResult result = new InstructorGetCoursesResult(coursesToDisplay);

        return new JsonResult(result);
    }

    /**
     * A data model containing details of a course for some instructor.
     */
    public static class CourseDetails {

        private final String id;
        private final String name;
        private final boolean isArchived;
        private final boolean isInstructorAllowedToModify;

        public CourseDetails(String id, String name, boolean isArchived, boolean isInstructorAllowedToModify) {
            this.id = id;
            this.name = name;
            this.isArchived = isArchived;
            this.isInstructorAllowedToModify = isInstructorAllowedToModify;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isArchived() {
            return isArchived;
        }

        public boolean isInstructorAllowedToModify() {
            return isInstructorAllowedToModify;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CourseDetails that = (CourseDetails) o;
            return isArchived == that.isArchived
                    && isInstructorAllowedToModify == that.isInstructorAllowedToModify
                    && Objects.equals(id, that.id)
                    && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(id, name, isArchived, isInstructorAllowedToModify);
        }
    }

    /**
     * Output format for {@link InstructorGetCoursesAction}.
     */
    public static class InstructorGetCoursesResult extends ApiOutput {

        private final List<CourseDetails> courses;

        public InstructorGetCoursesResult(List<CourseDetails> courses) {
            this.courses = courses;

            this.courses.sort(Comparator.comparing(CourseDetails::getId));
        }

        public List<CourseDetails> getCourses() {
            return courses;
        }
    }
}

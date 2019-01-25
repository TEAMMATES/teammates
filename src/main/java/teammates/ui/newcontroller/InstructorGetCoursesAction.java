package teammates.ui.newcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: for an instructor to get his/her list of courses.
 */
public class InstructorGetCoursesAction extends Action {

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

        Boolean isDisplayArchive = getBooleanRequestParamValue(Const.ParamsNames.DISPLAY_ARCHIVE);
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

            boolean isCourseDisplayed = isDisplayArchive || !instructor.isArchived;
            if (isCourseDisplayed) {
                coursesToDisplay.add(new CourseDetails(
                        course.getId(), course.getName(), instructor.isArchived, isInstructorAllowedToModify));
            }
        }

        InstructorGetCoursesResult result = new InstructorGetCoursesResult(isDisplayArchive, coursesToDisplay);

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
    }

    /**
     * Output format for {@link InstructorGetCoursesAction}.
     */
    public static class InstructorGetCoursesResult extends ActionResult.ActionOutput {

        private final boolean isDisplayArchive;
        private final List<CourseDetails> courses;

        public InstructorGetCoursesResult(boolean isDisplayArchive, List<CourseDetails> courses) {
            this.isDisplayArchive = isDisplayArchive;
            this.courses = courses;

            courses.sort((c1, c2) -> c1.getId().compareTo(c2.getId()));
        }

        public boolean isDisplayArchive() {
            return isDisplayArchive;
        }

        public List<CourseDetails> getCourses() {
            return courses;
        }
    }
}

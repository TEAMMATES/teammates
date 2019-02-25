package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets all courses (active, archived and soft-deleted courses) for the instructor.
 */
public class GetInstructorCoursesAction extends Action {

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

        Map<String, InstructorAttributes> instructorsForCourses = new HashMap<>();

        List<CourseAttributes> activeCourses = new ArrayList<>();
        List<CourseAttributes> archivedCourses = new ArrayList<>();

        // Get list of InstructorAttributes that belong to the user.
        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(userInfo.id);
        for (InstructorAttributes instructor : instructorList) {
            instructorsForCourses.put(instructor.courseId, instructor);
        }

        // Get corresponding courses of the instructors.
        List<CourseAttributes> allCourses = logic.getCoursesForInstructor(instructorList);
        List<CourseAttributes> softDeletedCourses = logic.getSoftDeletedCoursesForInstructors(instructorList);

        List<String> archivedCourseIds = logic.getArchivedCourseIds(allCourses, instructorsForCourses);
        for (CourseAttributes course : allCourses) {
            if (archivedCourseIds.contains(course.getId())) {
                archivedCourses.add(course);
            } else {
                activeCourses.add(course);
            }
        }

        // Sort CourseDetailsBundle lists by course id
        CourseAttributes.sortById(activeCourses);
        CourseAttributes.sortById(archivedCourses);
        CourseAttributes.sortById(softDeletedCourses);

        InstructorCourses output = new InstructorCourses(activeCourses, archivedCourses, softDeletedCourses, instructorList);
        return new JsonResult(output);

    }

    /**
     * Output format for {@link GetInstructorCoursesAction}.
     */
    public static class InstructorCourses extends ApiOutput {

        private final List<CourseAttributes> activeCourses;
        private final List<CourseAttributes> archivedCourses;
        private final List<CourseAttributes> softDeletedCourses;
        private final List<InstructorAttributes> instructorList;

        public InstructorCourses(List<CourseAttributes> activeCourses, List<CourseAttributes> archivedCourses,
                                 List<CourseAttributes> softDeletedCourses, List<InstructorAttributes> instructorList) {
            this.activeCourses = activeCourses;
            this.archivedCourses = archivedCourses;
            this.softDeletedCourses = softDeletedCourses;
            this.instructorList = instructorList;
        }

        public List<CourseAttributes> getActiveCourses() {
            return activeCourses;
        }

        public List<CourseAttributes> getArchivedCourses() {
            return archivedCourses;
        }

        public List<CourseAttributes> getSoftDeletedCourses() {
            return softDeletedCourses;
        }

        public List<InstructorAttributes> getInstructorList() {
            return instructorList;
        }
    }
}

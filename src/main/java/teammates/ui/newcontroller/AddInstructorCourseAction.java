package teammates.ui.newcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Adds a new course for instructor.
 */
public class AddInstructorCourseAction extends Action {

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
        String newCourseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String newCourseName = getNonNullRequestParamValue(Const.ParamsNames.COURSE_NAME);
        String newCourseTimeZone = getNonNullRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);

        try {
            logic.createCourseAndInstructor(userInfo.id, newCourseId, newCourseName, newCourseTimeZone);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        /* Prepare data for the refreshed page after executing the adding action */
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

        GetInstructorCoursesAction.InstructorCourses output
                = new GetInstructorCoursesAction.InstructorCourses(activeCourses, archivedCourses, softDeletedCourses);
        return new JsonResult(output);
    }
}

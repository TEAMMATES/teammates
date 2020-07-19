package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.CourseData;
import teammates.ui.webapi.output.CoursesData;

/**
 * Gets all courses for the instructor, and filtered by active, archived and soft-deleted.
 * Or gets all courses for the student he belongs to.
 */
public class GetCoursesAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!((entityType.equals(Const.EntityType.STUDENT) && userInfo.isStudent)
                || (entityType.equals(Const.EntityType.INSTRUCTOR) && userInfo.isInstructor))) {
            throw new UnauthorizedAccessException("Current account cannot access to courses of request entity type");
        }
    }

    @Override
    public ActionResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentCourses();
        case Const.EntityType.INSTRUCTOR:
            return getInstructorCourses();
        default:
            return new JsonResult("Error: invalid entity type", HttpStatus.SC_BAD_REQUEST);

        }
    }

    private ActionResult getStudentCourses() {
        List<CourseAttributes> courses = logic.getCoursesForStudentAccount(userInfo.id);
        CoursesData coursesData = new CoursesData(courses);
        coursesData.getCourses().forEach(CourseData::hideInformationForStudent);
        return new JsonResult(coursesData);
    }

    private ActionResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);
        List<CourseAttributes> courses;
        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            courses = getActiveCourse();
            break;
        case Const.CourseStatus.ARCHIVED:
            courses = getArchivedCourse();
            break;
        case Const.CourseStatus.SOFT_DELETED:
            courses = getSoftDeletedCourse();
            break;
        default:
            return new JsonResult("Error: invalid course status", HttpStatus.SC_BAD_REQUEST);
        }

        CourseAttributes.sortById(courses);
        return new JsonResult(new CoursesData(courses));

    }

    private List<CourseAttributes> getActiveCourse() {
        return logic.getCoursesForInstructor(userInfo.id, true);
    }

    private List<CourseAttributes> getArchivedCourse() {

        List<InstructorAttributes> allInstructors = logic.getInstructorsForGoogleId(userInfo.id, false);
        List<InstructorAttributes> archivedInstructors = new ArrayList<>();
        for (InstructorAttributes instructor : allInstructors) {
            if (instructor.isArchived) {
                archivedInstructors.add(instructor);
            }
        }

        return logic.getCoursesForInstructor(archivedInstructors);
    }

    private List<CourseAttributes> getSoftDeletedCourse() {
        return logic.getSoftDeletedCoursesForInstructors(logic.getInstructorsForGoogleId(userInfo.id));
    }
}

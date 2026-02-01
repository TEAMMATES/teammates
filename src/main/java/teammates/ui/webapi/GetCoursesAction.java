package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.util.Const;
import teammates.sqllogic.core.CoursesLogic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;

/**
 * Gets all courses for the instructor, and filtered by active, archived and
 * soft-deleted.
 * Or gets all courses for the student he belongs to.
 */
public class GetCoursesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(Const.EntityType.STUDENT.equals(entityType) && userInfo.isStudent)
                && !(Const.EntityType.INSTRUCTOR.equals(entityType) && userInfo.isInstructor)) {
            throw new UnauthorizedAccessException("Current account cannot access to courses of request entity type");
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentCourses();
        case Const.EntityType.INSTRUCTOR:
            return getInstructorCourses();
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult getStudentCourses() {
        List<Course> sqlCourses = sqlLogic.getCoursesForStudentAccount(userInfo.id);

        CoursesData coursesData = new CoursesData(sqlCourses);

        coursesData.getCourses().forEach(CourseData::hideInformationForStudent);
        return new JsonResult(coursesData);
    }

    private JsonResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);

        List<Instructor> sqlInstructors = new ArrayList<>();
        List<Course> sqlCourses = new ArrayList<>();

        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
            sqlCourses = sqlLogic.getCoursesForInstructors(sqlInstructors);

            break;
        // TODO: Either implement archived functionality or remove this whole branch
        case Const.CourseStatus.ARCHIVED:
            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
            sqlCourses = sqlLogic.getCoursesForInstructors(sqlInstructors);

            break;
        case Const.CourseStatus.SOFT_DELETED:
            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
            sqlCourses = sqlLogic.getSoftDeletedCoursesForInstructors(sqlInstructors);

            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid course status");
        }

        Map<String, Instructor> sqlCourseIdToInstructor = new HashMap<>();
        sqlInstructors.forEach(instructor -> sqlCourseIdToInstructor.put(instructor.getCourseId(), instructor));

        CoursesLogic.sortById(sqlCourses);

        CoursesData coursesData = new CoursesData(sqlCourses);

        coursesData.getCourses().forEach(courseData -> {
            Instructor instructor = sqlCourseIdToInstructor.get(courseData.getCourseId());
            if (instructor == null) {
                return;
            }
            InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
            courseData.setPrivileges(privilege);
        });

        return new JsonResult(coursesData);
    }
}

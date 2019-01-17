package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: deletes all students from a course.
 */
public class DeleteInstructorCourseAllStudentsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (userInfo.isInstructor) {
            String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            return;
        }
        throw new UnauthorizedAccessException("Instructor privilege is required to delete students from course.");
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteAllStudentsInCourse(courseId);

        return new JsonResult("All the students have been removed from the course", HttpStatus.SC_OK);
    }
}

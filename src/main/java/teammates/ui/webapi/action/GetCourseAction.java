package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.CourseData;

/**
 * Get a course for an instructor or student.
 */
public class GetCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        boolean checkForInstructorAccess = logic.getInstructorForGoogleId(courseId, userInfo.getId()) != null;

        if (userInfo.isInstructor && checkForInstructorAccess) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    logic.getCourse(courseId));
            return;
        }

        boolean checkForStudentAccess = logic.getStudentForGoogleId(courseId, userInfo.getId()) != null;

        if (userInfo.isStudent && checkForStudentAccess) {
            CourseAttributes course = logic.getCourse(courseId);
            gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.id), course);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes courseAttributes = logic.getCourse(courseId);
        if (courseAttributes == null) {
            return new JsonResult("No course with id: " + courseId, HttpStatus.SC_NOT_FOUND);
        }
        return new JsonResult(new CourseData(courseAttributes));
    }
}

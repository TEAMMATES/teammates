package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Save edited course details.
 */
public class SaveCourseEditDetailsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        CourseAttributes course = logic.getCourse(courseId);
        gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String courseName = getNonNullRequestParamValue(Const.ParamsNames.COURSE_NAME);
        String courseTimeZone = getNonNullRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);

        try {
            logic.updateCourse(courseId, courseName, courseTimeZone);
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException edee) {
            return new JsonResult(edee.getMessage(), HttpStatus.SC_NOT_FOUND);
        }

        return new JsonResult("Updated course [" + courseId + "] details: Name: " + courseName
                + ", Time zone: " + courseTimeZone, HttpStatus.SC_OK);
    }
}

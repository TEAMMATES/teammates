package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.output.CourseData;

/**
 * Get the detail of a course.
 */
public class GetCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                logic.getCourse(courseId));
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

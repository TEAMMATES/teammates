package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class GetCourseStatsAction extends Action {

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

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(Const.ParamsNames.COURSE_ID, courseId);

        CourseDetailsBundle courseDetails;
        try {
            courseDetails = logic.getCourseDetails(courseId);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given id.", HttpStatus.SC_NOT_FOUND);
        }

        CourseDetails output = new CourseDetails(courseDetails);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetCourseStatsAction}.
     */
    public static class CourseDetails extends ActionResult.ActionOutput {

        private final CourseDetailsBundle courseDetails;

        public CourseDetails(CourseDetailsBundle courseDetails) {
            this.courseDetails = courseDetails;
        }

        public CourseDetailsBundle getCourseDetails() {
            return courseDetails;
        }
    }

}

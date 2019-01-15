package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;
import teammates.common.util.Const;

/**
 * Action: Checks if a course is present based on its courseid.
 */
public class GetCoursePresentAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Anyone can check the status of a course
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseInfo dataFormat = new CourseInfo(logic.isCoursePresent(courseId));
        if (!dataFormat.isCoursePresent) {
            return new JsonResult("Invalid course", HttpStatus.SC_NOT_FOUND);
        }
        return new JsonResult(dataFormat);
    }

    /**
     * Output format for {@link GetCoursePresentAction}.
     */
    public static class CourseInfo extends ActionResult.ActionOutput {

        private final boolean isCoursePresent;

        public CourseInfo(boolean isCoursePresent) {
            this.isCoursePresent = isCoursePresent;
        }

        public boolean isCoursePresent() {
            return isCoursePresent;
        }
    }

}

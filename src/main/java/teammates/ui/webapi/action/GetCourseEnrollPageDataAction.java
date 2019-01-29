package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: Fetches course enroll page data.
 */
public class GetCourseEnrollPageDataAction extends Action {

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
        String hasExistingResponsesMessage = "";
        StatusMessageColor statusMessageColor = null;
        if (logic.hasResponsesForCourse(courseId)) {
            hasExistingResponsesMessage =
                    Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS;
            statusMessageColor = StatusMessageColor.WARNING;
        }

        // We should only set statusMessageColor.WARNING if there is an existing response
        CourseEnrollPageData dataFormat =
                new CourseEnrollPageData(
                        logic.isCoursePresent(courseId),
                        new StatusMessage(hasExistingResponsesMessage, statusMessageColor));
        if (!dataFormat.isCoursePresent) {
            return new JsonResult("Invalid course", HttpStatus.SC_NOT_FOUND);
        }
        return new JsonResult(dataFormat);
    }

    /**
     * Output format for {@link GetCourseEnrollPageDataAction}.
     */
    public static class CourseEnrollPageData extends ApiOutput {

        private final boolean isCoursePresent;
        private final StatusMessage statusMessage;

        public CourseEnrollPageData(boolean isCoursePresent, StatusMessage statusMessage) {
            this.isCoursePresent = isCoursePresent;
            this.statusMessage = statusMessage;
        }

        public boolean isCoursePresent() {
            return isCoursePresent;
        }

        public StatusMessage getStatusMessage() {
            return statusMessage;
        }
    }
}

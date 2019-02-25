package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;

/**
 * Get the information of an instructor inside a course.
 */
public class GetInstructorAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            // TODO implement this when necessary
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            return new JsonResult(new InstructorInfo.InstructorResponse(instructorAttributes));
        case FULL_DETAIL:
            // TODO implement this when necessary
            return null;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}

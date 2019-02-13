package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackSessionStudentsResponseData;

/**
 * Get students submission response status about the feedback session.
 */
public class GetFeedbackSessionStudentResponseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        gateKeeper.verifyAccessible(instructor, fsa);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionResponseStatus fsResponseStatus;
        try {
            fsResponseStatus = logic.getFeedbackSessionResponseStatus(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No session with given feedback session name and course id.",
                    HttpStatus.SC_NOT_FOUND);
        }

        return new JsonResult(new FeedbackSessionStudentsResponseData(fsResponseStatus));
    }
}

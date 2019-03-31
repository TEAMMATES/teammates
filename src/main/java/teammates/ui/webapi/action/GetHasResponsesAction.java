package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.output.HasResponsesData;

/**
 * Checks whether a course or question has responses.
 */
public class GetHasResponsesAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        //Only an instructor of the feedback session can check responses for questions within it.
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (questionId != null) {
            FeedbackQuestionAttributes feedbackQuestionAttributes = logic.getFeedbackQuestion(questionId);
            FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(
                    feedbackQuestionAttributes.getFeedbackSessionName(),
                    feedbackQuestionAttributes.getCourseId());

            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackQuestionAttributes.getCourseId(), userInfo.getId()),
                    feedbackSession);

            //prefer question check over course checks
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (courseId != null) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    logic.getCourse(courseId));
        }
    }

    @Override
    public ActionResult execute() {
        String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionID != null) {
            if (logic.getFeedbackQuestion(feedbackQuestionID) == null) {
                return new JsonResult("No feedback question with id: " + feedbackQuestionID, HttpStatus.SC_NOT_FOUND);
            }

            boolean hasResponses = logic.areThereResponsesForQuestion(feedbackQuestionID);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (logic.getCourse(courseId) == null) {
            return new JsonResult("No course with id: " + courseId, HttpStatus.SC_NOT_FOUND);
        }

        boolean hasResponses = logic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }
}

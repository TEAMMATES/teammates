package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes a feedback question.
 */
public class DeleteFeedbackQuestionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes == null) {
            throw new UnauthorizedAccessException("Unknown question ID");
        }

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

    }

    @Override
    public JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        logic.deleteFeedbackQuestionCascade(feedbackQuestionId);

        return new JsonResult("Feedback question deleted!");
    }

}

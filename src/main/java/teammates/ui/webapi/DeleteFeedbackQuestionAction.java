package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes a feedback question.
 */
class DeleteFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes == null) {
            throw new UnauthorizedAccessException("Unknown question ID");
        }

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);

    }

    @Override
    JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        logic.deleteFeedbackQuestionCascade(feedbackQuestionId);

        return new JsonResult("Feedback question deleted!");
    }

}

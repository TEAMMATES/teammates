package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Deletes a feedback question.
 */
public class DeleteFeedbackQuestionAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        gateKeeper.verifyInstructorHasPrivilegeInFeedbackQuestion(requestContext, questionId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);

    }

    @Override
    public JsonResult execute() {
        UUID questionId = null;
        FeedbackQuestion question = null;

        questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        question = logic.getFeedbackQuestion(questionId);

        JsonResult successfulJsonResult = new JsonResult("Feedback question deleted!");

        if (question == null) {
            return successfulJsonResult;
        }

        logic.deleteFeedbackQuestionCascade(questionId);

        return successfulJsonResult;
    }

}

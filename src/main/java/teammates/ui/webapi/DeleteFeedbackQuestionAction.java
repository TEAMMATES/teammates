package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
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
        UUID questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        logic.deleteFeedbackQuestionCascade(questionId);

        return new JsonResult("Feedback question deleted!");
    }

}

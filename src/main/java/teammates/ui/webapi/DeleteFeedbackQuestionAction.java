package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * Deletes a feedback question.
 */
public class DeleteFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID questionId;
        FeedbackQuestion question = null;

        questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        question = sqlLogic.getFeedbackQuestion(questionId);

        if (question == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(question.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(question.getFeedbackSession().getName(), question.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);

    }

    @Override
    public JsonResult execute() {
        UUID questionId = null;
        FeedbackQuestion question = null;

        questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        question = sqlLogic.getFeedbackQuestion(questionId);

        JsonResult successfulJsonResult = new JsonResult("Feedback question deleted!");

        if (question == null) {
            return successfulJsonResult;
        }

        sqlLogic.deleteFeedbackQuestionCascade(questionId);

        return successfulJsonResult;
    }

}

package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
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
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        UUID questionId;
        FeedbackQuestionAttributes questionAttributes = null;
        FeedbackQuestion question = null;
        String courseId;

        try {
            questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            question = sqlLogic.getFeedbackQuestion(questionId);
        } catch (InvalidHttpParameterException e) {
            questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (questionAttributes != null) {
            courseId = questionAttributes.getCourseId();
        } else if (question != null) {
            courseId = question.getCourseId();
        } else {
            throw new EntityNotFoundException("Unknown question id");
        }

        if (!isCourseMigrated(courseId)) {
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                    getNonNullFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
            return;
        }

        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(question.getCourseId(), userInfo.getId()),
                getNonNullSqlFeedbackSession(question.getFeedbackSession().getName(), question.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);

    }

    @Override
    public JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        UUID questionId = null;
        FeedbackQuestionAttributes questionAttributes = null;
        FeedbackQuestion question = null;
        String courseId;

        try {
            questionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            question = sqlLogic.getFeedbackQuestion(questionId);
        } catch (InvalidHttpParameterException e) {
            questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        JsonResult successfulJsonResult = new JsonResult("Feedback question deleted!");

        if (questionAttributes != null) {
            courseId = questionAttributes.getCourseId();
        } else if (question != null) {
            courseId = question.getCourseId();
        } else {
            return successfulJsonResult;
        }

        if (!isCourseMigrated(courseId)) {
            logic.deleteFeedbackQuestionCascade(feedbackQuestionId);
            return successfulJsonResult;
        }

        sqlLogic.deleteFeedbackQuestionCascade(questionId);

        return successfulJsonResult;
    }

}

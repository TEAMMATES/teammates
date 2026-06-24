package teammates.ui.webapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionsData;

/**
 * Get a list of feedback questions for a feedback session.
 */
public class GetFeedbackQuestionsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        gateKeeper.verifyInstructorInFeedbackSession(requestContext, feedbackSessionId);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        List<FeedbackQuestion> questions = logic.getFeedbackQuestionsForSession(feedbackSessionId);

        List<FeedbackQuestionData> questionDatas = questions.stream()
                .map(question -> new FeedbackQuestionData(question, Optional.empty()))
                .toList();

        FeedbackQuestionsData response = new FeedbackQuestionsData(questionDatas);
        response.normalizeQuestionNumber();

        return new JsonResult(response);
    }
}

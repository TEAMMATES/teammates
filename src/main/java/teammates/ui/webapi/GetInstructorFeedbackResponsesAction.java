package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponsesData;

/**
 * Get all responses given by an instructor for a question.
 */
public class GetInstructorFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), false);
        checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Instructor instructor = getInstructorOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
        List<FeedbackResponse> responses = logic.getFeedbackResponsesFromInstructorForQuestion(
                feedbackQuestion, instructor);

        FeedbackResponsesData result = FeedbackResponsesData.createFromEntity(responses);
        return new JsonResult(result);
    }
}

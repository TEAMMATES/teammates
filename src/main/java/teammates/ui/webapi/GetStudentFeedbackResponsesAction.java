package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.SessionKeyType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponsesData;

/**
 * Get all responses given by a student or the student's team for a question.
 */
public class GetStudentFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        gateKeeper.verifySessionKey(requestContext, feedbackSession.getId(), SessionKeyType.SUBMISSION);
        Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), false);
        checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Student student = getStudentOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
        List<FeedbackResponse> responses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                feedbackQuestion, student);

        FeedbackResponsesData result = FeedbackResponsesData.createFromEntity(responses);
        return new JsonResult(result);
    }
}

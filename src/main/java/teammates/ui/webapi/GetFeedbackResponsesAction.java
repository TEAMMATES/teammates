package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.Intent;

/**
 * Get all responses given by the user for a question.
 */
public class GetFeedbackResponsesAction extends BasicFeedbackSubmissionAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);

        Intent intent = getEnumRequestParamValue(Const.ParamsNames.INTENT, Intent.class);
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), false);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), false);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        Intent intent = getEnumRequestParamValue(Const.ParamsNames.INTENT, Intent.class);

        List<FeedbackResponse> responses;
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            responses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            responses = logic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructor);
            break;
        default:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        }

        FeedbackResponsesData result = FeedbackResponsesData.createFromEntity(responses);
        return new JsonResult(result);
    }

}

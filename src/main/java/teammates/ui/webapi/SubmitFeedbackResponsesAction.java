package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Submits a list of feedback responses to a feedback question.
 *
 * <p>This action is meant to completely overwrite the feedback responses that are previously attached to the
 * same feedback question.
 */
public class SubmitFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        final FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();
        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            Student student = getStudentOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            verifySessionOpenExceptForModeration(feedbackSession, student);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            verifySessionOpenExceptForModeration(feedbackSession, instructor);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT, STUDENT_RESULT:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        final FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        List<FeedbackResponse> output;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            log.info("Student " + student.getId() + " is submitting feedback responses for question "
                    + feedbackQuestion.getId() + " in session " + feedbackQuestion.getFeedbackSession().getId());
            output = logic.submitFeedbackResponsesFromStudent(feedbackQuestion, student, submitRequest);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackQuestion.getCourseId(), false);
            log.info("Instructor " + instructor.getId() + " is submitting feedback responses for question "
                    + feedbackQuestion.getId() + " in session " + feedbackQuestion.getFeedbackSession().getId());
            output = logic.submitFeedbackResponsesFromInstructor(feedbackQuestion, instructor, submitRequest);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(FeedbackResponsesData.createFromEntity(output));
    }

}

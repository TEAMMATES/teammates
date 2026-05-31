package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
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
 * Submits feedback responses for one or more feedback questions in a feedback session.
 *
 * <p>For each submitted question, the submitted responses are treated as the complete final set from the giver.
 * Any existing responses not included in the submission will be removed.
 */
public class SubmitFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("The feedback session does not exist.");
        }

        if (!StringHelper.isEmpty(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON))) {
            validateModerationVisibilityForSubmittedQuestions(feedbackSession,
                    getAndValidateRequestBody(FeedbackResponsesRequest.class));
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), false);
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            verifySessionOpenExceptForModeration(feedbackSession, student);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), false);
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
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("The feedback session does not exist.");
        }

        List<FeedbackResponse> output;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(feedbackSession.getCourseId(), false);
            log.info("Student " + student.getId() + " is submitting feedback responses for "
                    + submitRequest.getQuestionResponses().size() + " question(s) in session "
                    + feedbackSession.getId());
            try {
                output = logic.submitFeedbackResponsesFromStudent(feedbackSession, student, submitRequest);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e);
            }
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseForSubmission(feedbackSession.getCourseId(), false);
            log.info("Instructor " + instructor.getId() + " is submitting feedback responses for "
                    + submitRequest.getQuestionResponses().size() + " question(s) in session "
                    + feedbackSession.getId());
            try {
                output = logic.submitFeedbackResponsesFromInstructor(feedbackSession, instructor, submitRequest);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e);
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(FeedbackResponsesData.createFromEntity(output));
    }

    private void validateModerationVisibilityForSubmittedQuestions(
            FeedbackSession feedbackSession, FeedbackResponsesRequest submitRequest)
            throws UnauthorizedAccessException {
        for (UUID questionId : submitRequest.getQuestionResponses().keySet()) {
            FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(questionId);
            if (feedbackQuestion == null) {
                throw new EntityNotFoundException("The feedback question does not exist.");
            }
            if (!feedbackQuestion.getFeedbackSession().getId().equals(feedbackSession.getId())) {
                throw new InvalidHttpParameterException("The feedback question does not belong to the feedback session.");
            }
            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        }
    }

}

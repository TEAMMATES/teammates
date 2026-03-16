package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.Intent;

/**
 * Get all the comments given by the user for a response.
 */
public class GetFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackResponseIdParam =
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        UUID feedbackResponseId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                feedbackResponseIdParam);

        FeedbackResponse feedbackResponse = null;

        feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseId);

        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();
        FeedbackSession feedbackSession =
                getNonNullFeedbackSession(feedbackQuestion.getFeedbackSession().getName(),
                        courseId);

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String feedbackResponseIdParam =
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        UUID feedbackResponseId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                feedbackResponseIdParam);

        FeedbackResponse feedbackResponse = null;

        feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseId);

        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
        case INSTRUCTOR_SUBMISSION:
            FeedbackResponseComment comment =
                    sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
            if (comment == null) {
                FeedbackResponse fr = sqlLogic.getFeedbackResponse(feedbackResponseId);
                if (fr == null) {
                    throw new EntityNotFoundException("The feedback response does not exist.");
                }
                return new JsonResult("Comment not found", HttpStatus.SC_NO_CONTENT);
            }
            return new JsonResult(new FeedbackResponseCommentData(comment));
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }
}

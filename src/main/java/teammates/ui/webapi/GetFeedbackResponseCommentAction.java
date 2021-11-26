package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.Intent;

/**
 * Get all the comments given by the user for a response.
 */
class GetFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }
        FeedbackResponseAttributes feedbackResponseAttributes = logic.getFeedbackResponse(feedbackResponseId);

        if (feedbackResponseAttributes == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }
        String courseId = feedbackResponseAttributes.getCourseId();
        FeedbackSessionAttributes feedbackSession =
                getNonNullFeedbackSession(feedbackResponseAttributes.getFeedbackSessionName(),
                        feedbackResponseAttributes.getCourseId());
        FeedbackQuestionAttributes feedbackQuestion =
                logic.getFeedbackQuestion(feedbackResponseAttributes.getFeedbackQuestionId());

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            StudentAttributes student = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructor = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
        case INSTRUCTOR_SUBMISSION:
            FeedbackResponseCommentAttributes comment =
                    logic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
            if (comment == null) {
                FeedbackResponseAttributes fr = logic.getFeedbackResponse(feedbackResponseId);
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

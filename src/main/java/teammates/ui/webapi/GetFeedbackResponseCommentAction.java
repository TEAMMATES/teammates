package teammates.ui.webapi;

import java.util.UUID;

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
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        FeedbackResponseAttributes feedbackResponseAttributes = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        UUID feedbackResponseSqlId;

        try {
            feedbackResponseSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackResponseAttributes = logic.getFeedbackResponse(feedbackResponseId);
        }

        if (feedbackResponseAttributes != null) {
            courseId = feedbackResponseAttributes.getCourseId();
        } else if (feedbackResponse != null) {
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        if (!isCourseMigrated(courseId)) {
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
            return;
        }

        FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();
        FeedbackSession feedbackSession =
                getNonNullSqlFeedbackSession(feedbackQuestion.getFeedbackSession().getName(),
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
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        FeedbackResponseAttributes feedbackResponseAttributes = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        UUID feedbackResponseSqlId = null;

        try {
            feedbackResponseSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackResponseAttributes = logic.getFeedbackResponse(feedbackResponseId);
        }

        if (feedbackResponseAttributes != null) {
            courseId = feedbackResponseAttributes.getCourseId();
        } else if (feedbackResponse != null) {
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!isCourseMigrated(courseId)) {
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

        switch (intent) {
        case STUDENT_SUBMISSION:
        case INSTRUCTOR_SUBMISSION:
            FeedbackResponseComment comment =
                    sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseSqlId);
            if (comment == null) {
                FeedbackResponse fr = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
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

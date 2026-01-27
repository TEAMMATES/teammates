package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
        String feedbackResponseIdParam = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        ParsedFeedbackResponseId parsedId = parseFeedbackResponseId(feedbackResponseIdParam);

        FeedbackResponseAttributes feedbackResponseAttributes = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        if (parsedId.isSql) {
            feedbackResponse = sqlLogic.getFeedbackResponse(parsedId.sqlId);
            if (feedbackResponse == null) {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            feedbackResponseAttributes = logic.getFeedbackResponse(parsedId.datastoreId);
            if (feedbackResponseAttributes == null) {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }
            courseId = feedbackResponseAttributes.getCourseId();
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
        String feedbackResponseIdParam = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        ParsedFeedbackResponseId parsedId = parseFeedbackResponseId(feedbackResponseIdParam);

        FeedbackResponseAttributes response = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        if (parsedId.isSql) {
            feedbackResponse = sqlLogic.getFeedbackResponse(parsedId.sqlId);
            if (feedbackResponse == null) {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            response = logic.getFeedbackResponse(parsedId.datastoreId);
            if (response == null) {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }
            courseId = response.getCourseId();
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!isCourseMigrated(courseId)) {
            switch (intent) {
            case STUDENT_SUBMISSION:
            case INSTRUCTOR_SUBMISSION:
                FeedbackResponseCommentAttributes comment =
                        logic.getFeedbackResponseCommentForResponseFromParticipant(parsedId.datastoreId);
                if (comment == null) {
                    FeedbackResponseAttributes fr = logic.getFeedbackResponse(parsedId.datastoreId);
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
                    sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(parsedId.sqlId);
            if (comment == null) {
                FeedbackResponse fr = sqlLogic.getFeedbackResponse(parsedId.sqlId);
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

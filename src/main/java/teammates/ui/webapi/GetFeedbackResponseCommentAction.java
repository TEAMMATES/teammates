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
import teammates.common.util.*;
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

        FeedbackResponseAttributes feedbackResponseAttributes = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        // Check if feedbackResponseIdParam is a UUID first
        try {
            UUID feedbackResponseSqlId = getUuidFromString(
                    Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                    feedbackResponseIdParam  // Use raw parameter directly
            );
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);

            if (feedbackResponse == null) {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();

        } catch (InvalidHttpParameterException e) {
            // Not a valid UUID, so check if it is an encrypted datastore ID
            try {
                String decryptedId = StringHelper.decrypt(feedbackResponseIdParam);
                feedbackResponseAttributes = logic.getFeedbackResponse(decryptedId);

                if (feedbackResponseAttributes == null) {
                    throw new EntityNotFoundException("The feedback response does not exist.");
                }
                courseId = feedbackResponseAttributes.getCourseId();

            } catch (InvalidParametersException ipe) {
                throw new InvalidHttpParameterException(ipe);
            }
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

        FeedbackResponseAttributes response = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;
        String feedbackResponseId = null;  // For datastore path
        UUID feedbackResponseSqlId = null;

        // TRY SQL FIRST (UUID doesn't need decryption)
        try {
            feedbackResponseSqlId = getUuidFromString(
                    Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                    feedbackResponseIdParam
            );
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
            System.out.println("Trying to get feedback response via SQL with ID: " + feedbackResponseSqlId);
            if (feedbackResponse != null) {
                courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
                feedbackResponseId = feedbackResponseIdParam;  // Use raw UUID string
            } else {
                throw new EntityNotFoundException("The feedback response does not exist.");
            }

        } catch (InvalidHttpParameterException e) {
            // Not a valid UUID, so try datastore with decryption
            try {
                feedbackResponseId = StringHelper.decrypt(feedbackResponseIdParam);
                response = logic.getFeedbackResponse(feedbackResponseId);

                if (response == null) {
                    throw new EntityNotFoundException("The feedback response does not exist.");
                }
                courseId = response.getCourseId();

            } catch (InvalidParametersException ipe) {
                throw new InvalidHttpParameterException(ipe);
            }
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!isCourseMigrated(courseId) && feedbackResponseSqlId == null) {
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
            HibernateUtil.flushSession();
            HibernateUtil.clearSession();
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

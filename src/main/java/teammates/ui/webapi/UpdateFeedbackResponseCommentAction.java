package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback response comment.
 */
public class UpdateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        FeedbackResponseComment feedbackResponseComment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (frc != null) {
            courseId = frc.getCourseId();
        } else if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        if (!isCourseMigrated(courseId)) {
            String feedbackResponseId = frc.getFeedbackResponseId();
            FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
            String feedbackSessionName = frc.getFeedbackSessionName();
            FeedbackSessionAttributes session = getNonNullFeedbackSession(feedbackSessionName, courseId);
            assert response != null;
            String questionId = response.getFeedbackQuestionId();
            FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
            Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

            switch (intent) {
            case STUDENT_SUBMISSION:
                StudentAttributes student = getStudentOfCourseFromRequest(courseId);
                if (student == null) {
                    throw new EntityNotFoundException("Student does not exist.");
                }
                session = session.getCopyForStudent(student.getEmail());

                gateKeeper.verifyAnswerableForStudent(question);
                verifySessionOpenExceptForModeration(session);
                verifyInstructorCanSeeQuestionIfInModeration(question);
                verifyNotPreview();

                checkAccessControlForStudentFeedbackSubmission(student, session);
                gateKeeper.verifyOwnership(frc,
                        question.getGiverType() == FeedbackParticipantType.TEAMS
                                ? student.getTeam() : student.getEmail());
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
                if (instructorAsFeedbackParticipant == null) {
                    throw new EntityNotFoundException("Instructor does not exist.");
                }
                session = session.getCopyForInstructor(instructorAsFeedbackParticipant.getEmail());

                gateKeeper.verifyAnswerableForInstructor(question);
                verifySessionOpenExceptForModeration(session);
                verifyInstructorCanSeeQuestionIfInModeration(question);
                verifyNotPreview();

                checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
                gateKeeper.verifyOwnership(frc, instructorAsFeedbackParticipant.getEmail());
                break;
            case INSTRUCTOR_RESULT:
                gateKeeper.verifyLoggedInUserPrivileges(userInfo);
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                if (instructor == null) {
                    throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
                }
                if (frc.getCommentGiver().equals(instructor.getEmail())) { // giver, allowed by default
                    return;
                }
                gateKeeper.verifyAccessible(instructor, session, response.getGiverSection(),
                        Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                gateKeeper.verifyAccessible(instructor, session, response.getRecipientSection(),
                        Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
            return;
        }

        FeedbackResponse response = feedbackResponseComment.getFeedbackResponse();
        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            session = session.getCopyForUser(student.getEmail());

            gateKeeper.verifyAnswerableForStudent(question);
            verifySessionOpenExceptForModeration(session, student);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);
            gateKeeper.verifyOwnership(feedbackResponseComment,
                    question.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeam().getName() : student.getEmail());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getSqlInstructorOfCourseFromRequest(courseId);
            if (instructorAsFeedbackParticipant == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
            session = session.getCopyForUser(instructorAsFeedbackParticipant.getEmail());

            gateKeeper.verifyAnswerableForInstructor(question);
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            gateKeeper.verifyOwnership(feedbackResponseComment, instructorAsFeedbackParticipant.getEmail());
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            if (feedbackResponseComment.getGiver().equals(instructor.getEmail())) { // giver, allowed by default
                return;
            }
            gateKeeper.verifyAccessible(instructor, session, response.getGiverSection().getName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.getRecipientSection().getName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        FeedbackResponseComment feedbackResponseComment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (frc != null) {
            courseId = frc.getCourseId();
        } else if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        if (!isCourseMigrated(courseId)) {
            String feedbackResponseId = frc.getFeedbackResponseId();
            FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
            assert response != null;

            Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
            String email;

            switch (intent) {
            case STUDENT_SUBMISSION:
                StudentAttributes student = getStudentOfCourseFromRequest(courseId);
                email = student.getEmail();
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
                email = instructorAsFeedbackParticipant.getEmail();
                break;
            case INSTRUCTOR_RESULT:
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
                email = instructor.getEmail();
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }

            FeedbackResponseCommentUpdateRequest comment =
                    getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

            // Edit comment text
            String commentText = comment.getCommentText();
            if (commentText.trim().isEmpty()) {
                throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
            }

            List<FeedbackParticipantType> showCommentTo = comment.getShowCommentTo();
            List<FeedbackParticipantType> showGiverNameTo = comment.getShowGiverNameTo();

            FeedbackResponseCommentAttributes.UpdateOptions.Builder commentUpdateOptions =
                    FeedbackResponseCommentAttributes.updateOptionsBuilder(feedbackResponseCommentId)
                            .withCommentText(commentText)
                            .withShowCommentTo(showCommentTo)
                            .withShowGiverNameTo(showGiverNameTo)
                            .withLastEditorEmail(email)
                            .withLastEditorAt(Instant.now());

            FeedbackResponseCommentAttributes updatedComment;
            try {
                updatedComment = logic.updateFeedbackResponseComment(commentUpdateOptions.build());
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e);
            }

            return new JsonResult(new FeedbackResponseCommentData(updatedComment));
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String email;

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            email = student.getEmail();
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getSqlInstructorOfCourseFromRequest(courseId);
            email = instructorAsFeedbackParticipant.getEmail();
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
            email = instructor.getEmail();
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        // Validate comment text
        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
        }

        try {
            FeedbackResponseComment updatedFeedbackResponseComment =
                    sqlLogic.updateFeedbackResponseComment(feedbackResponseCommentId, comment, email);
            return new JsonResult(new FeedbackResponseCommentData(updatedFeedbackResponseComment));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}

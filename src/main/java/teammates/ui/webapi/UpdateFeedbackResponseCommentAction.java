package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;

/**
 * Updates a feedback response comment.
 */
class UpdateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String courseId = frc.courseId;
        String feedbackResponseId = frc.feedbackResponseId;
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        String feedbackSessionName = frc.feedbackSessionName;
        FeedbackSessionAttributes session = getNonNullFeedbackSession(feedbackSessionName, courseId);
        Assumption.assertNotNull(response);
        String questionId = response.getFeedbackQuestionId();
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = getStudentOfCourseFromRequest(courseId);
            Assumption.assertNotNull(student);

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
            Assumption.assertNotNull(instructorAsFeedbackParticipant);

            gateKeeper.verifyAnswerableForInstructor(question);
            verifySessionOpenExceptForModeration(session);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            gateKeeper.verifyOwnership(frc, instructorAsFeedbackParticipant.getEmail());
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges();
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            if (instructor != null && frc.getCommentGiver().equals(instructor.getEmail())) { // giver, allowed by default
                return;
            }
            gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    JsonResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String feedbackResponseId = frc.feedbackResponseId;
        String courseId = frc.courseId;
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

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

        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        // Edit comment text
        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            return new JsonResult(FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
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

        FeedbackResponseCommentAttributes updatedComment = null;
        try {
            updatedComment = logic.updateFeedbackResponseComment(commentUpdateOptions.build());
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new FeedbackResponseCommentData(updatedComment));
    }

}

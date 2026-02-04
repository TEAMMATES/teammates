package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityDoesNotExistException;
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

        FeedbackResponseComment feedbackResponseComment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
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

        FeedbackResponseComment feedbackResponseComment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
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

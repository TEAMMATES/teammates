package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new feedback response comment.
 */
public class CreateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);

        FeedbackResponse feedbackResponse = null;

        feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);

        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();

        FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();
        FeedbackSession session = feedbackQuestion.getFeedbackSession();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseFromRequest(courseId);
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            verifySessionOpenExceptForModeration(session, student);
            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);

            verifyResponseOwnershipForStudent(student, feedbackResponse, feedbackQuestion);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            if (instructorAsFeedbackParticipant == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);

            verifyResponseOwnerShipForInstructor(instructorAsFeedbackParticipant, feedbackResponse);
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, session, feedbackResponse.getGiverSection().getName(),
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, feedbackResponse.getRecipientSection().getName(),
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            if (!feedbackQuestion.getQuestionDetailsCopy().isInstructorCommentsOnResponsesAllowed()) {
                throw new InvalidHttpParameterException("Invalid question type for instructor comment");
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);

        FeedbackResponse feedbackResponse = null;

        feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);

        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
        }

        FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        String email;
        boolean isFromParticipant;
        boolean isFollowingQuestionVisibility;
        FeedbackParticipantType commentGiverType;

        switch (intent) {
        case STUDENT_SUBMISSION:
            verifyCommentNotExist(feedbackResponseId);
            Student student = getStudentOfCourseFromRequest(courseId);
            email = feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    ? student.getTeamName() : student.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    ? FeedbackParticipantType.TEAMS : FeedbackParticipantType.STUDENTS;
            break;
        case INSTRUCTOR_SUBMISSION:
            verifyCommentNotExist(feedbackResponseId);
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            email = instructorAsFeedbackParticipant.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.getId());
            email = instructor.getEmail();
            isFromParticipant = false;
            isFollowingQuestionVisibility = false;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(feedbackResponse, email,
                commentGiverType, feedbackResponse.getGiverSection(), feedbackResponse.getRecipientSection(), commentText,
                isFollowingQuestionVisibility, isFromParticipant, comment.getShowCommentTo(), comment.getShowGiverNameTo(),
                email);
        try {
            FeedbackResponseComment createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
            HibernateUtil.flushSession();
            return new JsonResult(new FeedbackResponseCommentData(createdComment));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }
    }
}

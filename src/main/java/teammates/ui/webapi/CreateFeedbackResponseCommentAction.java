package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
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
        FeedbackSession session = feedbackQuestion.getFeedbackSession();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
            case STUDENT_SUBMISSION:
                Student student = getSqlStudentOfCourseFromRequest(courseId);
                if (student == null) {
                    throw new EntityNotFoundException("Student does not exist.");
                }
                session = session.getCopyForUser(student.getEmail());

                gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
                verifySessionOpenExceptForModeration(session, student);
                verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
                verifyNotPreview();

                checkAccessControlForStudentFeedbackSubmission(student, session);

                verifyResponseOwnerShipForStudent(student, feedbackResponse, feedbackQuestion);
                break;
            case INSTRUCTOR_SUBMISSION:
                Instructor instructorAsFeedbackParticipant = getSqlInstructorOfCourseFromRequest(courseId);
                if (instructorAsFeedbackParticipant == null) {
                    throw new EntityNotFoundException("Instructor does not exist.");
                }
                session = session.getCopyForUser(instructorAsFeedbackParticipant.getEmail());

                gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
                verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
                verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
                verifyNotPreview();

                checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);

                verifyResponseOwnerShipForInstructor(instructorAsFeedbackParticipant, feedbackResponse);
                break;
            case INSTRUCTOR_RESULT:
                gateKeeper.verifyLoggedInUserPrivileges(userInfo);
                Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
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

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(
                FeedbackResponseCommentCreateRequest.class);

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
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            email = feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    ? student.getTeamName() : student.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    ? FeedbackParticipantType.TEAMS : FeedbackParticipantType.STUDENTS;
            break;
        case INSTRUCTOR_SUBMISSION:
            verifyCommentNotExist(feedbackResponseId);
            Instructor instructorAsFeedbackParticipant = getSqlInstructorOfCourseFromRequest(courseId);
            email = instructorAsFeedbackParticipant.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            email = instructor.getEmail();
            isFromParticipant = false;
            isFollowingQuestionVisibility = false;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(feedbackResponse, email,
                commentGiverType, feedbackResponse.getGiverSection(), feedbackResponse.getRecipientSection(),
                commentText,
                isFollowingQuestionVisibility, isFromParticipant, comment.getShowCommentTo(),
                comment.getShowGiverNameTo(),
                email);
        try {
            FeedbackResponseComment createdComment = sqlLogic.createFeedbackResponseComment(feedbackResponseComment);
            HibernateUtil.flushSession();
            return new JsonResult(new FeedbackResponseCommentData(createdComment));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }
    }
}

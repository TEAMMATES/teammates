package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new feedback response comment.
 */
class CreateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

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
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        if (response == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = response.getCourseId();
        String feedbackSessionName = response.getFeedbackSessionName();
        FeedbackSessionAttributes session = getNonNullFeedbackSession(feedbackSessionName, courseId);
        String questionId = response.getFeedbackQuestionId();
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            if (studentAttributes == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            session = session.getCopyForStudent(studentAttributes.getEmail());

            gateKeeper.verifyAnswerableForStudent(question);
            verifySessionOpenExceptForModeration(session);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(studentAttributes, session);

            validQuestionForCommentInSubmission(question);
            verifyResponseOwnerShipForStudent(studentAttributes, response, question);
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

            validQuestionForCommentInSubmission(question);
            verifyResponseOwnerShipForInstructor(instructorAsFeedbackParticipant, response);
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, session, response.getGiverSection(),
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.getRecipientSection(),
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            if (!question.getQuestionDetailsCopy().isInstructorCommentsOnResponsesAllowed()) {
                throw new InvalidHttpParameterException("Invalid question type for instructor comment");
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        if (response == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }
        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
        }
        String questionId = response.getFeedbackQuestionId();
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        String courseId = response.getCourseId();
        String email;

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        boolean isFromParticipant;
        boolean isFollowingQuestionVisibility;
        FeedbackParticipantType commentGiverType;
        switch (intent) {
        case STUDENT_SUBMISSION:
            verifyCommentNotExist(feedbackResponseId);
            StudentAttributes student = getStudentOfCourseFromRequest(courseId);
            email = question.getGiverType() == FeedbackParticipantType.TEAMS
                    ? student.getTeam() : student.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = question.getGiverType() == FeedbackParticipantType.TEAMS
                    ? FeedbackParticipantType.TEAMS : FeedbackParticipantType.STUDENTS;
            break;
        case INSTRUCTOR_SUBMISSION:
            verifyCommentNotExist(feedbackResponseId);
            InstructorAttributes instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            email = instructorAsFeedbackParticipant.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            email = instructor.getEmail();
            isFromParticipant = false;
            isFollowingQuestionVisibility = false;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        String feedbackQuestionId = response.getFeedbackQuestionId();
        String feedbackSessionName = response.getFeedbackSessionName();

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withCommentGiver(email)
                .withCommentText(commentText)
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackResponseId(feedbackResponseId)
                .withGiverSection(response.getGiverSection())
                .withReceiverSection(response.getRecipientSection())
                .withCommentFromFeedbackParticipant(isFromParticipant)
                .withCommentGiverType(commentGiverType)
                .withVisibilityFollowingFeedbackQuestion(isFollowingQuestionVisibility)
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
                .build();

        FeedbackResponseCommentAttributes createdComment;
        try {
            createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new FeedbackResponseCommentData(createdComment));
    }

}

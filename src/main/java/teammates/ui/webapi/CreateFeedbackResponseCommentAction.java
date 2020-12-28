package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;

/**
 * Creates a new feedback response comment.
 */
class CreateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        String courseId = response.courseId;
        String feedbackSessionName = response.feedbackSessionName;
        FeedbackSessionAttributes session = getNonNullFeedbackSession(feedbackSessionName, courseId);
        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            Assumption.assertNotNull(studentAttributes);

            gateKeeper.verifyAnswerableForStudent(question);
            verifySessionOpenExceptForModeration(session);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(studentAttributes, session);

            validQuestionForCommentInSubmission(question);
            verifyCommentNotExist(feedbackResponseId);
            verifyResponseOwnerShipForStudent(studentAttributes, response, question);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            Assumption.assertNotNull(instructorAsFeedbackParticipant);

            gateKeeper.verifyAnswerableForInstructor(question);
            verifySessionOpenExceptForModeration(session);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);

            validQuestionForCommentInSubmission(question);
            verifyCommentNotExist(feedbackResponseId);
            verifyResponseOwnerShipForInstructor(instructorAsFeedbackParticipant, response);
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges();
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
            if (!question.getQuestionDetails().isInstructorCommentsOnResponsesAllowed()) {
                throw new InvalidHttpParameterException("Invalid question type for instructor comment");
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    JsonResult execute() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }

        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);
        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            return new JsonResult(FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
        }
        String questionId = response.getFeedbackQuestionId();
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        String courseId = response.courseId;
        String email;

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        boolean isFromParticipant;
        boolean isFollowingQuestionVisibility;
        FeedbackParticipantType commentGiverType;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = getStudentOfCourseFromRequest(courseId);
            email = question.getGiverType() == FeedbackParticipantType.TEAMS
                    ? student.getTeam() : student.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = question.getGiverType() == FeedbackParticipantType.TEAMS
                    ? FeedbackParticipantType.TEAMS : FeedbackParticipantType.STUDENTS;
            break;
        case INSTRUCTOR_SUBMISSION:
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

        String feedbackQuestionId = response.feedbackQuestionId;
        String feedbackSessionName = response.feedbackSessionName;

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withCommentGiver(email)
                .withCommentText(commentText)
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackResponseId(feedbackResponseId)
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .withCommentFromFeedbackParticipant(isFromParticipant)
                .withCommentGiverType(commentGiverType)
                .withVisibilityFollowingFeedbackQuestion(isFollowingQuestionVisibility)
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
                .build();

        FeedbackResponseCommentAttributes createdComment = null;
        try {
            createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new FeedbackResponseCommentData(createdComment));
    }

}

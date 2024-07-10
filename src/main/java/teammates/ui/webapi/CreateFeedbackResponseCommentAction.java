package teammates.ui.webapi;

import java.util.UUID;

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
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
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
        String feedbackResponseId;

        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        FeedbackResponseAttributes response = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        UUID feedbackResponseSqlId;

        try {
            feedbackResponseSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            response = logic.getFeedbackResponse(feedbackResponseId);
        }

        if (response != null) {
            courseId = response.getCourseId();
        } else if (feedbackResponse != null) {
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        if (!isCourseMigrated(courseId)) {
            handleDataStoreAccessControl(courseId, response);
            return;
        }

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
        String feedbackResponseId;

        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        FeedbackResponseAttributes response = null;
        FeedbackResponse feedbackResponse = null;
        String courseId;

        UUID feedbackResponseSqlId;

        try {
            feedbackResponseSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
            feedbackResponse = sqlLogic.getFeedbackResponse(feedbackResponseSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            response = logic.getFeedbackResponse(feedbackResponseId);
        }

        if (response != null) {
            courseId = response.getCourseId();
        } else if (feedbackResponse != null) {
            courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
        }

        if (!isCourseMigrated(courseId)) {
            String questionId = response.getFeedbackQuestionId();
            FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
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
                commentGiverType, feedbackResponse.getGiverSection(), feedbackResponse.getRecipientSection(), commentText,
                isFollowingQuestionVisibility, isFromParticipant, comment.getShowCommentTo(), comment.getShowGiverNameTo(),
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

    private void handleDataStoreAccessControl(String courseId, FeedbackResponseAttributes response)
            throws UnauthorizedAccessException {
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
}

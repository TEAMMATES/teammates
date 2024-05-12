package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Submits a list of feedback responses to a feedback question.
 *
 * <p>This action is meant to completely overwrite the feedback responses that are previously attached to the
 * same feedback question.
 */
public class SubmitFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        FeedbackQuestion feedbackQuestion = null;
        FeedbackQuestionAttributes feedbackQuestionAttributes = null;

        try {
            UUID feedbackQuestionSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
            feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (feedbackQuestion == null && feedbackQuestionAttributes == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        String courseId;
        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else {
            courseId = feedbackQuestionAttributes.getCourseId();
        }

        if (!isCourseMigrated(courseId)) {
            handleDataStoreAccessControl(feedbackQuestionAttributes);
            return;
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();
        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            Student student = getSqlStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            feedbackSession = feedbackSession.getCopyForUser(student.getEmail());
            verifySessionOpenExceptForModeration(feedbackSession, student);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getSqlInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            feedbackSession = feedbackSession.getCopyForUser(instructor.getEmail());
            verifySessionOpenExceptForModeration(feedbackSession, instructor);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
        case STUDENT_RESULT:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    private void handleDataStoreAccessControl(FeedbackQuestionAttributes feedbackQuestionAttributes)
            throws UnauthorizedAccessException {
        FeedbackSessionAttributes feedbackSession =
                getNonNullFeedbackSession(
                        feedbackQuestionAttributes.getFeedbackSessionName(),
                        feedbackQuestionAttributes.getCourseId());

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestionAttributes);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestionAttributes);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackQuestionAttributes.getCourseId());
            if (studentAttributes == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            feedbackSession = feedbackSession.getCopyForStudent(studentAttributes.getEmail());
            verifySessionOpenExceptForModeration(feedbackSession);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestionAttributes);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(
                    feedbackQuestionAttributes.getCourseId());
            if (instructorAttributes == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
            feedbackSession = feedbackSession.getCopyForInstructor(instructorAttributes.getEmail());
            verifySessionOpenExceptForModeration(feedbackSession);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
        case STUDENT_RESULT:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        FeedbackQuestion feedbackQuestionSql = null;
        FeedbackQuestionAttributes feedbackQuestionAttributes = null;

        try {
            UUID feedbackQuestionSqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
            feedbackQuestionSql = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);
        }

        if (feedbackQuestionSql == null && feedbackQuestionAttributes == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        String courseId;
        if (feedbackQuestionSql != null) {
            courseId = feedbackQuestionSql.getCourseId();
        } else {
            courseId = feedbackQuestionAttributes.getCourseId();
        }

        if (!isCourseMigrated(courseId)) {
            return handleDataStoreExecute(feedbackQuestionAttributes);
        }

        final FeedbackQuestion feedbackQuestion = feedbackQuestionSql;
        List<FeedbackResponse> existingResponses;
        Map<String, FeedbackQuestionRecipient> recipientsOfTheQuestion;

        String giverIdentifier;
        Section giverSection;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier =
                    feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeamName() : student.getEmail();
            giverSection = student.getSection();
            existingResponses = sqlLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, student);
            recipientsOfTheQuestion = sqlLogic.getRecipientsOfQuestion(feedbackQuestion, null, student);
            sqlLogic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    feedbackQuestion.getCourseId(), student.getEmail(), student.getTeamName());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier = instructor.getEmail();
            giverSection = sqlLogic.getDefaultSectionOrCreate(courseId);
            existingResponses = sqlLogic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructor);
            recipientsOfTheQuestion = sqlLogic.getRecipientsOfQuestion(feedbackQuestion, instructor, null);
            sqlLogic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    feedbackQuestion.getCourseId(), instructor.getEmail(), null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        Map<String, FeedbackResponse> existingResponsesPerRecipient = new HashMap<>();
        existingResponses.forEach(response -> existingResponsesPerRecipient.put(response.getRecipient(), response));

        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);
        log.info(JsonUtils.toCompactJson(submitRequest));

        for (String recipient : submitRequest.getRecipients()) {
            if (!recipientsOfTheQuestion.containsKey(recipient)) {
                throw new InvalidOperationException(
                        "The recipient " + recipient + " is not a valid recipient of the question");
            }
        }

        List<FeedbackResponse> feedbackResponsesToValidate = new ArrayList<>();
        List<FeedbackResponse> feedbackResponsesToAdd = new ArrayList<>();
        List<FeedbackResponse> feedbackResponsesToUpdate = new ArrayList<>();

        submitRequest.getResponses().forEach(responseRequest -> {
            String recipient = responseRequest.getRecipient();
            FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();

            if (existingResponsesPerRecipient.containsKey(recipient)) {
                Section recipientSection = getRecipientSection(feedbackQuestion.getCourseId(),
                        feedbackQuestion.getGiverType(),
                        feedbackQuestion.getRecipientType(), recipient);

                FeedbackResponse existingFeedbackResponse = existingResponsesPerRecipient.get(recipient);
                FeedbackResponse updatedFeedbackResponse = FeedbackResponse.updateResponse(
                        existingFeedbackResponse,
                        feedbackQuestion,
                        giverIdentifier,
                        giverSection,
                        recipient,
                        recipientSection,
                        responseDetails);

                feedbackResponsesToValidate.add(updatedFeedbackResponse);
                feedbackResponsesToUpdate.add(updatedFeedbackResponse);
            } else {
                FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                        feedbackQuestion,
                        giverIdentifier,
                        giverSection,
                        recipient,
                        getRecipientSection(feedbackQuestion.getCourseId(),
                            feedbackQuestion.getGiverType(),
                            feedbackQuestion.getRecipientType(), recipient),
                        responseDetails
                    );

                feedbackResponsesToValidate.add(feedbackResponse);
                feedbackResponsesToAdd.add(feedbackResponse);
            }
        });

        List<FeedbackResponseDetails> responseDetails = feedbackResponsesToValidate.stream()
                .map(FeedbackResponse::getFeedbackResponseDetailsCopy)
                .collect(Collectors.toList());

        int numRecipients = feedbackQuestion.getNumOfEntitiesToGiveFeedbackTo();
        if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS
                || numRecipients > recipientsOfTheQuestion.size()) {
            numRecipients = recipientsOfTheQuestion.size();
        }

        List<String> questionSpecificErrors =
                feedbackQuestion.getQuestionDetailsCopy()
                        .validateResponsesDetails(responseDetails, numRecipients);

        if (!questionSpecificErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(questionSpecificErrors.toString());
        }

        List<String> recipients = submitRequest.getRecipients();
        List<FeedbackResponse> feedbackResponsesToDelete = existingResponsesPerRecipient.entrySet().stream()
                .filter(entry -> !recipients.contains(entry.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        for (FeedbackResponse feedbackResponse : feedbackResponsesToDelete) {
            sqlLogic.deleteFeedbackResponsesAndCommentsCascade(feedbackResponse);
        }

        List<FeedbackResponse> output = new ArrayList<>();

        for (FeedbackResponse feedbackResponse : feedbackResponsesToAdd) {
            try {
                output.add(sqlLogic.createFeedbackResponse(feedbackResponse));
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when creating response: " + e.getMessage(), e);
            }
        }

        for (FeedbackResponse feedbackResponse : feedbackResponsesToUpdate) {
            try {
                output.add(sqlLogic.updateFeedbackResponseCascade(feedbackResponse));
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when updating response: " + e.getMessage(), e);
            }
        }

        return new JsonResult(FeedbackResponsesData.createFromEntity(output));
    }

    private JsonResult handleDataStoreExecute(FeedbackQuestionAttributes feedbackQuestion)
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        List<FeedbackResponseAttributes> existingResponses;
        Map<String, FeedbackQuestionRecipient> recipientsOfTheQuestion;

        String giverIdentifier;
        String giverSection;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier =
                    feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                            ? studentAttributes.getTeam() : studentAttributes.getEmail();
            giverSection = studentAttributes.getSection();
            existingResponses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, studentAttributes);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, null, studentAttributes);
            logic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    studentAttributes.getEmail(), studentAttributes.getTeam());
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier = instructorAttributes.getEmail();
            giverSection = Const.DEFAULT_SECTION;
            existingResponses = logic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructorAttributes);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, instructorAttributes, null);
            logic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    instructorAttributes.getEmail(), null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        Map<String, FeedbackResponseAttributes> existingResponsesPerRecipient = new HashMap<>();
        existingResponses.forEach(response -> existingResponsesPerRecipient.put(response.getRecipient(), response));

        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);
        log.info(JsonUtils.toCompactJson(submitRequest));

        for (String recipient : submitRequest.getRecipients()) {
            if (!recipientsOfTheQuestion.containsKey(recipient)) {
                throw new InvalidOperationException(
                        "The recipient " + recipient + " is not a valid recipient of the question");
            }
        }

        List<FeedbackResponseAttributes> feedbackResponsesToValidate = new ArrayList<>();
        List<FeedbackResponseAttributes> feedbackResponsesToAdd = new ArrayList<>();
        List<FeedbackResponseAttributes.UpdateOptions> feedbackResponsesToUpdate = new ArrayList<>();

        submitRequest.getResponses().forEach(responseRequest -> {
            String recipient = responseRequest.getRecipient();
            FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();

            if (existingResponsesPerRecipient.containsKey(recipient)) {
                String recipientSection = getDatastoreRecipientSection(feedbackQuestion.getCourseId(),
                        feedbackQuestion.getGiverType(),
                        feedbackQuestion.getRecipientType(), recipient);
                FeedbackResponseAttributes updatedResponse =
                        new FeedbackResponseAttributes(existingResponsesPerRecipient.get(recipient));
                FeedbackResponseAttributes.UpdateOptions updateOptions =
                        FeedbackResponseAttributes.updateOptionsBuilder(updatedResponse.getId())
                                .withGiver(giverIdentifier)
                                .withGiverSection(giverSection)
                                .withRecipient(recipient)
                                .withRecipientSection(recipientSection)
                                .withResponseDetails(responseDetails)
                                .build();
                updatedResponse.update(updateOptions);

                feedbackResponsesToValidate.add(updatedResponse);
                feedbackResponsesToUpdate.add(updateOptions);
            } else {
                FeedbackResponseAttributes feedbackResponse = FeedbackResponseAttributes
                        .builder(feedbackQuestion.getId(), giverIdentifier, recipient)
                        .withGiverSection(giverSection)
                        .withRecipientSection(getDatastoreRecipientSection(feedbackQuestion.getCourseId(),
                                feedbackQuestion.getGiverType(),
                                feedbackQuestion.getRecipientType(), recipient))
                        .withCourseId(feedbackQuestion.getCourseId())
                        .withFeedbackSessionName(feedbackQuestion.getFeedbackSessionName())
                        .withResponseDetails(responseDetails)
                        .build();

                feedbackResponsesToValidate.add(feedbackResponse);
                feedbackResponsesToAdd.add(feedbackResponse);
            }
        });

        List<FeedbackResponseDetails> responseDetails = feedbackResponsesToValidate.stream()
                .map(FeedbackResponseAttributes::getResponseDetailsCopy)
                .collect(Collectors.toList());

        int numRecipients = feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo();
        if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS
                || numRecipients > recipientsOfTheQuestion.size()) {
            numRecipients = recipientsOfTheQuestion.size();
        }

        List<String> questionSpecificErrors =
                feedbackQuestion.getQuestionDetailsCopy()
                        .validateResponsesDetails(responseDetails, numRecipients);

        if (!questionSpecificErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(questionSpecificErrors.toString());
        }

        List<String> recipients = submitRequest.getRecipients();
        List<FeedbackResponseAttributes> feedbackResponsesToDelete = existingResponsesPerRecipient.entrySet().stream()
                .filter(entry -> !recipients.contains(entry.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        for (FeedbackResponseAttributes feedbackResponse : feedbackResponsesToDelete) {
            logic.deleteFeedbackResponseCascade(feedbackResponse.getId());
        }

        List<FeedbackResponseAttributes> output = new ArrayList<>();

        for (FeedbackResponseAttributes feedbackResponse : feedbackResponsesToAdd) {
            try {
                output.add(logic.createFeedbackResponse(feedbackResponse));
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when creating response: " + e.getMessage(), e);
            }
        }

        for (FeedbackResponseAttributes.UpdateOptions feedbackResponse : feedbackResponsesToUpdate) {
            try {
                output.add(logic.updateFeedbackResponseCascade(feedbackResponse));
            } catch (InvalidParametersException | EntityAlreadyExistsException | EntityDoesNotExistException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when updating response: " + e.getMessage(), e);
            }
        }

        return new JsonResult(FeedbackResponsesData.createFromAttributes(output));
    }
}

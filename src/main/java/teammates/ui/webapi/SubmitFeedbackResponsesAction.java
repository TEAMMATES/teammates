package teammates.ui.webapi;

import java.time.Instant;
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
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.FeedbackResponsesRequest.FeedbackResponseRequest;
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

        FeedbackQuestionAttributes feedbackQuestion = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId;
        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
            courseId = sqlFeedbackQuestion.getCourseId();
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
            courseId = feedbackQuestion.getCourseId();
        }

        if (!isCourseMigrated(courseId)) {
            if (feedbackQuestion == null) {
                throw new EntityNotFoundException("The feedback question does not exist.");
            }
            FeedbackSessionAttributes feedbackSession =
                    getNonNullFeedbackSession(feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getCourseId());

            verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
            verifyNotPreview();

            Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
            switch (intent) {
            case STUDENT_SUBMISSION:
                gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
                StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
                if (studentAttributes == null) {
                    throw new EntityNotFoundException("Student does not exist.");
                }
                feedbackSession = feedbackSession.getCopyForStudent(studentAttributes.getEmail());
                verifySessionOpenExceptForModeration(feedbackSession);
                checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
                break;
            case INSTRUCTOR_SUBMISSION:
                gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
                InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
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

            return;
        }

        if (sqlFeedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        FeedbackSession feedbackSession =
                getNonNullSqlFeedbackSession(
                        sqlFeedbackQuestion.getFeedbackSession().getName(), sqlFeedbackQuestion.getCourseId());

        verifyInstructorCanSeeQuestionIfInModeration(sqlFeedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(sqlFeedbackQuestion);
            Student student = getSqlStudentOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            Instant studentDeadline = sqlLogic.getDeadlineForUser(feedbackSession, student);
            verifySqlSessionOpenExceptForModeration(feedbackSession, studentDeadline);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(sqlFeedbackQuestion);
            Instructor instructor = getSqlInstructorOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            if (instructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
            Instant instructorDeadline = sqlLogic.getDeadlineForUser(feedbackSession, instructor);
            verifySqlSessionOpenExceptForModeration(feedbackSession, instructorDeadline);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
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

        FeedbackQuestionAttributes feedbackQuestion = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId = null;
        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        }
        
        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else if (sqlFeedbackQuestion != null) {
            courseId = sqlFeedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        if (!isCourseMigrated(courseId)) {
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
                existingResponses =
                        logic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, studentAttributes);
                recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, null, studentAttributes);
                logic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                        studentAttributes.getEmail(), studentAttributes.getTeam());
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
                giverIdentifier = instructorAttributes.getEmail();
                giverSection = Const.DEFAULT_SECTION;
                existingResponses =
                        logic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructorAttributes);
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

            List<FeedbackResponseRequest> responseRequests = submitRequest.getResponses();

            for (FeedbackResponseRequest responseRequest : responseRequests) {
                String recipient = responseRequest.getRecipient();
                FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();

                if (existingResponsesPerRecipient.containsKey(recipient)) {
                    String recipientSection = getRecipientSectionName(feedbackQuestion.getCourseId(),
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
                            .withRecipientSection(getRecipientSectionName(feedbackQuestion.getCourseId(),
                                    feedbackQuestion.getGiverType(),
                                    feedbackQuestion.getRecipientType(), recipient))
                            .withCourseId(feedbackQuestion.getCourseId())
                            .withFeedbackSessionName(feedbackQuestion.getFeedbackSessionName())
                            .withResponseDetails(responseDetails)
                            .build();

                    feedbackResponsesToValidate.add(feedbackResponse);
                    feedbackResponsesToAdd.add(feedbackResponse);
                }
            }

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

            FeedbackResponsesData feedbackResponsesData = new FeedbackResponsesData();
            List<FeedbackResponseData> responses =
                    output.stream().map(FeedbackResponseData::new).collect(Collectors.toList());
            feedbackResponsesData.setResponses(responses);

            return new JsonResult(feedbackResponsesData);
        }

        List<FeedbackResponse> existingResponses;
        Map<String, FeedbackQuestionRecipient> recipientsOfTheQuestion;

        String giverIdentifier;
        Section giverSection = null;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            giverIdentifier =
                    sqlFeedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeamName() : student.getEmail();
            giverSection = student.getSection();
            existingResponses = sqlLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(sqlFeedbackQuestion, student);
            recipientsOfTheQuestion = sqlLogic.getRecipientsOfQuestion(sqlFeedbackQuestion, null, student);
            sqlLogic.populateFieldsToGenerateInQuestion(
                    sqlFeedbackQuestion, sqlFeedbackQuestion.getCourseId(), student.getEmail(), student.getTeamName());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getSqlInstructorOfCourseFromRequest(sqlFeedbackQuestion.getCourseId());
            giverIdentifier = instructor.getEmail();
            existingResponses = sqlLogic.getFeedbackResponsesFromInstructorForQuestion(sqlFeedbackQuestion, instructor);
            recipientsOfTheQuestion = sqlLogic.getRecipientsOfQuestion(sqlFeedbackQuestion, instructor, null);
            sqlLogic.populateFieldsToGenerateInQuestion(
                    sqlFeedbackQuestion, sqlFeedbackQuestion.getCourseId(), instructor.getEmail(), null);
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

        List<FeedbackResponseRequest> responseRequests = submitRequest.getResponses();

        for (FeedbackResponseRequest responseRequest : responseRequests) {
            String recipient = responseRequest.getRecipient();
            FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();

            if (existingResponsesPerRecipient.containsKey(recipient)) {
                Section recipientSection = getRecipientSection(sqlFeedbackQuestion.getCourseId(),
                        sqlFeedbackQuestion.getGiverType(),
                        sqlFeedbackQuestion.getRecipientType(), recipient);

                FeedbackResponse existingResponse = existingResponsesPerRecipient.get(recipient);
                FeedbackResponse updatedResponse =
                        FeedbackResponse.makeResponse(
                                sqlFeedbackQuestion, giverIdentifier, giverSection,
                                recipient, recipientSection, responseDetails);
                updatedResponse.setId(existingResponse.getId());

                feedbackResponsesToValidate.add(updatedResponse);
                feedbackResponsesToUpdate.add(updatedResponse);
            } else {
                Section recipientSection = getRecipientSection(sqlFeedbackQuestion.getCourseId(),
                        sqlFeedbackQuestion.getGiverType(),
                        sqlFeedbackQuestion.getRecipientType(), recipient);

                FeedbackResponse feedbackResponse =
                        FeedbackResponse.makeResponse(
                                sqlFeedbackQuestion, giverIdentifier, giverSection,
                                recipientSection.getName(), recipientSection, responseDetails);

                feedbackResponsesToValidate.add(feedbackResponse);
                feedbackResponsesToAdd.add(feedbackResponse);
            }
        }

        List<FeedbackResponseDetails> responseDetails = feedbackResponsesToValidate.stream()
                .map(FeedbackResponse::getFeedbackResponseDetailsCopy)
                .collect(Collectors.toList());

        int numRecipients = sqlFeedbackQuestion.getNumOfEntitiesToGiveFeedbackTo();
        if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS
                || numRecipients > recipientsOfTheQuestion.size()) {
            numRecipients = recipientsOfTheQuestion.size();
        }

        List<String> questionSpecificErrors =
                sqlFeedbackQuestion.getQuestionDetailsCopy()
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
            sqlLogic.deleteFeedbackResponseCascade(feedbackResponse.getId());
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
            } catch (InvalidParametersException | EntityAlreadyExistsException | EntityDoesNotExistException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when updating response: " + e.getMessage(), e);
            }
        }

        return new JsonResult(new FeedbackResponsesData(output));
    }

}

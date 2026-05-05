package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
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
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        final FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();
        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            Student student = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            verifySessionOpenExceptForModeration(feedbackSession, student);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            Instructor instructor = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
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

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        final FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("The feedback question does not exist.");
        }

        String courseId = feedbackQuestion.getCourseId();

        List<FeedbackResponse> existingResponses;
        Map<String, FeedbackQuestionRecipient> recipientsOfTheQuestion;
        FeedbackQuestionDetails questionDetails = feedbackQuestion.getQuestionDetailsCopy();
        Optional<List<String>> dynamicallyGeneratedOptions;

        String giverIdentifier;
        Section giverSection;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier =
                    feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeamName() : student.getEmail();
            giverSection = student.getSection();
            existingResponses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(feedbackQuestion, student);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, null, student);
            dynamicallyGeneratedOptions = logic.getDynamicallyGeneratedOptions(feedbackQuestion, student);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier = instructor.getEmail();
            giverSection = logic.getDefaultSectionOrCreate(courseId);
            existingResponses = logic.getFeedbackResponsesFromInstructorForQuestion(feedbackQuestion, instructor);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, instructor, null);
            dynamicallyGeneratedOptions = logic.getDynamicallyGeneratedOptions(feedbackQuestion, null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (dynamicallyGeneratedOptions.isPresent()) {
            // Dynamically generated options are only supported for MCQ and MSQ questions
            if (questionDetails instanceof FeedbackMcqQuestionDetails feedbackMcqQuestionDetails) {
                feedbackMcqQuestionDetails.setMcqChoices(dynamicallyGeneratedOptions.get());
            } else if (questionDetails instanceof FeedbackMsqQuestionDetails feedbackMsqQuestionDetails) {
                feedbackMsqQuestionDetails.setMsqChoices(dynamicallyGeneratedOptions.get());
            }
        }

        Map<String, FeedbackResponse> existingResponsesPerRecipient = new HashMap<>();
        existingResponses.forEach(response -> existingResponsesPerRecipient.put(response.getRecipient(), response));

        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);
        log.info(JsonUtils.toCompactJson(submitRequest));

        for (String recipient : submitRequest.getRecipients()) {
            if (recipient == null || !recipientsOfTheQuestion.containsKey(recipient)) {
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
                        giverIdentifier,
                        giverSection,
                        recipient,
                        getRecipientSection(feedbackQuestion.getCourseId(),
                            feedbackQuestion.getGiverType(),
                            feedbackQuestion.getRecipientType(), recipient),
                        responseDetails
                    );

                feedbackQuestion.addFeedbackResponse(feedbackResponse);
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

        List<String> questionSpecificErrors = questionDetails
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
            logic.deleteFeedbackResponsesAndCommentsCascade(feedbackResponse);
        }

        List<FeedbackResponse> output = new ArrayList<>();

        for (FeedbackResponse feedbackResponse : feedbackResponsesToAdd) {
            try {
                output.add(logic.createFeedbackResponse(feedbackResponse));
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when creating response: " + e.getMessage(), e);
            }
        }

        for (FeedbackResponse feedbackResponse : feedbackResponsesToUpdate) {
            try {
                output.add(logic.updateFeedbackResponseCascade(feedbackResponse));
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                // None of the exceptions should be happening as the responses have been pre-validated
                log.severe("Encountered exception when updating response: " + e.getMessage(), e);
            }
        }

        return new JsonResult(FeedbackResponsesData.createFromEntity(output));
    }
}

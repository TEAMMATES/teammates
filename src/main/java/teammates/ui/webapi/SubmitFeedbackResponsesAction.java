package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;

/**
 * Submits a list of feedback responses to a feedback question.
 *
 * <p>This action is meant to completely overwrite the feedback responses that are previously attached to the
 * same feedback question.
 */
class SubmitFeedbackResponsesAction extends BasicFeedbackSubmissionAction {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("The feedback question does not exist."));
        }
        FeedbackSessionAttributes feedbackSession =
                getNonNullFeedbackSession(feedbackQuestion.feedbackSessionName, feedbackQuestion.courseId);

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifySessionOpenExceptForModeration(feedbackSession);
        verifyNotPreview();

        Map<String, String> recipientsOfTheQuestion;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, null, studentAttributes);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            recipientsOfTheQuestion = logic.getRecipientsOfQuestion(feedbackQuestion, instructorAttributes, null);
            break;
        case INSTRUCTOR_RESULT:
        case STUDENT_RESULT:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);

        for (String recipient : submitRequest.getRecipients()) {
            if (!recipientsOfTheQuestion.containsKey(recipient)) {
                throw new UnauthorizedAccessException(
                        "The recipient " + recipient + " is not a valid recipient of the question", true);
            }
        }
    }

    @Override
    JsonResult execute() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("The feedback question does not exist."));
        }

        List<FeedbackResponseAttributes> existingResponses;
        Map<String, String> recipientsOfTheQuestion;

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

        List<FeedbackResponseAttributes> feedbackResponsesToValidate = new ArrayList<>();
        List<FeedbackResponseAttributes> feedbackResponsesToAdd = new ArrayList<>();
        List<FeedbackResponseAttributes.UpdateOptions> feedbackResponsesToUpdate = new ArrayList<>();

        FeedbackResponsesRequest submitRequest = getAndValidateRequestBody(FeedbackResponsesRequest.class);
        log.info(JsonUtils.toCompactJson(submitRequest));

        submitRequest.getResponses().forEach(responseRequest -> {
            String recipient = responseRequest.getRecipient();
            FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();

            if (existingResponsesPerRecipient.containsKey(recipient)) {
                String recipientSection = getRecipientSection(feedbackQuestion.getCourseId(),
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
                        .withRecipientSection(getRecipientSection(feedbackQuestion.getCourseId(),
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
                .map(FeedbackResponseAttributes::getResponseDetails)
                .collect(Collectors.toList());

        int numRecipients = feedbackQuestion.numberOfEntitiesToGiveFeedbackTo;
        if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS
                || numRecipients > recipientsOfTheQuestion.size()) {
            numRecipients = recipientsOfTheQuestion.size();
        }

        List<String> questionSpecificErrors =
                feedbackQuestion.getQuestionDetails()
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
                throw new InvalidHttpRequestBodyException(e.getMessage(), e);
            }
        }

        for (FeedbackResponseAttributes.UpdateOptions feedbackResponse : feedbackResponsesToUpdate) {
            try {
                output.add(logic.updateFeedbackResponseCascade(feedbackResponse));
            } catch (InvalidParametersException | EntityAlreadyExistsException | EntityDoesNotExistException e) {
                throw new InvalidHttpRequestBodyException(e.getMessage(), e);
            }
        }

        return new JsonResult(new FeedbackResponsesData(output));
    }

}

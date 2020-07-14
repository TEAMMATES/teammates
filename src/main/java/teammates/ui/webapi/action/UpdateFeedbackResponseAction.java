package teammates.ui.webapi.action;

import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.request.FeedbackResponseUpdateRequest;
import teammates.ui.webapi.request.Intent;

/**
 * Updates a feedback response.
 */
public class UpdateFeedbackResponseAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("The feedback response does not exist."));
        }
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackResponse.feedbackQuestionId);
        FeedbackSessionAttributes feedbackSession =
                logic.getFeedbackSession(feedbackResponse.feedbackSessionName, feedbackResponse.courseId);

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
            recipientsOfTheQuestion =
                    logic.getRecipientsOfQuestion(feedbackQuestion, null, studentAttributes);
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            recipientsOfTheQuestion =
                    logic.getRecipientsOfQuestion(feedbackQuestion, instructorAttributes, null);
            break;
        case INSTRUCTOR_RESULT:
        case STUDENT_RESULT:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseUpdateRequest updateRequest = getAndValidateRequestBody(FeedbackResponseUpdateRequest.class);
        if (!recipientsOfTheQuestion.containsKey(updateRequest.getRecipientIdentifier())) {
            throw new UnauthorizedAccessException("The recipient is not a valid recipient of the question");
        }
    }

    @Override
    public ActionResult execute() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackResponse.feedbackQuestionId);

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
            logic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    studentAttributes.getEmail(), studentAttributes.getTeam());
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            giverIdentifier = instructorAttributes.getEmail();
            giverSection = Const.DEFAULT_SECTION;
            logic.populateFieldsToGenerateInQuestion(feedbackQuestion,
                    instructorAttributes.getEmail(), null);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseUpdateRequest updateRequest = getAndValidateRequestBody(FeedbackResponseUpdateRequest.class);
        feedbackResponse.giver = giverIdentifier;
        feedbackResponse.giverSection = giverSection;
        feedbackResponse.recipient = updateRequest.getRecipientIdentifier();
        feedbackResponse.recipientSection =
                getRecipientSection(feedbackQuestion.getCourseId(), feedbackQuestion.getGiverType(),
                        feedbackQuestion.getRecipientType(), updateRequest.getRecipientIdentifier());
        feedbackResponse.responseDetails = updateRequest.getResponseDetails();

        validResponseOfQuestion(feedbackQuestion, feedbackResponse);

        try {
            FeedbackResponseAttributes updatedFeedbackResponse = logic.updateFeedbackResponseCascade(
                    FeedbackResponseAttributes.updateOptionsBuilder(feedbackResponse.getId())
                            .withGiver(feedbackResponse.giver)
                            .withGiverSection(feedbackResponse.giverSection)
                            .withRecipient(feedbackResponse.recipient)
                            .withRecipientSection(feedbackResponse.recipientSection)
                            .withResponseDetails(feedbackResponse.getResponseDetails())
                            .build());

            return new JsonResult(new FeedbackResponseData(updatedFeedbackResponse));
        } catch (Exception e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }
    }

}

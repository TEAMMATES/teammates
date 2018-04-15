package teammates.ui.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.api.EmailGenerator;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;

public abstract class FeedbackSubmissionEditSaveAction extends Action {

    private static final Logger log = Logger.getLogger();

    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;
    protected boolean hasValidResponse;
    protected boolean isSendSubmissionEmail;
    protected List<FeedbackResponseAttributes> responsesToSave = new ArrayList<>();
    protected List<FeedbackResponseAttributes> responsesToDelete = new ArrayList<>();
    protected List<FeedbackResponseAttributes> responsesToUpdate = new ArrayList<>();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        setAdditionalParameters();
        verifyAccessibleForSpecificUser();

        String userEmailForCourse = getUserEmailForCourse();

        data = new FeedbackSubmissionEditPageData(account, student, sessionToken);
        data.bundle = getDataBundle(userEmailForCourse);
        Assumption.assertNotNull("Feedback session " + feedbackSessionName
                                 + " does not exist in " + courseId + ".", data.bundle);

        checkAdditionalConstraints();

        setStatusToAdmin();

        if (!isSessionOpenForSpecificUser(data.bundle.feedbackSession)) {
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN,
                                               StatusMessageColor.WARNING));
            return createSpecificRedirectResult();
        }

        String userTeamForCourse = getUserTeamForCourse();
        String userSectionForCourse = getUserSectionForCourse();

        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();

        for (int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String totalResponsesForQuestion = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-" + questionIndx);

            if (totalResponsesForQuestion == null) {
                continue; // question has been skipped (not displayed).
            }

            List<FeedbackResponseAttributes> responsesForQuestion = new ArrayList<>();
            String questionId = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
            FeedbackQuestionAttributes questionAttributes = data.bundle.getQuestionAttributes(questionId);
            if (questionAttributes == null) {
                statusToUser.add(new StatusMessage("The feedback session or questions may have changed "
                                                       + "while you were submitting. Please check your responses "
                                                       + "to make sure they are saved correctly.",
                                                   StatusMessageColor.WARNING));
                isError = true;
                log.warning("Question not found. (deleted or invalid id passed?) id: "
                            + questionId + " index: " + questionIndx);
                continue;
            }

            FeedbackQuestionDetails questionDetails = questionAttributes.getQuestionDetails();

            int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);

            Set<String> emailSet = data.bundle.getRecipientEmails(questionAttributes.getId());
            emailSet.add("");
            emailSet = SanitizationHelper.desanitizeFromHtml(emailSet);

            ArrayList<String> responsesRecipients = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++) {
                FeedbackResponseAttributes response =
                        extractFeedbackResponseData(requestParameters, questionIndx, responseIndx, questionAttributes);

                if (response.feedbackQuestionType != questionAttributes.questionType) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_WRONG_QUESTION_TYPE, questionIndx));
                }

                boolean isExistingResponse = response.getId() != null;
                // test that if editing an existing response, that the edited response's id
                // came from the original set of existing responses loaded on the submission page
                if (isExistingResponse && !isExistingResponseValid(response)) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_INVALID_ID, questionIndx));
                    continue;
                }

                responsesRecipients.add(response.recipient);
                // if the answer is not empty but the recipient is empty
                if (response.recipient.isEmpty() && !response.responseMetaData.getValue().isEmpty()) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MISSING_RECIPIENT, questionIndx));
                }

                if (response.responseMetaData.getValue().isEmpty()) {
                    // deletes the response since answer is empty
                    addToPendingResponses(response);
                } else {
                    response.giver = questionAttributes.giverType.isTeam() ? userTeamForCourse
                                                                                : userEmailForCourse;
                    response.giverSection = userSectionForCourse;
                    responsesForQuestion.add(response);
                }
            }

            List<String> questionSpecificErrors =
                    questionDetails.validateResponseAttributes(responsesForQuestion,
                                                               data.bundle.recipientList.get(questionId).size());
            errors.addAll(questionSpecificErrors);

            if (!emailSet.containsAll(responsesRecipients)) {
                errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSE_INVALID_RECIPIENT, questionIndx));
            }

            if (errors.isEmpty()) {
                for (FeedbackResponseAttributes response : responsesForQuestion) {
                    addToPendingResponses(response);
                }
            } else {
                List<StatusMessage> errorMessages = new ArrayList<>();

                for (String error : errors) {
                    errorMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
                }

                statusToUser.addAll(errorMessages);
                isError = true;
            }
        }

        saveNewReponses(responsesToSave);
        deleteResponses(responsesToDelete);
        updateResponses(responsesToUpdate);

        if (!isError) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, StatusMessageColor.SUCCESS));
        }

        if (isUserRespondentOfSession()) {
            appendRespondent();
        } else {
            removeRespondent();
        }

        boolean isSubmissionEmailRequested = "on".equals(getRequestParamValue(Const.ParamsNames.SEND_SUBMISSION_EMAIL));
        if (!isError && isSendSubmissionEmail && isSubmissionEmailRequested) {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            Assumption.assertNotNull(session);

            String user = account == null ? null : account.googleId;
            String unregisteredStudentEmail = student == null ? null : student.email;
            String unregisteredStudentRegisterationKey = student == null ? null : student.key;
            StudentAttributes student = null;
            InstructorAttributes instructor = null;
            if (user != null) {
                student = logic.getStudentForGoogleId(courseId, user);
                instructor = logic.getInstructorForGoogleId(courseId, user);
            }
            if (student == null && unregisteredStudentEmail != null) {
                student = StudentAttributes
                        .builder("", unregisteredStudentEmail, unregisteredStudentEmail)
                        .withKey(unregisteredStudentRegisterationKey)
                        .build();
            }
            Assumption.assertFalse(student == null && instructor == null);

            try {
                EmailWrapper email = instructor == null
                        ? new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForStudent(session,
                                student, Instant.now())
                        : new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session,
                                instructor, Instant.now());
                emailSender.sendEmail(email);
            } catch (EmailSendingException e) {
                log.severe("Submission confirmation email failed to send: "
                           + TeammatesException.toStringWithStackTrace(e));
            }
        }
        // TODO: Refactor to AjaxResult so status messages do not have to be passed by session
        return createSpecificRedirectResult();
    }

    /**
     * If the {@code response} is an existing response, check that
     * the questionId and responseId that it has
     * is in {@code data.bundle.questionResponseBundle}.
     * @param response  a response which has non-null id
     */
    private boolean isExistingResponseValid(FeedbackResponseAttributes response) {

        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = data.bundle.getQuestionAttributes(questionId);

        if (!data.bundle.questionResponseBundle.containsKey(question)) {
            // question id is invalid
            return false;
        }

        List<FeedbackResponseAttributes> existingResponses = data.bundle.questionResponseBundle.get(question);
        List<String> existingResponsesId = new ArrayList<>();
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            existingResponsesId.add(existingResponse.getId());
        }

        // checks if response id is valid
        return existingResponsesId.contains(response.getId());
    }

    private void addToPendingResponses(FeedbackResponseAttributes response) {
        boolean isExistingResponse = response.getId() != null;
        if (isExistingResponse) {
            // Delete away response if any empty fields
            if (response.responseMetaData.getValue().isEmpty() || response.recipient.isEmpty()) {
                responsesToDelete.add(response);
                return;
            }
            responsesToUpdate.add(response);
        } else if (!response.responseMetaData.getValue().isEmpty()
                   && !response.recipient.isEmpty()) {
            responsesToSave.add(response);
        }
    }

    private void saveNewReponses(List<FeedbackResponseAttributes> responsesToSave)
            throws EntityDoesNotExistException {
        try {
            logic.createFeedbackResponses(responsesToSave);
            hasValidResponse = true;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
    }

    private void deleteResponses(List<FeedbackResponseAttributes> responsesToDelete) {
        for (FeedbackResponseAttributes response : responsesToDelete) {
            logic.deleteFeedbackResponse(response);
        }
    }

    private void updateResponses(List<FeedbackResponseAttributes> responsesToUpdate)
            throws EntityDoesNotExistException {
        for (FeedbackResponseAttributes response : responsesToUpdate) {
            try {
                logic.updateFeedbackResponse(response);
                hasValidResponse = true;
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                setStatusForException(e);
            }
        }
    }

    private FeedbackResponseAttributes extractFeedbackResponseData(
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx,
            FeedbackQuestionAttributes feedbackQuestionAttributes) {

        FeedbackQuestionDetails questionDetails = feedbackQuestionAttributes.getQuestionDetails();
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        FeedbackResponseAttributes.Builder responseBuilder = FeedbackResponseAttributes.builder().withCourseId(courseId);

        responseBuilder = extractFeedbackResponseId(questionIndx, responseIndx, responseBuilder);

        responseBuilder = extractFeedbackResponseFeedbackSessionName(responseBuilder);

        responseBuilder = extractFeedbackResponseQuestionId(questionIndx, feedbackQuestionAttributes, responseBuilder);

        responseBuilder = extractFeedbackResponseQuestionType(questionIndx, responseBuilder);

        responseBuilder = extractFeedbackResponseRecipient(questionIndx, responseIndx, feedbackQuestionAttributes,
                responseBuilder, courseId);

        responseBuilder = extractFeedbackResponseMetaData(requestParameters, questionIndx, responseIndx,
                responseBuilder, questionDetails);

        return responseBuilder.build();
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseMetaData(Map<String, String[]> requestParameters,
            int questionIndx, int responseIndx, FeedbackResponseAttributes.Builder responseBuilder,
            FeedbackQuestionDetails questionDetails) {
        // This field can be null if the question is skipped
        String paramName = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionIndx + "-" + responseIndx;
        String[] answer = getRequestParamValues(paramName);

        if (questionDetails.isQuestionSkipped(answer)) {
            Text responseMetaData = new Text("");
            responseBuilder = responseBuilder.withResponseMetaData(responseMetaData);
        } else {
            FeedbackResponseDetails responseDetails =
                    FeedbackResponseDetails.createResponseDetails(answer, questionDetails.getQuestionType(),
                                                                  questionDetails, requestParameters,
                                                                  questionIndx, responseIndx);
            responseBuilder = responseBuilder.withReponseMetaDataFromFeedbackResponseDetails(responseDetails);
        }
        return responseBuilder;
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseRecipient(int questionIndx, int responseIndx,
            FeedbackQuestionAttributes feedbackQuestionAttributes, FeedbackResponseAttributes.Builder responseBuilder,
            String courseId) {
        String recipient = getNonNullRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + questionIndx + "-" + responseIndx);

        FeedbackParticipantType recipientType = feedbackQuestionAttributes.recipientType;
        String recipientSection;
        if (recipientType == FeedbackParticipantType.INSTRUCTORS || recipientType == FeedbackParticipantType.NONE) {
            recipientSection = Const.DEFAULT_SECTION;
        } else if (recipientType == FeedbackParticipantType.TEAMS) {
            recipientSection = logic.getSectionForTeam(courseId, recipient);
        } else if (recipientType == FeedbackParticipantType.STUDENTS) {
            StudentAttributes student = logic.getStudentForEmail(courseId, recipient);
            recipientSection = student == null ? Const.DEFAULT_SECTION : student.section;
        } else {
            recipientSection = getUserSectionForCourse();
        }

        return responseBuilder.withRecipient(recipient).withRecipientSection(recipientSection);
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseQuestionType(int questionIndx,
            FeedbackResponseAttributes.Builder responseBuilder) {
        String feedbackQuestionType = getNonNullRequestParamValue(
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-" + questionIndx);
        return responseBuilder.withFeedbackQuestionType(FeedbackQuestionType.valueOf(feedbackQuestionType));
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseQuestionId(int questionIndx,
            FeedbackQuestionAttributes feedbackQuestionAttributes, FeedbackResponseAttributes.Builder responseBuilder) {
        String feedbackQuestionId = getNonNullRequestParamValue(
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
        Assumption.assertEquals("feedbackQuestionId Mismatch", feedbackQuestionAttributes.getId(),
                                feedbackQuestionId);
        return responseBuilder.withFeedbackQuestionId(feedbackQuestionId);
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseFeedbackSessionName(
            FeedbackResponseAttributes.Builder responseBuilder) {
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        return responseBuilder.withFeedbackSessionName(feedbackSessionName);
    }

    private FeedbackResponseAttributes.Builder extractFeedbackResponseId(int questionIndx, int responseIndx,
            FeedbackResponseAttributes.Builder responseBuilder) {
        // This field can be null if the response is new
        String feedbackResponseId = getRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-" + questionIndx + "-" + responseIndx);
        return responseBuilder.withFeedbackResponseId(feedbackResponseId);
    }

    /**
     * To be used to set any extra parameters or attributes that
     * a class inheriting FeedbackSubmissionEditSaveAction requires.
     */
    protected abstract void setAdditionalParameters() throws EntityDoesNotExistException;

    /**
     * To be used to test any constraints that a class inheriting FeedbackSubmissionEditSaveAction
     * needs. For example, this is used in moderations that check that instructors did not
     * respond to any question that they did not have access to during moderation.
     *
     * <p>Called after FeedbackSubmissionEditPageData data is set, and after setAdditionalParameters
     */
    protected abstract void checkAdditionalConstraints();

    /**
     * Note that when overriding this method, this should not use {@code respondingStudentList}
     * or {@code respondingInstructorList} of {@code FeedbackSessionAttributes}, because this method
     * is used to update {@code respondingStudentList} and {@code respondingInstructorList}.
     *
     * @return true if user has responses in the feedback session
     */
    protected boolean isUserRespondentOfSession() {
        // if there is no valid response on the form submission,
        // we need to use logic to check the database to handle cases where not all questions are displayed
        // e.g. on FeedbackQuestionSubmissionEditSaveAction,
        // or if the submitter can submit both as a student and instructor
        return hasValidResponse
            || logic.hasGiverRespondedForSession(getUserEmailForCourse(), feedbackSessionName, courseId);
    }

    protected abstract void appendRespondent();

    protected abstract void removeRespondent();

    protected abstract void verifyAccessibleForSpecificUser();

    protected abstract String getUserEmailForCourse();

    protected abstract String getUserTeamForCourse();

    protected abstract String getUserSectionForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract void setStatusToAdmin();

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);

    protected abstract RedirectResult createSpecificRedirectResult();
}

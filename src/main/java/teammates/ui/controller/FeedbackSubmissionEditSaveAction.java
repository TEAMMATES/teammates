package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseDetails;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.datastore.Text;

public abstract class FeedbackSubmissionEditSaveAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;
    protected boolean hasValidResponse;
    protected boolean isSendSubmissionEmail;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        
        setAdditionalParameters();
        verifyAccesibleForSpecificUser();
        
        String userEmailForCourse = getUserEmailForCourse();
        
        data = new FeedbackSubmissionEditPageData(account, student);
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
            String totalResponsesForQuestion =
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-" + questionIndx);
            
            if (totalResponsesForQuestion == null) {
                continue; // question has been skipped (not displayed).
            }
            
            List<FeedbackResponseAttributes> responsesForQuestion = new ArrayList<FeedbackResponseAttributes>();
            String questionId = HttpRequestHelper.getValueFromParamMap(
                    requestParameters,
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
            emailSet = StringHelper.recoverFromSanitizedText(emailSet);
            
            ArrayList<String> responsesRecipients = new ArrayList<String>();
            List<String> errors = new ArrayList<String>();
            
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
                    saveResponse(response);
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
                    saveResponse(response);
                }
            } else {
                List<StatusMessage> errorMessages = new ArrayList<StatusMessage>();
                
                for (String error : errors) {
                    errorMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
                }
                
                statusToUser.addAll(errorMessages);
                isError = true;
            }
            
        }
        
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
                student = new StudentAttributes();
                student.email = unregisteredStudentEmail;
                student.name = unregisteredStudentEmail;
                student.key = unregisteredStudentRegisterationKey;
            }
            Assumption.assertFalse(student == null && instructor == null);
            
            try {
                Calendar timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                EmailWrapper email = instructor == null
                        ? new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForStudent(session,
                                student, timestamp)
                        : new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session,
                                instructor, timestamp);
                emailSender.sendEmail(email);
            } catch (EmailSendingException e) {
                log.severe("Submission confirmation email failed to send: "
                           + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return createSpecificRedirectResult();
    }
    
    /**
     * If the {@code response} is an existing response, check that
     * the questionId and responseId that it has
     * is in {@code data.bundle.questionResponseBundle}
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
        List<String> existingResponsesId = new ArrayList<String>();
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            existingResponsesId.add(existingResponse.getId());
        }
        
        // checks if response id is valid
        return existingResponsesId.contains(response.getId());
    }

    private void saveResponse(FeedbackResponseAttributes response)
            throws EntityDoesNotExistException {
        boolean isExistingResponse = response.getId() != null;
        if (isExistingResponse) {
            // Delete away response if any empty fields
            if (response.responseMetaData.getValue().isEmpty() || response.recipient.isEmpty()) {
                logic.deleteFeedbackResponse(response);
                return;
            }
            try {
                logic.updateFeedbackResponse(response);
                hasValidResponse = true;
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                setStatusForException(e);
            }
        } else if (!response.responseMetaData.getValue().isEmpty()
                   && !response.recipient.isEmpty()) {
            try {
                logic.createFeedbackResponse(response);
                hasValidResponse = true;
            } catch (InvalidParametersException e) {
                setStatusForException(e);
            }
        }
    }
    
    private FeedbackResponseAttributes extractFeedbackResponseData(
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx,
            FeedbackQuestionAttributes feedbackQuestionAttributes) {
        
        FeedbackQuestionDetails questionDetails = feedbackQuestionAttributes.getQuestionDetails();
        FeedbackResponseAttributes response = new FeedbackResponseAttributes();
        
        // This field can be null if the response is new
        response.setId(HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-" + questionIndx + "-" + responseIndx));
                
        response.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("Null feedback session name", response.feedbackSessionName);
        
        response.courseId = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("Null feedback courseId", response.courseId);
        
        response.feedbackQuestionId = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
        Assumption.assertNotNull("Null feedbackQuestionId", response.feedbackQuestionId);
        Assumption.assertEquals("feedbackQuestionId Mismatch", feedbackQuestionAttributes.getId(),
                                response.feedbackQuestionId);
        
        response.recipient = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + questionIndx + "-" + responseIndx);
        Assumption.assertNotNull("Null feedback recipientEmail", response.recipient);
        
        String feedbackQuestionType = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-" + questionIndx);
        Assumption.assertNotNull("Null feedbackQuestionType", feedbackQuestionType);
        response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);
        
        FeedbackParticipantType recipientType = feedbackQuestionAttributes.recipientType;
        if (recipientType == FeedbackParticipantType.INSTRUCTORS || recipientType == FeedbackParticipantType.NONE) {
            response.recipientSection = Const.DEFAULT_SECTION;
        } else if (recipientType == FeedbackParticipantType.TEAMS) {
            response.recipientSection = StudentsLogic.inst().getSectionForTeam(courseId, response.recipient);
        } else if (recipientType == FeedbackParticipantType.STUDENTS) {
            StudentAttributes student = logic.getStudentForEmail(courseId, response.recipient);
            response.recipientSection = student == null ? Const.DEFAULT_SECTION : student.section;
        } else {
            response.recipientSection = getUserSectionForCourse();
        }
        
        // This field can be null if the question is skipped
        String paramName = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionIndx + "-" + responseIndx;
        String[] answer = HttpRequestHelper.getValuesFromParamMap(requestParameters, paramName);
        
        if (questionDetails.isQuestionSkipped(answer)) {
            response.responseMetaData = new Text("");
        } else {
            FeedbackResponseDetails responseDetails =
                    FeedbackResponseDetails.createResponseDetails(answer, questionDetails.getQuestionType(),
                                                                  questionDetails, requestParameters,
                                                                  questionIndx, responseIndx);
            response.setResponseDetails(responseDetails);
        }
        
        return response;
    }

    /**
     * To be used to set any extra parameters or attributes that
     * a class inheriting FeedbackSubmissionEditSaveAction requires
     */
    protected abstract void setAdditionalParameters() throws EntityDoesNotExistException;
    
    /**
     * To be used to test any constraints that a class inheriting FeedbackSubmissionEditSaveAction
     * needs. For example, this is used in moderations that check that instructors did not
     * respond to any question that they did not have access to during moderation.
     * 
     * Called after FeedbackSubmissionEditPageData data is set, and after setAdditionalParameters
     */
    protected abstract void checkAdditionalConstraints();
    
    /**
     * Note that when overriding this method, this should not use {@code respondingStudentList}
     * or {@code respondingInstructorList} of {@code FeedbackSessionAttributes}, because this method
     * is used to update {@code respondingStudentList} and {@code respondingInstructorList}
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
    
    protected abstract void verifyAccesibleForSpecificUser();

    protected abstract String getUserEmailForCourse();
    
    protected abstract String getUserTeamForCourse();
    
    protected abstract String getUserSectionForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract void setStatusToAdmin();

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);

    protected abstract RedirectResult createSpecificRedirectResult();
}

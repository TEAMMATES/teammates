package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackAbstractQuestionDetails;
import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionBundle;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.StudentsLogic;

public abstract class FeedbackQuestionSubmissionEditSaveAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected String feedbackQuestionId;
    protected FeedbackQuestionSubmissionEditPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        
        String totalResponsesForQuestion = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL);
        Assumption.assertNotNull(totalResponsesForQuestion);
        
        verifyAccesibleForSpecificUser();
        
        setStatusToAdmin();
        
        FeedbackSessionAttributes fs = logic.getFeedbackSession(feedbackSessionName, courseId);
        if (isSessionOpenForSpecificUser(fs) == false) {
            isError = true;
            statusToUser.add(Const.StatusMessages.FEEDBACK_SUBMISSION_EXCEEDED_DEADLINE);
            getPageData(getUserEmailForCourse());
            return createSpecificShowPageResult();
        }
        
        String userEmailForCourse = getUserEmailForCourse();
        String userSectionForCourse = getUserSectionForCourse();
        
        getPageData(userEmailForCourse);
        
        int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);
        List<FeedbackResponseAttributes> responsesForQuestion = new ArrayList<FeedbackResponseAttributes>();
        FeedbackAbstractQuestionDetails questionDetails  = data.bundle.question.getQuestionDetails();
        
        for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++) {
            FeedbackResponseAttributes response = extractFeedbackResponseData(requestParameters, 1, responseIndx, questionDetails);
            if(response.responseMetaData.getValue().isEmpty()){
                //deletes the response since answer is empty.
                saveResponse(response);
            } else {
                response.giverEmail = userEmailForCourse;
                response.giverSection = userSectionForCourse;
                responsesForQuestion.add(response);
            }
        }
        
        List<String> errors = questionDetails.validateResponseAttributes(responsesForQuestion, data.bundle.recipientList.size());
        
        if(errors.isEmpty()) {
            for(FeedbackResponseAttributes response : responsesForQuestion) {
                saveResponse(response);
            }
        } else {
            statusToUser.addAll(errors);
            isError = true;
        }
        
        if (isError == false) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        }
        
        getPageData(userEmailForCourse);
        
        data.isSessionOpenForSubmission = isSessionOpenForSpecificUser(fs);
        
        return createSpecificShowPageResult();
    }
    
    private void saveResponse(FeedbackResponseAttributes response)
            throws EntityDoesNotExistException {
        if (response.getId() != null) {
            // Delete away response if any empty fields
            if (response.responseMetaData.getValue().isEmpty() || 
                response.recipientEmail.isEmpty()) {
                logic.deleteFeedbackResponse(response);
                return;
            }
            try {
                logic.updateFeedbackResponse(response);
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                setStatusForException(e);
            }
        } else if (!response.responseMetaData.getValue().isEmpty() &&
                    !response.recipientEmail.isEmpty()){
            try {
                logic.createFeedbackResponse(response);
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                setStatusForException(e);
            }
        }
    }
    
    private FeedbackResponseAttributes extractFeedbackResponseData(Map<String, String[]> requestParameters, int questionIndx, int responseIndx, FeedbackAbstractQuestionDetails questionDetails) {
        FeedbackResponseAttributes response = new FeedbackResponseAttributes();
        
        //This field can be null if the response is new
        response.setId(HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
                
        response.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("Null feedback session name", response.feedbackSessionName);
        
        response.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("Null feedback courseId", response.courseId);
        
        response.feedbackQuestionId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertNotNull("Null feedbackQuestionId", response.feedbackQuestionId);
        
        response.recipientEmail = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
        Assumption.assertNotNull("Null feedback recipientEmail", response.recipientEmail);
        
        String feedbackQuestionType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        Assumption.assertNotNull("Null feedbackQuestionType", feedbackQuestionType);
        response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);
        
        FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst().getFeedbackQuestion(response.feedbackQuestionId);
        FeedbackParticipantType recipientType = question.recipientType;
        if(recipientType == FeedbackParticipantType.INSTRUCTORS || recipientType == FeedbackParticipantType.NONE){
            response.recipientSection = Const.DEFAULT_SECTION;
        } else if(recipientType == FeedbackParticipantType.TEAMS){
            response.recipientSection = StudentsLogic.inst().getSectionForTeam(courseId, response.recipientEmail);
        } else if(recipientType == FeedbackParticipantType.STUDENTS){
            response.recipientSection = logic.getStudentForEmail(courseId, response.recipientEmail).section;
        } else {
            response.recipientSection = getUserSectionForCourse();
        }
        
        //This field can be null if the question is skipped
        String[] answer = HttpRequestHelper.getValuesFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
        
        boolean allAnswersEmpty = true;
        if(answer!=null){
            for(int i=0 ; i<answer.length ; i++){
                if(answer[i]!=null || !answer[i].trim().isEmpty()){
                    allAnswersEmpty = false;
                }
            }
        }
        
        if(answer != null && !allAnswersEmpty) {
            FeedbackAbstractResponseDetails responseDetails = 
                    FeedbackAbstractResponseDetails.createResponseDetails(
                            answer,
                            questionDetails.questionType,
                            questionDetails);
            response.setResponseDetails(responseDetails);
        } else {
            response.responseMetaData = new Text("");
        }
        
        return response;
    }
    
    private void getPageData(String userEmailForCourse) throws EntityDoesNotExistException{
        data = new FeedbackQuestionSubmissionEditPageData(account);
        data.bundle = getDataBundle(userEmailForCourse);
    }

    protected abstract void verifyAccesibleForSpecificUser();
    
    protected abstract String getUserEmailForCourse();
    
    protected abstract String getUserSectionForCourse();
    
    protected abstract FeedbackQuestionBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;
    
    protected abstract void setStatusToAdmin();
    
    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs);
        
    protected abstract ShowPageResult createSpecificShowPageResult();
}

package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackAbstractQuestionDetails;
import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.StudentsLogic;

public abstract class FeedbackSubmissionEditSaveAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        
        verifyAccesibleForSpecificUser();
        
        String userEmailForCourse = getUserEmailForCourse();
        String userSectionForCourse = getUserSectionForCourse();
        data = new FeedbackSubmissionEditPageData(account, student);
        data.bundle = getDataBundle(userEmailForCourse);        
        Assumption.assertNotNull("Feedback session " + feedbackSessionName + " does not exist in " + courseId + ".", data.bundle);
        
        setStatusToAdmin();
        
        if (!isSessionOpenForSpecificUser(data.bundle.feedbackSession)) {
            isError = true;
            statusToUser.add(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN);
            return createSpecificRedirectResult();
        }
        
        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();
        for(int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String totalResponsesForQuestion = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-" + questionIndx);
            
            if (totalResponsesForQuestion == null) {
                continue; // question has been skipped (not displayed).
            }
            
            List<FeedbackResponseAttributes> responsesForQuestion = new ArrayList<FeedbackResponseAttributes>();
            String questionId = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, 
                    Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
            FeedbackQuestionAttributes questionAttributes = data.bundle.getQuestionAttributes(questionId);
            if(questionAttributes == null){
                statusToUser.add("The feedback session or questions may have changed while you were submitting. Please check your responses to make sure they are saved correctly.");
                isError = true;
                log.warning("Question not found. (deleted or invalid id passed?) id: "+ questionId + " index: " + questionIndx);
                continue;
            }
            FeedbackAbstractQuestionDetails questionDetails = questionAttributes.getQuestionDetails();

            
            int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);  
            String qnId = "";
            
            for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++) {
                FeedbackResponseAttributes response = extractFeedbackResponseData(requestParameters, questionIndx, responseIndx, questionDetails);
                if(response.responseMetaData.getValue().isEmpty()){
                    //deletes the response since answer is empty
                    saveResponse(response);
                } else {
                    response.giverEmail = userEmailForCourse;
                    response.giverSection = userSectionForCourse;
                    responsesForQuestion.add(response);
                }
                qnId = response.feedbackQuestionId;
            }
            
            List<String> errors = questionDetails.validateResponseAttributes(responsesForQuestion, data.bundle.recipientList.get(qnId).size());
            
            if(errors.isEmpty()) {
                for(FeedbackResponseAttributes response : responsesForQuestion) {
                    saveResponse(response);
                }
            } else {
                statusToUser.addAll(errors);
                isError = true;
            }
            
        }
        
        if (!isError) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        }
        
        // TODO: what happens if qn is deleted as response is being submitted?
        // what happens if team/etc change such that receiver / response in general is invalid?
        return createSpecificRedirectResult();
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
    
    private FeedbackResponseAttributes extractFeedbackResponseData(
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx, 
            FeedbackAbstractQuestionDetails questionDetails) {
        FeedbackResponseAttributes response = new FeedbackResponseAttributes();
        
        //This field can be null if the response is new
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
        
        response.recipientEmail = HttpRequestHelper.getValueFromParamMap(
                requestParameters, 
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + questionIndx + "-" + responseIndx);
        Assumption.assertNotNull("Null feedback recipientEmail", response.recipientEmail);
        
        String feedbackQuestionType = HttpRequestHelper.getValueFromParamMap(
                requestParameters, 
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-" + questionIndx);
        Assumption.assertNotNull("Null feedbackQuestionType", feedbackQuestionType);
        response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);
        
        FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst().getFeedbackQuestion(response.feedbackQuestionId);
        FeedbackParticipantType recipientType = question.recipientType;
        if(recipientType == FeedbackParticipantType.INSTRUCTORS || recipientType == FeedbackParticipantType.NONE){
            response.recipientSection = Const.DEFAULT_SECTION;
        } else if(recipientType == FeedbackParticipantType.TEAMS){
            response.recipientSection = StudentsLogic.inst().getSectionForTeam(courseId, response.recipientEmail);
        } else if(recipientType == FeedbackParticipantType.STUDENTS){
            StudentAttributes student = logic.getStudentForEmail(courseId, response.recipientEmail);
            response.recipientSection = (student == null) ? Const.DEFAULT_SECTION : student.section;
        } else {
            response.recipientSection = getUserSectionForCourse();
        }
        
        //This field can be null if the question is skipped
        String[] answer = HttpRequestHelper.getValuesFromParamMap(
                requestParameters, 
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionIndx + "-" + responseIndx);
        
        boolean allAnswersEmpty = true;
        if(answer!=null){
            for(int i=0 ; i<answer.length ; i++){
                if(!answer[i].trim().isEmpty()){
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
    
    protected abstract void verifyAccesibleForSpecificUser();

    protected abstract String getUserEmailForCourse();
    
    protected abstract String getUserSectionForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;

    protected abstract void setStatusToAdmin();

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);

    protected abstract RedirectResult createSpecificRedirectResult();
}

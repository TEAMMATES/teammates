package teammates.ui.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionEditAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        
        new GateKeeper().verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId), 
                                          logic.getFeedbackSession(feedbackSessionName, courseId),
                                          false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        String editType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);
        Assumption.assertNotNull("Null editType", editType);
        
        FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData(requestParameters);
        
        try {
            if (editType.equals("edit")) {
                String questionText = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
                Assumption.assertNotNull("Null question text", questionText);
                Assumption.assertNotEmpty("Empty question text", questionText);
                
                editQuestion(updatedQuestion);
            } else if (editType.equals("delete")) {
                // branch not tested because if it's not edit or delete, Assumption.fail will cause test failure
                deleteQuestion(updatedQuestion);
            } else {
                // Assumption.fails are not tested
                Assumption.fail("Invalid editType");
            }
        } catch (InvalidParametersException e) {
            // This part is not tested because GateKeeper handles if this happens, would be
            // extremely difficult to replicate a situation whereby it gets past GateKeeper
            setStatusForException(e);
        }
        
        return createRedirectResult(new PageData(account)
                                            .getInstructorFeedbackEditLink(courseId,feedbackSessionName));
    }

    private void deleteQuestion(FeedbackQuestionAttributes updatedQuestion) {
        logic.deleteFeedbackQuestionWithResponseRateCheck(updatedQuestion.getId());
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, StatusMessageColor.SUCCESS));
        statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">("
                        + updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                        + updatedQuestion.courseId + "]</span> deleted.<br>";
    }

    private void editQuestion(FeedbackQuestionAttributes updatedQuestion) throws InvalidParametersException, 
                                                                                 EntityDoesNotExistException {
        String err = validateQuestionGiverRecipientVisibility(updatedQuestion);
        
        if (!err.isEmpty()) {
            statusToUser.add(new StatusMessage(err, StatusMessageColor.DANGER));
            isError = true;
        }
        
        if (updatedQuestion.questionNumber != 0) { // Question number was updated
            List<String> questionDetailsErrors = updatedQuestion.getQuestionDetails().validateQuestionDetails();
            List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<StatusMessage>();
            
            for (String error : questionDetailsErrors) {
                questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
            }
            
            // if error is not empty not tested as extractFeedbackQuestionData method uses Assumptions to cover it
            if (!questionDetailsErrors.isEmpty()) {
                statusToUser.addAll(questionDetailsErrorsMessages);
                isError = true;
            } else {
                logic.updateFeedbackQuestionNumber(updatedQuestion);
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, StatusMessageColor.SUCCESS));
            }
        } else {
            List<String> questionDetailsErrors = updatedQuestion.getQuestionDetails().validateQuestionDetails();
            List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<StatusMessage>();
            
            for (String error : questionDetailsErrors) {
                questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
            }
            
            // if error is not empty not tested as extractFeedbackQuestionData method uses Assumptions to cover it
            if (!questionDetailsErrors.isEmpty()) {
                statusToUser.addAll(questionDetailsErrorsMessages);
                isError = true;
            } else {
                logic.updateFeedbackQuestionWithResponseRateCheck(updatedQuestion);    
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, StatusMessageColor.SUCCESS));
                statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber 
                                + " for session:<span class=\"bold\">("
                                + updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                                + updatedQuestion.courseId + "]</span> edited.<br>"
                                + "<span class=\"bold\">" 
                                + updatedQuestion.getQuestionDetails().getQuestionTypeDisplayName() + ":</span> "
                                + updatedQuestion.getQuestionDetails().questionText;
            }
        }
    }
    
    /**
     * Validates that the giver and recipient for the given FeedbackQuestionAttributes is valid for its question type.
     * Validates that the visibility for the given FeedbackQuestionAttributes is valid for its question type.
     * 
     * @param feedbackQuestionAttributes
     * @return error message detailing the error, or an empty string if valid.
     */
    public static String validateQuestionGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";
        
        FeedbackQuestionDetails questionDetails = null;
        Class<? extends FeedbackQuestionDetails> questionDetailsClass = feedbackQuestionAttributes
                                                                            .questionType.getQuestionDetailsClass();
        Constructor<? extends FeedbackQuestionDetails> questionDetailsClassConstructor;
        
        try {
            questionDetailsClassConstructor = questionDetailsClass.getConstructor();
            questionDetails = questionDetailsClassConstructor.newInstance();
            Method m = questionDetailsClass.getMethod("validateGiverRecipientVisibility", 
                                                      FeedbackQuestionAttributes.class);
            errorMsg = (String) m.invoke(questionDetails, feedbackQuestionAttributes);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            // Assumption.fails are not tested
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for "
                            + feedbackQuestionAttributes.questionType.toString() + " question type.");
        }
        
        return errorMsg;
    }

    private static FeedbackQuestionAttributes extractFeedbackQuestionData(Map<String, String[]> requestParameters) {
        FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();
        
        newQuestion.setId(HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                                 Const.ParamsNames.FEEDBACK_QUESTION_ID));
        Assumption.assertNotNull("Null question id", newQuestion.getId());
        
        newQuestion.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                                      Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("Null course id", newQuestion.courseId);
        
        newQuestion.feedbackSessionName = 
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("Null feedback session name", newQuestion.feedbackSessionName);
        
        // TODO thoroughly investigate when and why these parameters can be null
        // and check all possibilities in the tests
        // should only be null when deleting. might be good to separate the delete action from this class
        
        // When editing, usually the following fields are not null. If they are null somehow(edit from browser),
        // Then the field will not update and take on its old value.
        // When deleting, the following fields are null.
        // numofrecipients
        // questiontext
        // numofrecipientstype
        // recipienttype
        // receiverLeaderCheckbox
        // givertype
        
        // Can be null
        String giverType = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                                  Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
        if (giverType != null) {
            newQuestion.giverType = FeedbackParticipantType.valueOf(giverType);
        }
        
        // Can be null
        String recipientType = 
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
        if (recipientType != null) {
            newQuestion.recipientType = FeedbackParticipantType.valueOf(recipientType);
        }

        String questionNumber = 
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        Assumption.assertNotNull("Null question number", questionNumber);
        newQuestion.questionNumber = Integer.parseInt(questionNumber);
        // 0 for no change in question number.
        Assumption.assertTrue("Invalid question number", newQuestion.questionNumber >= 0);
        
        // Can be null
        String nEntityTypes = 
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
        
        if (numberOfEntitiesIsUserDefined(newQuestion.recipientType, nEntityTypes)) {
            String nEntities;
            nEntities = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                               Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
            Assumption.assertNotNull(nEntities);
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(nEntities);
        } else {
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        }
        
        newQuestion.showResponsesTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));                
        newQuestion.showGiverNameTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));        
        newQuestion.showRecipientNameTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));    
        
        String questionType = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        Assumption.assertNotNull(questionType);
        newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);
        
        // Can be null
        String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        if (questionText != null && !questionText.isEmpty()) {
            FeedbackQuestionDetails questionDetails = FeedbackQuestionDetails.createQuestionDetails(
                    requestParameters, newQuestion.questionType);
            newQuestion.setQuestionDetails(questionDetails);
        }
        
        return newQuestion;
    }
    
    private static boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType, String nEntityTypes) {
        if (recipientType != FeedbackParticipantType.STUDENTS
            && recipientType != FeedbackParticipantType.TEAMS) {
            return false;
        }
        
        if (nEntityTypes.equals("custom") == false) {
            return false;
        }
        
        return true;
    }

    private static List<FeedbackParticipantType> getParticipantListFromParams(String params) {
        List<FeedbackParticipantType> list = new ArrayList<FeedbackParticipantType>();
        
        if (params.isEmpty()) { return list; }    
        
        String[] splitString = params.split(",");
        
        for (String str : splitString) {
            list.add(FeedbackParticipantType.valueOf(str));
        }
        
        return list;
    }
}

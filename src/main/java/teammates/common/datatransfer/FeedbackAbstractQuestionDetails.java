package teammates.common.datatransfer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

/** A class holding the details for a specific question type.
+ * This abstract class is inherited by concrete Feedback*QuestionDetails
+ * classes which provides the implementation for the various abstract methods
+ * such that pages can render the correct information/forms depending on the 
+ * question type
+ */
public abstract class FeedbackAbstractQuestionDetails {
    public FeedbackQuestionType questionType;
    public String questionText;
    
    protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType){
        this.questionType = questionType;
    }
    
    protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType,
            String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
        
    public abstract String getQuestionTypeDisplayName();
    
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId,
            FeedbackAbstractResponseDetails existingResponseDetails);
    
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId);
    
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);
    
    public abstract String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId);
    
    public abstract String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view);
    
    public abstract String getQuestionResultStatisticsCsv(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle);
    
    public abstract boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails);
    
    public abstract String getCsvHeader();
    
    /**
     * Validates the question details
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an empty list if question details are valid.
     */
    public abstract List<String> validateQuestionDetails();
    
    /**
     * Validates {@code List<FeedbackResponseAttributes>} for the question based on the current {@code Feedback*QuestionDetails}.
     * @param responses - The {@code List<FeedbackResponseAttributes>} for the question to be validated
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an empty list if question responses are valid.
     */
    public abstract List<String> validateResponseAttributes(List<FeedbackResponseAttributes> responses, int numRecipients);
    
    public static FeedbackAbstractQuestionDetails createQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType) {
        String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        Assumption.assertNotNull("Null question text", questionText);
        Assumption.assertNotEmpty("Empty question text", questionText);
        
        FeedbackAbstractQuestionDetails questionDetails = null;
        
        switch(questionType){
        case TEXT:
            questionDetails = new FeedbackTextQuestionDetails(questionText);
            break;
        case MCQ:
            int numOfMcqChoices = 0;
            List<String> mcqChoices = new LinkedList<String>();
            boolean mcqOtherEnabled = false; // TODO change this when implementing "other, please specify" field
            
            String generatedMcqOptions = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS);
            if (generatedMcqOptions.equals(FeedbackParticipantType.NONE.toString())) {
                String numMcqChoicesCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
                Assumption.assertNotNull("Null number of choice for MCQ", numMcqChoicesCreatedString);
                int numMcqChoicesCreated = Integer.parseInt(numMcqChoicesCreatedString);
                
                for(int i = 0; i < numMcqChoicesCreated; i++) {
                    String mcqChoice = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i);
                    if(mcqChoice != null && !mcqChoice.trim().isEmpty()) {
                        mcqChoices.add(mcqChoice);
                        numOfMcqChoices++;
                    }
                }
                
                questionDetails = new FeedbackMcqQuestionDetails(questionText, numOfMcqChoices, mcqChoices, mcqOtherEnabled);
            } else {
                questionDetails = new FeedbackMcqQuestionDetails(questionText, FeedbackParticipantType.valueOf(generatedMcqOptions));
            }
                        
            break;
        case MSQ:
            int numOfMsqChoices = 0;
            List<String> msqChoices = new LinkedList<String>();
            boolean msqOtherEnabled = false; // TODO change this when implementing "other, please specify" field
                
            String generatedMsqOptions = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS);
            if (generatedMsqOptions.equals(FeedbackParticipantType.NONE.toString())) {
                String numMsqChoicesCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
                Assumption.assertNotNull("Null number of choice for MSQ", numMsqChoicesCreatedString);
                int numMsqChoicesCreated = Integer.parseInt(numMsqChoicesCreatedString);
                
                for(int i = 0; i < numMsqChoicesCreated; i++) {
                    String msqChoice = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-" + i);
                    if(msqChoice != null && !msqChoice.trim().isEmpty()) {
                        msqChoices.add(msqChoice);
                        numOfMsqChoices++;
                    }
                }
            
                questionDetails = new FeedbackMsqQuestionDetails(questionText, numOfMsqChoices, msqChoices, msqOtherEnabled);
            } else {
                questionDetails = new FeedbackMsqQuestionDetails(questionText, FeedbackParticipantType.valueOf(generatedMsqOptions));
            }
            break;
        case NUMSCALE:
            String minScaleString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN);
            Assumption.assertNotNull("Null minimum scale", minScaleString);
            int minScale = Integer.parseInt(minScaleString);
            
            String maxScaleString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX);
            Assumption.assertNotNull("Null maximum scale", maxScaleString);
            int maxScale = Integer.parseInt(maxScaleString);
            
            String stepString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP);
            Assumption.assertNotNull("Null step", stepString);
            Double step = Double.parseDouble(stepString);

            questionDetails = 
                    new FeedbackNumericalScaleQuestionDetails(questionText, minScale, maxScale, step);
            break;
        case CONSTSUM:
            int numOfConstSumOptions = 0;
            List<String> constSumOptions = new LinkedList<String>();
            String distributeToRecipientsString = null;
            String pointsPerOptionString = null;
            String pointsString = null;
            boolean distributeToRecipients = false;
            boolean pointsPerOption = false;
            int points = 0;
            
            distributeToRecipientsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS);
            pointsPerOptionString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION);
            pointsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS);
            Assumption.assertNotNull("Null points", pointsString);
            
            distributeToRecipients = (distributeToRecipientsString == null) ? false : (distributeToRecipientsString.equals("true")? true : false);
            pointsPerOption = (pointsPerOptionString == null) ? false : pointsPerOptionString.equals("true") ? true : false;
            points = Integer.parseInt(pointsString);
            
            if (!distributeToRecipients) {
                String numConstSumOptionsCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
                Assumption.assertNotNull("Null number of choice for ConstSum", numConstSumOptionsCreatedString);
                int numConstSumOptionsCreated = Integer.parseInt(numConstSumOptionsCreatedString);
                
                for(int i = 0; i < numConstSumOptionsCreated; i++) {
                    String constSumOption = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-" + i);
                    if(constSumOption != null && !constSumOption.trim().isEmpty()) {
                        constSumOptions.add(constSumOption);
                        numOfConstSumOptions++;
                    }
                }
                questionDetails = new FeedbackConstantSumQuestionDetails(questionText, numOfConstSumOptions, constSumOptions, pointsPerOption, points);
            } else {
                questionDetails = new FeedbackConstantSumQuestionDetails(questionText, pointsPerOption, points);
            }
            break;
        case CONTRIB:
            questionDetails = new FeedbackContributionQuestionDetails(questionText);
            break;
        default:
            Assumption.fail("Question type not supported by FeedbackAbstractQuestionDetails");
            break;
        }
        
        return questionDetails;
    }
}
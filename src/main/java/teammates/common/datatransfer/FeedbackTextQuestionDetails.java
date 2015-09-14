package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackTextQuestionDetails extends FeedbackQuestionDetails {
    
    public FeedbackTextQuestionDetails() {
        super(FeedbackQuestionType.TEXT);
    }
    
    public FeedbackTextQuestionDetails(String questionText) {
        super(FeedbackQuestionType.TEXT, questionText);
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        // Nothing to do here.
        return true;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.TEXT;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackResponseDetails existingResponseDetails) {
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.TEXT_SUBMISSION_FORM,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${existingResponse}", Sanitizer.sanitizeForHtml(existingResponseDetails.getAnswerString()));
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.TEXT_SUBMISSION_FORM,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${existingResponse}", "");
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        return "";
    }
    
    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        return "";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        return "";
    }
    
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        if (responses.isEmpty()) {
            return "";
        }
        
        @SuppressWarnings("unused")
        String html = "";
        int averageLength = 0;
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        int numResponses = 0;
        int totalLength = 0;
        
        for(FeedbackResponseAttributes response : responses){
            numResponses++;
            String answerString = response.getResponseDetails().getAnswerString();
            minLength = (StringHelper.countWords(answerString) < minLength) ? StringHelper.countWords(answerString) : minLength;
            maxLength = (StringHelper.countWords(answerString) > maxLength) ? StringHelper.countWords(answerString) : maxLength;
            totalLength += StringHelper.countWords(answerString);
        }
        
        averageLength = totalLength/numResponses;
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        FeedbackQuestionFormTemplates.TEXT_RESULT_STATS,
                        "${averageLength}", Integer.toString(averageLength),
                        "${minLength}", (minLength == Integer.MAX_VALUE)? "-" : Integer.toString(minLength),
                        "${maxLength}", (maxLength == Integer.MIN_VALUE)? "-" : Integer.toString(maxLength));
        
        //TODO: evaluate what statistics are needed for text questions later.
        return "";
    }
    

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        return "";
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<option value = \"TEXT\">"+Const.FeedbackQuestionTypeNames.TEXT+"</option>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

}

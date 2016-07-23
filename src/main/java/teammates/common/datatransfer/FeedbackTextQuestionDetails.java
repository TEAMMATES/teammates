package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

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
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        return Templates.populateTemplate(
                FormTemplates.TEXT_SUBMISSION_FORM,
                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.TEXT_EXISTING_RESPONSE, Sanitizer.sanitizeForHtml(existingResponseDetails.getAnswerString()));
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        return Templates.populateTemplate(
                FormTemplates.TEXT_SUBMISSION_FORM,
                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.TEXT_EXISTING_RESPONSE, "");
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
        
        String html = "";
        /*
        int averageLength = 0;
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        int numResponses = 0;
        int totalLength = 0;
        
        for(FeedbackResponseAttributes response : responses){
            numResponses++;
            String answerString = response.getResponseDetails().getAnswerString();
            minLength = StringHelper.countWords(answerString) < minLength
                        ? StringHelper.countWords(answerString)
                        : minLength;
            maxLength = StringHelper.countWords(answerString) > maxLength
                        ? StringHelper.countWords(answerString)
                        : maxLength;
            totalLength += StringHelper.countWords(answerString);
        }
        
        averageLength = totalLength/numResponses;
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        FeedbackQuestionFormTemplates.TEXT_RESULT_STATS,
                        "${averageLength}", Integer.toString(averageLength),
                        "${minLength}", (minLength == Integer.MAX_VALUE)? "-" : Integer.toString(minLength),
                        "${maxLength}", (maxLength == Integer.MIN_VALUE)? "-" : Integer.toString(maxLength));
        */
        //TODO: evaluate what statistics are needed for text questions later.
        return html;
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
        return "<li data-questiontype = \"TEXT\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.TEXT + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        return new ArrayList<String>();
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        return new ArrayList<String>();
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

}

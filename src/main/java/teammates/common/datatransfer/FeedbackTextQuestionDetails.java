package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;

public class FeedbackTextQuestionDetails extends
        FeedbackAbstractQuestionDetails {
    public FeedbackTextQuestionDetails() {
        super(FeedbackQuestionType.TEXT);
    }
    
    public FeedbackTextQuestionDetails(String questionText) {
        super(FeedbackQuestionType.TEXT, questionText);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.TEXT;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackAbstractResponseDetails existingResponseDetails) {
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
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        return "";
    }
    
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses) {
        // TODO Auto-generated method stub
        return "";
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

}

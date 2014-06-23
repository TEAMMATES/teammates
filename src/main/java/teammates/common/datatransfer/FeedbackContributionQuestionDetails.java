package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;

public class FeedbackContributionQuestionDetails extends FeedbackAbstractQuestionDetails {
    
    public FeedbackContributionQuestionDetails() {
        super(FeedbackQuestionType.CONTRIB);
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.CONTRIB;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackAbstractResponseDetails existingResponseDetails) {

        FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) existingResponseDetails;
        int points = frd.getAnswer();
        String optionSelectFragmentsHtml = getContributionOptionsHtml(points);
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${contribSelectFragmentsHtml}", optionSelectFragmentsHtml);
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {

        String optionSelectHtml = getContributionOptionsHtml(Const.INT_UNINITIALIZED);
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${contribSelectFragmentsHtml}", optionSelectHtml);
        
        return html;
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
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        if(responses.size() == 0){
            return "";
        }
        
        String html = "";
        
        return html;
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

    final String ERROR_INVALID_OPTION = "Invalid option for the " + Const.FeedbackQuestionTypeNames.CONTRIB + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        /*
        for(FeedbackResponseAttributes response : responses){
            FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) response.getResponseDetails();
            if(frd.getAnswer() > 21 || frd.getAnswer()<0){
                errors.add(ERROR_INVALID_OPTION);
            }
        }*/
        return errors;
    }
    
    
    /*
     * The functions below are taken and modified from EvalSubmissionEditPageData.java
     * -------------------------------------------------------------------------------
     */
    
    /**
     * Returns the options for contribution share in a team. 
     */
    private String getContributionOptionsHtml(int points){
        String result = "";
        if(points==Const.POINTS_NOT_SUBMITTED || points==Const.INT_UNINITIALIZED ){
            points=Const.POINTS_NOT_SURE;
        }
        for(int i=200; i>=0; i-=10){
            result += "<option value=\"" + i + "\"" +
                        (i==points
                        ? "selected=\"selected\""
                        : "") +
                        ">" + convertToEqualShareFormat(i) +
                        "</option>\r\n";
        }
        result+="<option value=\"" + Const.POINTS_NOT_SURE + "\""
                + (points==Const.POINTS_NOT_SURE ? " selected=\"selected\"" : "") + ">" +
                "Not Sure</option>";
        return result;
    }
    
    public static String convertToEqualShareFormat(int i) {
        if (i > 100)
            return "Equal share + " + (i - 100) + "%"; // Do more
        else if (i == 100)
            return "Equal share"; // Do same
        else if (i > 0)
            return "Equal share - " + (100 - i) + "%"; // Do less
        else
            return "0%"; // Do none
    }

}

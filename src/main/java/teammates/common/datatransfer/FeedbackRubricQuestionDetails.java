package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;

public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {
    public int numOfRubricChoices;
    public List<String> rubricChoices;
    public int numOfRubricSubQuestions;
    public List<String> rubricSubQuestions;
    public List<List<String>> rubricDescriptions;
    
    public FeedbackRubricQuestionDetails() {
        super(FeedbackQuestionType.RUBRIC);
        
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<String>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<String>();
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        
        return true;
    }

    private void setRubricQuestionDetails(int numOfRubricChoices,
            List<String> rubricChoices,
            int numOfRubricSubQuestions,
            List<String> rubricSubQuestions,
            List<List<String>> rubricDescriptions) {
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RUBRIC;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRubricQuestionDetails newRubricDetails = (FeedbackRubricQuestionDetails) newDetails;
        // TODO: need to check for exact match.
        
        // Responses requires deletion if choices change
        if (this.numOfRubricChoices != newRubricDetails.numOfRubricChoices ||
            this.rubricChoices.containsAll(newRubricDetails.rubricChoices) == false ||
            newRubricDetails.rubricChoices.containsAll(this.rubricChoices) == false) {
            return true;
        }
        
        // Responses requires deletion if sub-questions change
        if (this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions ||
            this.rubricSubQuestions.containsAll(newRubricDetails.rubricChoices) == false ||
            newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions) == false) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackResponseDetails existingResponseDetails) {
        
        return "";
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        
        return "";
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        String questionNumberString = Integer.toString(questionNumber);
        
        
        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_HEADER_FRAGMENT;
        for(int i = 0 ; i < numOfRubricChoices ; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${qnIndex}", questionNumberString,
                            "${col}", Integer.toString(i),
                            "${rubricChoiceValue}", rubricChoices.get(i),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE);
            tableHeaderFragmentHtml.append(optionFragment + Const.EOL);
        }
        
        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();
        
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_BODY;
        
        for(int j = 0 ; j < numOfRubricSubQuestions ; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for(int i = 0 ; i < numOfRubricChoices ; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${col}", Integer.toString(i),
                                "${row}", Integer.toString(j),
                                "${description}", "",//TODO: populate this.
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION);
                tableBodyFragmentHtml.append(optionFragment + Const.EOL);
            }
            
            // Get entire row
            String optionFragment2 = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${row}", Integer.toString(j),
                            "${subQuestion}", rubricSubQuestions.get(j),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString(),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION);
            tableBodyHtml.append(optionFragment2 + Const.EOL);
        }
        
        // Append last row of table body
        String addRowFragment = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_BODY_ADD_ROW_FRAGMENT;
        for(int i = 0 ; i < numOfRubricChoices + 1 ; i++) {
            //addRowFragment += "<td></td>";
        }
        tableBodyHtml.append(addRowFragment);
        
        
        // Create edit form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM,
                "${qnIndex}", questionNumberString,
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml.toString(),
                "${tableBodyHtml}", tableBodyHtml.toString());
        
        return html;
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty choices by default
        this.numOfRubricChoices = 2;
        this.rubricChoices.add("Choice 1");
        this.rubricChoices.add("Choice 2");
        
        // Add two empty sub-questions by default
        this.numOfRubricSubQuestions = 2;
        this.rubricSubQuestions.add("sub-question 1");
        this.rubricSubQuestions.add("sub-question 2");
        
        return "<div id=\"rubricForm\">" + 
                    this.getQuestionSpecificEditFormHtml(-1) +
               "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        return "";
    }
    
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view) {
        
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
        return "<option value = \"RUBRIC\">"+Const.FeedbackQuestionTypeNames.RUBRIC+"</option>";
    }

    final int MIN_NUM_OF_RUBRIC_CHOICES = 2;
    final String ERROR_NOT_ENOUGH_RUBRIC_CHOICES = "Too little choices for "+Const.FeedbackQuestionTypeNames.RUBRIC+". Minimum number of options is: ";
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

    final String ERROR_INVALID_OPTION = " is not a valid option for the " + Const.FeedbackQuestionTypeNames.RUBRIC + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }


}

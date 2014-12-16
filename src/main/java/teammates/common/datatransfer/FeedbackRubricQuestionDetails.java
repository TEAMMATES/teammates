package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;

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
        String numOfRubricChoicesString = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricNumCols");
        String numOfRubricSubQuestionsString = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricNumRows");
        
        int numOfRubricChoices = Integer.parseInt(numOfRubricChoicesString);
        int numOfRubricSubQuestions = Integer.parseInt(numOfRubricSubQuestionsString);
        List<String> rubricChoices = new ArrayList<String>();
        List<String> rubricSubQuestions = new ArrayList<String>();
        List<List<String>> rubricDescriptions = new ArrayList<List<String>>();
        
        int numActualChoices = 0;
        int numActualSubQuestions = 0;
        
        // Get list of choices
        for(int i = 0 ; i<numOfRubricChoices ; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricChoice" + "-" + i);
            if(choice != null) {
                rubricChoices.add(choice);
                numActualChoices++;
            }
        }
        
        // Get list of sub-questions
        for(int i = 0 ; i<numOfRubricSubQuestions ; i++) {
            String subQuestion = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricSubQn" + "-" + i);
            if(subQuestion != null) {
                rubricSubQuestions.add(subQuestion);
                numActualSubQuestions++;
            }
        }
        
        // Get descriptions
        for(int i = 0 ; i<numOfRubricSubQuestions ; i++) {
            rubricDescriptions.add(new ArrayList<String>());
            for(int j = 0 ; j<numOfRubricChoices ; j++) {
                String description = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricDesc" + "-" + i + "-" + j);
                if(description != null) {
                    rubricDescriptions.get(i).add(description);
                }
            }
        }
        
        // Set details
        setRubricQuestionDetails(numActualChoices, rubricChoices,
                numActualSubQuestions, rubricSubQuestions, rubricDescriptions);
        
        // Assert sizes of description, choices, sub-qns
        // TODO: move this to validateQuestionDetails and handle properly.
        Assumption.assertTrue(validDescriptionSize());
        
        
        return true;
    }
    
    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     * @return
     */
    private boolean validDescriptionSize() {
        if (this.rubricDescriptions.size() != this.numOfRubricSubQuestions) {
            return false;
        }
        for (int i=0 ; i<this.rubricDescriptions.size() ; i++) {
            if (this.rubricDescriptions.get(i).size() != this.numOfRubricChoices) {
                return false;
            }
        }
        return true;
    }

    private void setRubricQuestionDetails(int numOfRubricChoices,
            List<String> rubricChoices,
            int numOfRubricSubQuestions,
            List<String> rubricSubQuestions,
            List<List<String>> rubricDescriptions) {
        this.numOfRubricChoices = numOfRubricChoices;
        this.rubricChoices = rubricChoices;
        this.numOfRubricSubQuestions = numOfRubricSubQuestions;
        this.rubricSubQuestions = rubricSubQuestions;
        this.rubricDescriptions = rubricDescriptions;
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
                                "${description}", this.getDescription(j, i),
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
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
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
        
        initializeRubricDescriptions();
        
        return "<div id=\"rubricForm\">" + 
                    this.getQuestionSpecificEditFormHtml(-1) +
               "</div>";
    }
    
    private void initializeRubricDescriptions() {
        rubricDescriptions = new ArrayList<List<String>>();
        for (int subQns=0 ; subQns<this.numOfRubricSubQuestions ; subQns++) {
            List<String> descList = new ArrayList<String>();
            for (int ch=0 ; ch<this.numOfRubricChoices ; ch++) {
                descList.add("");
            }
            rubricDescriptions.add(descList);
        }
    }
    
    /**
     * Gets the description for given sub-question and choice
     * @param subQuestion
     * @param choice
     */
    public String getDescription(int subQuestion, int choice) {
        return this.rubricDescriptions.get(subQuestion).get(choice);
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

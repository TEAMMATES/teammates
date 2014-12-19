package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

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
        int descRows = -1;
        for(int i = 0 ; i<numOfRubricSubQuestions ; i++) {
            for(int j = 0 ; j<numOfRubricChoices ; j++) {
                String description = HttpRequestHelper.getValueFromParamMap(requestParameters, "rubricDesc" + "-" + i + "-" + j);
                if(description != null) {
                    if (j==0) {
                        descRows++;
                        rubricDescriptions.add(new ArrayList<String>());
                    }
                    rubricDescriptions.get(descRows).add(description);
                }
            }
        }
        
        // Set details
        setRubricQuestionDetails(numActualChoices, rubricChoices,
                numActualSubQuestions, rubricSubQuestions, rubricDescriptions);
        
        return true;
    }
    
    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     * @return
     */
    private boolean isValidDescriptionSize() {
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
            this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions) == false ||
            newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions) == false) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackResponseDetails existingResponseDetails) {
        
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) existingResponseDetails;
        
        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);
        
        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT;
        for(int i = 0 ; i < numOfRubricChoices ; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${col}", Integer.toString(i),
                            "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(i)));
            tableHeaderFragmentHtml.append(optionFragment + Const.EOL);
        }
        
        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();
        
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY;
        
        for(int j = 0 ; j < numOfRubricSubQuestions ; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for(int i = 0 ; i < numOfRubricChoices ; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${respIndex}", responseNumberString,
                                "${col}", Integer.toString(i),
                                "${row}", Integer.toString(j),
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(j, i)),
                                "${checked}", (frd.getAnswer(j) == i)? "checked":"", //Check if existing choice for sub-question == current choice
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE);
                tableBodyFragmentHtml.append(optionFragment + Const.EOL);
            }
            
            // Get entire row
            String optionFragment2 = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${row}", Integer.toString(j),
                            "${subQuestion}", StringHelper.integerToBase26String(j+1) + ") "+ Sanitizer.sanitizeForHtml(rubricSubQuestions.get(j)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString());
            tableBodyHtml.append(optionFragment2 + Const.EOL);
        }
        
        // Create edit form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM,
                "${qnIndex}", questionNumberString,
                "${respIndex}", responseNumberString,
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml.toString(),
                "${tableBodyHtml}", tableBodyHtml.toString(),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);
        
        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT;
        for(int i = 0 ; i < numOfRubricChoices ; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${col}", Integer.toString(i),
                            "${rubricChoiceValue}", rubricChoices.get(i));
            tableHeaderFragmentHtml.append(optionFragment + Const.EOL);
        }
        
        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();
        
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY;
        
        for(int j = 0 ; j < numOfRubricSubQuestions ; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for(int i = 0 ; i < numOfRubricChoices ; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${respIndex}", responseNumberString,
                                "${col}", Integer.toString(i),
                                "${row}", Integer.toString(j),
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(j, i)),
                                "${checked}", "",
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE);
                tableBodyFragmentHtml.append(optionFragment + Const.EOL);
            }
            
            // Get entire row
            String optionFragment2 = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${row}", Integer.toString(j),
                            "${subQuestion}", StringHelper.integerToBase26String(j+1) + ") "+ Sanitizer.sanitizeForHtml(rubricSubQuestions.get(j)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString());
            tableBodyHtml.append(optionFragment2 + Const.EOL);
        }
        
        // Create edit form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM,
                "${qnIndex}", questionNumberString,
                "${respIndex}", responseNumberString,
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml.toString(),
                "${tableBodyHtml}", tableBodyHtml.toString(),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
        
        return html;
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
                            "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(i)),
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
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(j, i)),
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION);
                tableBodyFragmentHtml.append(optionFragment + Const.EOL);
            }
            
            // Get entire row
            String optionFragment2 = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${row}", Integer.toString(j),
                            "${subQuestion}", Sanitizer.sanitizeForHtml(rubricSubQuestions.get(j)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString(),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION);
            tableBodyHtml.append(optionFragment2 + Const.EOL);
        }
        
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
        StringBuilder subQuestionListHtml = new StringBuilder();
        
        if(numOfRubricSubQuestions > 0){
            subQuestionListHtml.append("<p>");
            for(int i = 0; i < numOfRubricSubQuestions; i++) {
                String subQuestionFragment = 
                        StringHelper.integerToBase26String(i+1) + ") "+ rubricSubQuestions.get(i);
                subQuestionListHtml.append(subQuestionFragment);
                subQuestionListHtml.append("<br>");
            }
            subQuestionListHtml.append("</p>");
        }
        
        
        String additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${rubricAdditionalInfoFragments}", subQuestionListHtml.toString());
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
        
        return html;
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

    final int MIN_NUM_OF_RUBRIC_SUB_QUESTIONS = 1;
    final String ERROR_NOT_ENOUGH_RUBRIC_SUB_QUESTIONS = "Too little sub-questions for "+Const.FeedbackQuestionTypeNames.RUBRIC+". Minimum number of sub-questions is: ";
    
    final String ERROR_RUBRIC_DESC_INVALID_SIZE = "Invalid number of descriptions for "+Const.FeedbackQuestionTypeNames.RUBRIC;
    
    final String ERROR_RUBRIC_EMPTY_CHOICE = "Choices for "+Const.FeedbackQuestionTypeNames.RUBRIC + " cannot be empty.";
    final String ERROR_RUBRIC_EMPTY_SUB_QUESTION = "Sub-questions for "+Const.FeedbackQuestionTypeNames.RUBRIC + " cannot be empty.";
    
    /**
     * For rubric questions,
     *      1) Description size should be valid
     *      2) At least 2 choices
     *      3) At least 1 sub-question
     *      4) Choices and sub-questions should not be empty
     */
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        
        if (!isValidDescriptionSize()) {
            errors.add(ERROR_RUBRIC_DESC_INVALID_SIZE);
        }
        
        if (this.numOfRubricChoices < MIN_NUM_OF_RUBRIC_CHOICES) {
            errors.add(ERROR_NOT_ENOUGH_RUBRIC_CHOICES + MIN_NUM_OF_RUBRIC_CHOICES);
        }
        
        if (this.numOfRubricSubQuestions < MIN_NUM_OF_RUBRIC_SUB_QUESTIONS) {
            errors.add(ERROR_NOT_ENOUGH_RUBRIC_SUB_QUESTIONS + MIN_NUM_OF_RUBRIC_SUB_QUESTIONS);
        }
        
        for (String choice : this.rubricChoices) {
            if (choice.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_CHOICE);
                break;
            }
        }
        
        for (String subQn : this.rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_SUB_QUESTION);
                break;
            }
        }
        
        return errors;
    }

    final String ERROR_INVALID_CHOICE = "An invalid choice was chosen for the " + Const.FeedbackQuestionTypeNames.RUBRIC + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }


}

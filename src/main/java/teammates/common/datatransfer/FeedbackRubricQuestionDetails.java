package teammates.common.datatransfer;

import java.text.DecimalFormat;
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
        this.initializeRubricDescriptions();
    }
    
    public FeedbackRubricQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RUBRIC, questionText);
        
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<String>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<String>();
        this.initializeRubricDescriptions();
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String numOfRubricChoicesString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS);
        String numOfRubricSubQuestionsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS);
        
        if (numOfRubricChoicesString == null || numOfRubricSubQuestionsString == null) {
            return false;
        }
        
        int numOfRubricChoices = Integer.parseInt(numOfRubricChoicesString);
        int numOfRubricSubQuestions = Integer.parseInt(numOfRubricSubQuestionsString);
        List<String> rubricChoices = new ArrayList<String>();
        List<String> rubricSubQuestions = new ArrayList<String>();
        List<List<String>> rubricDescriptions = new ArrayList<List<String>>();
        
        int numActualChoices = 0;
        int numActualSubQuestions = 0;
        
        // Get list of choices
        for(int i = 0 ; i<numOfRubricChoices ; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + i);
            if(choice != null) {
                rubricChoices.add(choice);
                numActualChoices++;
            }
        }
        
        // Get list of sub-questions
        for(int i = 0 ; i<numOfRubricSubQuestions ; i++) {
            String subQuestion = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-" + i);
            if(subQuestion != null) {
                rubricSubQuestions.add(subQuestion);
                numActualSubQuestions++;
            }
        }
        
        // Get descriptions
        int descRows = -1;
        for(int i = 0 ; i<numOfRubricSubQuestions ; i++) {
            boolean rowAdded = false;
            for(int j = 0 ; j<numOfRubricChoices ; j++) {
                String description = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-" + i + "-" + j);
                if(description != null) {
                    if (rowAdded == false) {
                        descRows++;
                        rubricDescriptions.add(new ArrayList<String>());
                        rowAdded = true;
                    }
                    rubricDescriptions.get(descRows).add(description);
                }
            }
        }
        
        // Set details
        setRubricQuestionDetails(numActualChoices, rubricChoices,
                numActualSubQuestions, rubricSubQuestions, rubricDescriptions);
        
        if (!this.isValidDescriptionSize()) {
            // If description sizes are invalid, default to empty descriptions.
            this.initializeRubricDescriptions();
        }
        
        return true;
    }
    
    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
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
        
        // Responses require deletion if choices change
        if (this.numOfRubricChoices != newRubricDetails.numOfRubricChoices ||
            this.rubricChoices.containsAll(newRubricDetails.rubricChoices) == false ||
            newRubricDetails.rubricChoices.containsAll(this.rubricChoices) == false) {
            return true;
        }
        
        // Responses require deletion if sub-questions change
        if (this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions ||
            this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions) == false ||
            newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions) == false) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) existingResponseDetails;

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml = getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml = getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, true, frd);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString, sessionIsOpen, true, frd);

        // Create submission form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM,
                "${qnIndex}", questionNumberString,
                "${respIndex}", responseNumberString,
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml,
                "${tableBodyHtml}", tableBodyHtml,
                "${mobileHtml}", mobileHtml,
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml = getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml = getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, false, null);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString, sessionIsOpen, false, null);

        // Create submission form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM,
                "${qnIndex}", questionNumberString,
                "${respIndex}", responseNumberString,
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml,
                "${tableBodyHtml}", tableBodyHtml,
                "${mobileHtml}", mobileHtml,
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
        return html;
    }

    private String getSubmissionFormTableHeaderFragmentHtml(String questionNumberString, String responseNumberString) {
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT;

        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${col}", Integer.toString(i),
                            "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(i))
                                                    + " (" + (numOfRubricChoices - i) + ")");
            tableHeaderFragmentHtml.append(tableHeaderCell + Const.EOL);
        }
        return tableHeaderFragmentHtml.toString();
    }

    private String getSubmissionFormTableBodyHtml(String questionNumberString, String responseNumberString,
            boolean sessionIsOpen, boolean isExistingResponse, FeedbackRubricResponseDetails frd) {
        StringBuilder tableBodyHtml = new StringBuilder();
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_BODY;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String tableBodyCell = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${respIndex}", responseNumberString,
                                "${col}", Integer.toString(j),
                                "${row}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(i, j)),
                                "${checked}", (isExistingResponse && frd.getAnswer(i) == j)? "checked":"", //Check if existing choice for sub-question == current choice
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                tableBodyFragmentHtml.append(tableBodyCell + Const.EOL);
            }
            // Get entire row
            String tableRow = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${respIndex}", responseNumberString,
                            "${row}", Integer.toString(i),
                            "${subQuestion}", StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") " + Sanitizer.sanitizeForHtml(rubricSubQuestions.get(i)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow + Const.EOL);
        }
        return tableBodyHtml.toString();
    }

    private String getSubmissionFormMobileHtml(String questionNumberString, String responseNumberString,
            boolean sessionIsOpen, boolean isExistingResponse, FeedbackRubricResponseDetails frd) {
        StringBuilder mobileHtml = new StringBuilder();
        String mobilePanelTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL;
        String mobilePanelFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL_FRAGMENT;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder panelBody = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String panelBodyFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(mobilePanelFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${respIndex}", responseNumberString,
                                "${col}", Integer.toString(j),
                                "${row}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(i, j)),
                                "${checked}", (isExistingResponse && frd.getAnswer(i) == j)? "checked":"", //Check if existing choice for sub-question == current choice
                                "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(j)),
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                panelBody.append(panelBodyFragment);
            }
            String panel = 
                FeedbackQuestionFormTemplates.populateTemplate(mobilePanelTemplate,
                    "${panelBody}", panelBody.toString(),
                    "${subQuestion}", StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") " + Sanitizer.sanitizeForHtml(rubricSubQuestions.get(i)));
            mobileHtml.append(panel + Const.EOL);
        }
        return mobileHtml.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        String questionNumberString = Integer.toString(questionNumber);
        
        
        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_HEADER_FRAGMENT;
        for(int i = 0 ; i < numOfRubricChoices ; i++) {
            String tableHeaderCell = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${qnIndex}", questionNumberString,
                            "${col}", Integer.toString(i),
                            "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
            tableHeaderFragmentHtml.append(tableHeaderCell + Const.EOL);
        }
        
        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();
        
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM_BODY;
        
        for(int j = 0 ; j < numOfRubricSubQuestions ; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for(int i = 0 ; i < numOfRubricChoices ; i++) {
                String tableBodyCell = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${qnIndex}", questionNumberString,
                                "${col}", Integer.toString(i),
                                "${row}", Integer.toString(j),
                                "${description}", Sanitizer.sanitizeForHtml(this.getDescription(j, i)),
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION);
                tableBodyFragmentHtml.append(tableBodyCell + Const.EOL);
            }
            
            // Get entire row
            String tableRow = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${qnIndex}", questionNumberString,
                            "${row}", Integer.toString(j),
                            "${subQuestion}", Sanitizer.sanitizeForHtml(rubricSubQuestions.get(j)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString(),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION);
            tableBodyHtml.append(tableRow + Const.EOL);
        }
        
        // Create edit form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_EDIT_FORM,
                "${qnIndex}", questionNumberString,
                "${currRows}", Integer.toString(this.numOfRubricSubQuestions),
                "${currCols}", Integer.toString(this.numOfRubricChoices),
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml.toString(),
                "${tableBodyHtml}", tableBodyHtml.toString(),
                "${Const.ParamNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS,
                "${Const.ParamNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS}", Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS);
        
        return html;
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add some choices by default
        this.numOfRubricChoices = 4;
        this.rubricChoices.add("Strongly Agree");
        this.rubricChoices.add("Agree");
        this.rubricChoices.add("Disagree");
        this.rubricChoices.add("Strongly Disagree");
        
        // Add some sub-questions by default
        this.numOfRubricSubQuestions = 2;
        this.rubricSubQuestions.add("This student participates well in online discussions.");
        this.rubricSubQuestions.add("This student completes assigned tasks on time.");
        
        this.initializeRubricDescriptions();
        
        setDescription(0,0, "Initiates discussions frequently, and engages the team.");
        setDescription(0,1, "Takes part in discussions and sometimes initiates discussions.");
        setDescription(0,2, "Occasionally responds, but never initiates discussions.");
        setDescription(0,3, "Rarely or never responds.");
        
        setDescription(1,0, "Tasks are always completed before the deadline.");
        setDescription(1,1, "Occasionally misses deadlines.");
        setDescription(1,2, "Often misses deadlines.");
        setDescription(1,3, "Rarely or never completes tasks.");
        
        return "<div id=\"rubricForm\">" + 
                    this.getQuestionSpecificEditFormHtml(-1) +
               "</div>";
    }
    
    private void initializeRubricDescriptions() {
        this.rubricDescriptions = new ArrayList<List<String>>();
        for (int subQns=0 ; subQns<this.numOfRubricSubQuestions ; subQns++) {
            List<String> descList = new ArrayList<String>();
            for (int ch=0 ; ch<this.numOfRubricChoices ; ch++) {
                descList.add("");
            }
            rubricDescriptions.add(descList);
        }
    }
    
    public void setDescription(int row, int col, String description) {
        this.rubricDescriptions.get(row).set(col, description);
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
                        StringHelper.integerToLowerCaseAlphabeticalIndex(i+1) + ") "+ Sanitizer.sanitizeForHtml(rubricSubQuestions.get(i));
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
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {

        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        int[][] responseFrequency = calculateResponseFrequency(responses, fqd);
        float[][] rubricStats = calculateRubricStats(responses, question);
        
        
        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_RESULT_STATS_HEADER_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableHeaderFragmentTemplate,
                            "${rubricChoiceValue}", Sanitizer.sanitizeForHtml(rubricChoices.get(i)) 
                                                    + " (" + (numOfRubricChoices - i) + ")");
            tableHeaderFragmentHtml.append(tableHeaderCell + Const.EOL);
        }
        
        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();
        
        String tableBodyFragmentTemplate = FeedbackQuestionFormTemplates.RUBRIC_RESULT_STATS_BODY_FRAGMENT;
        String tableBodyTemplate = FeedbackQuestionFormTemplates.RUBRIC_RESULT_STATS_BODY;
        DecimalFormat df = new DecimalFormat("#"); 
        DecimalFormat dfAverage = new DecimalFormat("###.##");
        
        for (int j = 0; j < numOfRubricSubQuestions; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int i = 0; i < numOfRubricChoices; i++) {
                String tableBodyCell = 
                        FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate,
                                "${percentageFrequencyOrAverage}", df.format(rubricStats[j][i]*100) + "%" 
                                                                   + " (" + responseFrequency[j][i] +")");
                tableBodyFragmentHtml.append(tableBodyCell + Const.EOL);
            }

            String tableAverageCell = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyFragmentTemplate, 
                             "${percentageFrequencyOrAverage}", dfAverage.format(rubricStats[j][numOfRubricChoices]));
            tableBodyFragmentHtml.append(tableAverageCell + Const.EOL);
            
            // Get entire row
            String tableRow = 
                    FeedbackQuestionFormTemplates.populateTemplate(tableBodyTemplate,
                            "${subQuestion}", StringHelper.integerToLowerCaseAlphabeticalIndex(j+1) + ") "+ Sanitizer.sanitizeForHtml(rubricSubQuestions.get(j)),
                            "${rubricRowBodyFragments}",  tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow + Const.EOL);
        }
        
        // Create edit form
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RUBRIC_RESULT_STATS,
                "${statsTitle}", (view=="student")?"Response Summary (of visible responses)":"Response Summary",
                "${tableHeaderRowFragmentHtml}", tableHeaderFragmentHtml.toString(),
                "${tableBodyHtml}", tableBodyHtml.toString());
        
        return html;
    }
    
    /**
     * Calculates the statistics for rubric question
     * 
     * Returns a 2D float array to indicate the percentage frequency
     * a choice is selected for each sub-question.
     * 
     * e.g.
     * pecentageFrequency[subQuestionIndex][choiceIndex]
     *  -> is the percentage choiceIndex is chosen for subQuestionIndex, for the given question/responses.
     *
     */
    private float[][] calculateRubricStats(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question) {
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        
        // Initialize response frequency variable, used to store frequency each choice is selected.
        int[][] responseFrequency = calculateResponseFrequency(responses, fqd);
        
        // Initialize percentage frequencies and average value
        float[][] percentageFrequencyOrAverage = new float[fqd.numOfRubricSubQuestions][];
        for (int i = 0; i < percentageFrequencyOrAverage.length; i++) {
            //+ 1 is the position for average value
            percentageFrequencyOrAverage[i] = new float[fqd.numOfRubricChoices + 1];
            for (int j = 0; j < percentageFrequencyOrAverage[i].length - 1; j++) {
                // Initialize to be number of responses
                percentageFrequencyOrAverage[i][j] = responseFrequency[i][j];
            }
            percentageFrequencyOrAverage[i][fqd.numOfRubricChoices] = 0;
        }
        
        // Calculate percentage frequencies
        for (int i = 0; i < percentageFrequencyOrAverage.length; i++) {
            // Count total number of responses for each sub-question
            int totalForSubQuestion = 0;
            for (int j = 0; j < percentageFrequencyOrAverage[i].length - 1; j++) {
                totalForSubQuestion += responseFrequency[i][j];
            }
            
            // Divide by totalForSubQuestion to get percentage and calculate the average value
            for (int j = 0; j < percentageFrequencyOrAverage[i].length - 1; j++) {
                percentageFrequencyOrAverage[i][j] /= totalForSubQuestion;
                float choiceWeight = (fqd.numOfRubricChoices - j) * percentageFrequencyOrAverage[i][j];
                percentageFrequencyOrAverage[i][fqd.numOfRubricChoices] += choiceWeight;
            }
        }
        
        return percentageFrequencyOrAverage;
    }
    
    private int[][] calculateResponseFrequency(
            List<FeedbackResponseAttributes> responses,
            FeedbackRubricQuestionDetails fqd) {
        int[][] responseFrequency = new int[fqd.numOfRubricSubQuestions][];
        for (int i=0 ; i<responseFrequency.length ; i++) {
            responseFrequency[i] = new int[fqd.numOfRubricChoices];
            for (int j=0 ; j<responseFrequency[i].length ; j++) {
                responseFrequency[i][j] = 0;
            }
        }
        
        // Count frequencies
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) response.getResponseDetails();
            for (int i=0 ; i<fqd.numOfRubricSubQuestions ; i++) {
                int chosenChoice = frd.getAnswer(i);
                if (chosenChoice != -1) {
                    responseFrequency[i][chosenChoice]+=1;
                }
            }
        }
        return responseFrequency;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if(responses.isEmpty()){
            return "";
        }

        StringBuilder csv = new StringBuilder();

        // table header
        for (String choice : rubricChoices) {
            csv.append("," + Sanitizer.sanitizeForCsv(choice));
        }
        csv.append("," + "Average");
        csv.append(Const.EOL);

        // table body
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfAverage = new DecimalFormat("###.##");

        int[][] responseFrequency = calculateResponseFrequency(responses, this);
        float[][] rubricStats = calculateRubricStats(responses, question);
        
        for (int i = 0; i < rubricSubQuestions.size(); i++) {
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);
            csv.append(Sanitizer.sanitizeForCsv(alphabeticalIndex + ") " + rubricSubQuestions.get(i)));
            for (int j = 0; j < rubricChoices.size(); j++) {
                String percentageFrequency = df.format(rubricStats[i][j] * 100) + "%";
                csv.append("," + percentageFrequency + " (" + responseFrequency[i][j] + ")");
            }
            csv.append("," + dfAverage.format(rubricStats[i][rubricChoices.size()]));
            csv.append(Const.EOL);
        }

        return csv.toString();
    }
    
    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
       return Sanitizer.sanitizeForCsv("All Sub-Questions") + ","
            + Sanitizer.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
    }
    
    @Override
    public String getCsvHeader() {
        return "Choice Value";
    }
    
    public String getCsvDetailedResponsesHeader() {
        return    "Team" + "," + "Giver's Full Name" + "," 
                + "Giver's Last Name" + "," +"Giver's Email" + ","  
                + "Recipient's Team" + "," + "Recipient's Full Name" + "," 
                + "Recipient's Last Name" + "," + "Recipient's Email" + ","  
                + "Sub Question" + "," + this.getCsvHeader() + ","
                + "Choice Number" + Const.EOL;
    }
    
    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
            FeedbackResponseAttributes feedbackResponseAttributes,
            FeedbackQuestionAttributes question) {
        
        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverTeamName =fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);
        
        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientTeamName =fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);
        
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) feedbackResponseAttributes.getResponseDetails();
        String detailedResponsesRow = "";
        for (int i=0 ; i<frd.answer.size() ; i++) {
            int chosenIndex = frd.answer.get(i);
            String chosenChoiceNumber = "", chosenChoiceValue = "";
            String chosenIndexString = StringHelper.integerToLowerCaseAlphabeticalIndex(i+1);
            
            if (chosenIndex == -1) {
                chosenChoiceValue = Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
            } else {
                chosenChoiceNumber = Integer.toString(chosenIndex+1);
                chosenChoiceValue = this.rubricChoices.get(frd.answer.get(i));
            }
            
            detailedResponsesRow += Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName)) 
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName)) 
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName))
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail))
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName))
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName))
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName))
                                    + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail))
                                    + "," + Sanitizer.sanitizeForCsv(chosenIndexString)
                                    + "," + Sanitizer.sanitizeForCsv(chosenChoiceValue)
                                    + "," + Sanitizer.sanitizeForCsv(chosenChoiceNumber)
                                    + Const.EOL;
        }
        
        return detailedResponsesRow;
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<option value = \"RUBRIC\">"+Const.FeedbackQuestionTypeNames.RUBRIC+"</option>";
    }

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
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_DESC_INVALID_SIZE);
        }
        
        if (this.numOfRubricChoices < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_CHOICES + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES);
        }
        
        if (this.numOfRubricSubQuestions < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
        }
        
        //Rubric choices are now allowed to be empty.
        /*
        for (String choice : this.rubricChoices) {
            if (choice.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_CHOICE);
                break;
            }
        }
        */
        
        for (String subQn : this.rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }
        
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

package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {

    private static final Logger log = Logger.getLogger();
    private static final String STATISTICS_NO_VALUE_STRING = "-";

    private boolean hasAssignedWeights;
    private List<Double> rubricWeights;
    private int numOfRubricChoices;
    private List<String> rubricChoices;
    private int numOfRubricSubQuestions;
    private List<String> rubricSubQuestions;
    private List<List<String>> rubricDescriptions;

    public FeedbackRubricQuestionDetails() {
        super(FeedbackQuestionType.RUBRIC);

        this.hasAssignedWeights = false;
        this.rubricWeights = new ArrayList<>();
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<>();
        this.initializeRubricDescriptions();
    }

    public FeedbackRubricQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RUBRIC, questionText);

        this.hasAssignedWeights = false;
        this.rubricWeights = new ArrayList<>();
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<>();
        this.initializeRubricDescriptions();
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String numOfRubricChoicesString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS);
        String numOfRubricSubQuestionsString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS);

        if (numOfRubricChoicesString == null || numOfRubricSubQuestionsString == null) {
            return false;
        }

        String hasAssignedWeightsString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED);

        boolean hasAssignedWeights = "on".equals(hasAssignedWeightsString);
        int numOfRubricChoices = Integer.parseInt(numOfRubricChoicesString);
        int numOfRubricSubQuestions = Integer.parseInt(numOfRubricSubQuestionsString);
        List<Double> rubricWeights = getRubricWeights(requestParameters, numOfRubricChoices, hasAssignedWeights);
        List<String> rubricChoices = getRubricChoices(requestParameters, numOfRubricChoices);
        List<String> rubricSubQuestions = getSubQuestions(requestParameters, numOfRubricSubQuestions);
        List<List<String>> rubricDescriptions = getRubricQuestionDescriptions(requestParameters,
                                                                              numOfRubricChoices,
                                                                              numOfRubricSubQuestions);

        // Set details
        setRubricQuestionDetails(hasAssignedWeights, rubricWeights, rubricChoices, rubricSubQuestions, rubricDescriptions);

        if (!isValidDescriptionSize()) {
            // If description sizes are invalid, default to empty descriptions.
            initializeRubricDescriptions();
        }

        return true;
    }

    private List<Double> getRubricWeights(Map<String, String[]> requestParameters, int numOfRubricChoices,
                                         boolean hasAssignedWeights) {
        List<Double> rubricWeights = new ArrayList<>();

        if (!hasAssignedWeights) {
            return rubricWeights;
        }

        for (int i = 0; i < numOfRubricChoices; i++) {

            String weight = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-" + i);
            String choice = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + i);

            if (choice == null || weight == null) {
                continue;
            }

            try {
                rubricWeights.add(Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                // Do not add weight to rubricWeights if the weight cannot be parsed
                log.warning("Failed to parse weight for rubric question: " + weight);
            }
        }

        return rubricWeights;
    }

    private List<String> getRubricChoices(Map<String, String[]> requestParameters, int numOfRubricChoices) {
        List<String> rubricChoices = new ArrayList<>();
        for (int i = 0; i < numOfRubricChoices; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                  Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + i);
            if (choice != null) {
                rubricChoices.add(choice);
            }
        }
        return rubricChoices;
    }

    private List<String> getSubQuestions(Map<String, String[]> requestParameters, int numOfRubricSubQuestions) {
        List<String> rubricSubQuestions = new ArrayList<>();
        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            String subQuestion = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-" + i);
            if (subQuestion != null) {
                rubricSubQuestions.add(subQuestion);
            }
        }
        return rubricSubQuestions;
    }

    private List<List<String>> getRubricQuestionDescriptions(Map<String, String[]> requestParameters,
                                                             int numOfRubricChoices, int numOfRubricSubQuestions) {
        List<List<String>> rubricDescriptions = new ArrayList<>();
        int descRows = -1;
        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            boolean rowAdded = false;
            for (int j = 0; j < numOfRubricChoices; j++) {
                String paramName = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-" + i + "-" + j;
                String description = HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
                if (description != null) {
                    if (!rowAdded) {
                        descRows++;
                        rubricDescriptions.add(new ArrayList<String>());
                        rowAdded = true;
                    }
                    rubricDescriptions.get(descRows).add(description);
                }
            }
        }
        return rubricDescriptions;
    }

    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidDescriptionSize() {
        if (rubricDescriptions.size() != numOfRubricSubQuestions) {
            return false;
        }
        for (int i = 0; i < rubricDescriptions.size(); i++) {
            if (rubricDescriptions.get(i).size() != numOfRubricChoices) {
                return false;
            }
        }
        return true;
    }

    private void setRubricQuestionDetails(boolean hasAssignedWeights,
                                          List<Double> rubricWeights,
                                          List<String> rubricChoices,
                                          List<String> rubricSubQuestions,
                                          List<List<String>> rubricDescriptions) {
        this.hasAssignedWeights = hasAssignedWeights;
        this.rubricWeights = rubricWeights;
        this.numOfRubricChoices = rubricChoices.size();
        this.rubricChoices = rubricChoices;
        this.numOfRubricSubQuestions = rubricSubQuestions.size();
        this.rubricSubQuestions = rubricSubQuestions;
        this.rubricDescriptions = rubricDescriptions;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RUBRIC;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRubricQuestionDetails newRubricDetails = (FeedbackRubricQuestionDetails) newDetails;
        // TODO: need to check for exact match.

        // Responses require deletion if choices change
        if (!this.rubricChoices.equals(newRubricDetails.rubricChoices)) {
            return true;
        }

        // Responses require deletion if sub-questions change
        return this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions
            || !this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions)
            || !newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions);
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails,
            StudentAttributes student) {
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) existingResponseDetails;

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml =
                getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml =
                getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, true, frd);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString,
                                                        sessionIsOpen, true, frd);

        // Create submission form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.RESPONSE_INDEX, responseNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml,
                Slots.TABLE_BODY_HTML, tableBodyHtml,
                Slots.MOBILE_HTML, mobileHtml,
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student) {

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml =
                getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml =
                getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, false, null);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString,
                                                        sessionIsOpen, false, null);

        // Create submission form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.RESPONSE_INDEX, responseNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml,
                Slots.TABLE_BODY_HTML, tableBodyHtml,
                Slots.MOBILE_HTML, mobileHtml,
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
    }

    private String getSubmissionFormTableHeaderFragmentHtml(String questionNumberString, String responseNumberString) {
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT;

        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.RESPONSE_INDEX, responseNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(i)));
            // TODO display numerical value of option
            tableHeaderFragmentHtml.append(tableHeaderCell).append(System.lineSeparator());
        }
        return tableHeaderFragmentHtml.toString();
    }

    private String getSubmissionFormTableBodyHtml(String questionNumberString, String responseNumberString,
                                                  boolean sessionIsOpen, boolean isExistingResponse,
                                                  FeedbackRubricResponseDetails frd) {
        StringBuilder tableBodyHtml = new StringBuilder();
        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_BODY;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String tableBodyCell =
                        Templates.populateTemplate(tableBodyFragmentTemplate,
                                Slots.QUESTION_INDEX, questionNumberString,
                                Slots.RESPONSE_INDEX, responseNumberString,
                                Slots.COL, Integer.toString(j),
                                Slots.ROW, Integer.toString(i),
                                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(i, j)),
                                // Check if existing choice for sub-question == current choice
                                Slots.CHECKED, isExistingResponse && frd.getAnswer(i) == j ? "checked" : "",
                                Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                tableBodyFragmentHtml.append(tableBodyCell).append(System.lineSeparator());
            }
            // Get entire row
            String tableRow =
                    Templates.populateTemplate(tableBodyTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.RESPONSE_INDEX, responseNumberString,
                            Slots.ROW, Integer.toString(i),
                            Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                                              + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)),
                            Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow).append(System.lineSeparator());
        }
        return tableBodyHtml.toString();
    }

    private String getSubmissionFormMobileHtml(String questionNumberString, String responseNumberString,
            boolean sessionIsOpen, boolean isExistingResponse, FeedbackRubricResponseDetails frd) {
        StringBuilder mobileHtml = new StringBuilder();
        String mobilePanelTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL;
        String mobilePanelFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL_FRAGMENT;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder panelBody = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String panelBodyFragment = Templates.populateTemplate(mobilePanelFragmentTemplate,
                        Slots.QUESTION_INDEX, questionNumberString,
                        Slots.RESPONSE_INDEX, responseNumberString,
                        Slots.COL, Integer.toString(j),
                        Slots.ROW, Integer.toString(i),
                        Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                        Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(i, j)),
                        // Check if existing choice for sub-question == current choice
                        Slots.CHECKED, isExistingResponse && frd.getAnswer(i) == j ? "checked" : "",
                        Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(j)),
                        Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                panelBody.append(panelBodyFragment);
            }
            String panel = Templates.populateTemplate(mobilePanelTemplate,
                    Slots.PANEL_BODY, panelBody.toString(),
                    Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                            + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)));
            mobileHtml.append(panel).append(System.lineSeparator());
        }
        return mobileHtml.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        String questionNumberString = Integer.toString(questionNumber);
        DecimalFormat weightFormat = new DecimalFormat("#.##");

        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_HEADER_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(i)),
                            Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
            tableHeaderFragmentHtml.append(tableHeaderCell).append(System.lineSeparator());
        }

        // Create rubric weights row
        StringBuilder tableWeightFragmentHtml = new StringBuilder();
        String tableWeightFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_WEIGHT_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableWeightCell =
                    Templates.populateTemplate(tableWeightFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_WEIGHT, hasAssignedWeights ? weightFormat.format(rubricWeights.get(i)) : "0",
                            Slots.RUBRIC_PARAM_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT);
            tableWeightFragmentHtml.append(tableWeightCell).append(System.lineSeparator());
        }

        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();

        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_EDIT_FORM_BODY;

        for (int j = 0; j < numOfRubricSubQuestions; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int i = 0; i < numOfRubricChoices; i++) {
                String tableBodyCell =
                        Templates.populateTemplate(tableBodyFragmentTemplate,
                                Slots.QUESTION_INDEX, questionNumberString,
                                Slots.COL, Integer.toString(i),
                                Slots.ROW, Integer.toString(j),
                                Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(j, i)),
                                Slots.RUBRIC_PARAM_DESCRIPTION, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION);
                tableBodyFragmentHtml.append(tableBodyCell).append(System.lineSeparator());
            }

            // Get entire row
            String tableRow =
                    Templates.populateTemplate(tableBodyTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.ROW, Integer.toString(j),
                            Slots.SUB_QUESTION, SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(j)),
                            Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString(),
                            Slots.RUBRIC_PARAM_SUB_QUESTION, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION);
            tableBodyHtml.append(tableRow).append(System.lineSeparator());
        }

        // Create rubric column options as the last row of the table
        StringBuilder rubricColumnOptionsFragments = new StringBuilder();
        String tableOptionsTemplate = FormTemplates.RUBRIC_EDIT_FORM_TABLE_OPTIONS;
        String tableOptionsFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_TABLE_OPTIONS_FRAGMENT;

        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableBodyCell = Templates.populateTemplate(tableOptionsFragmentTemplate,
                    Slots.QUESTION_INDEX, questionNumberString,
                    Slots.COL, Integer.toString(i));
            rubricColumnOptionsFragments.append(tableBodyCell).append(System.lineSeparator());
        }

        String tableOptions = Templates.populateTemplate(tableOptionsTemplate,
                Slots.RUBRIC_TABLE_OPTIONS_FRAGMENT, rubricColumnOptionsFragments.toString());

        StringBuilder tableOptionsHtml = new StringBuilder().append(tableOptions).append(System.lineSeparator());

        // Create edit form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_EDIT_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml.toString(),
                Slots.RUBRIC_TABLE_WEIGHT_ROW_FRAGMENT_HTML, tableWeightFragmentHtml.toString(),
                Slots.TABLE_BODY_HTML, tableBodyHtml.toString(),
                Slots.RUBRIC_PARAM_NUM_ROWS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS,
                Slots.RUBRIC_PARAM_NUM_COLS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS,
                Slots.CHECK_ASSIGN_WEIGHTS, hasAssignedWeights ? "checked" : "",
                Slots.RUBRIC_TOOLTIPS_ASSIGN_WEIGHTS, Const.Tooltips.FEEDBACK_QUESTION_RUBRIC_ASSIGN_WEIGHTS,
                Slots.RUBRIC_PARAM_ASSIGN_WEIGHTS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED,
                Slots.RUBRIC_TABLE_OPTIONS, tableOptionsHtml.toString());
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add some choices by default
        numOfRubricChoices = 4;
        rubricChoices.add("Strongly Disagree");
        rubricChoices.add("Disagree");
        rubricChoices.add("Agree");
        rubricChoices.add("Strongly Agree");

        hasAssignedWeights = false;

        // Add some sub-questions by default
        numOfRubricSubQuestions = 2;
        rubricSubQuestions.add("This student participates well in online discussions.");
        rubricSubQuestions.add("This student completes assigned tasks on time.");

        initializeRubricDescriptions();

        setDescription(0, 0, "Rarely or never responds.");
        setDescription(0, 1, "Occasionally responds, but never initiates discussions.");
        setDescription(0, 2, "Takes part in discussions and sometimes initiates discussions.");
        setDescription(0, 3, "Initiates discussions frequently, and engages the team.");

        setDescription(1, 0, "Rarely or never completes tasks.");
        setDescription(1, 1, "Often misses deadlines.");
        setDescription(1, 2, "Occasionally misses deadlines.");
        setDescription(1, 3, "Tasks are always completed before the deadline.");

        return "<div id=\"rubricForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    private void initializeRubricDescriptions() {
        rubricDescriptions = new ArrayList<>();
        for (int subQns = 0; subQns < numOfRubricSubQuestions; subQns++) {
            List<String> descList = new ArrayList<>();
            for (int ch = 0; ch < numOfRubricChoices; ch++) {
                descList.add("");
            }
            rubricDescriptions.add(descList);
        }
    }

    private void setDescription(int row, int col, String description) {
        this.rubricDescriptions.get(row).set(col, description);
    }

    /**
     * Gets the description for given sub-question and choice.
     */
    private String getDescription(int subQuestion, int choice) {
        return rubricDescriptions.get(subQuestion).get(choice);
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder subQuestionListHtml = new StringBuilder();

        if (numOfRubricSubQuestions > 0) {
            subQuestionListHtml.append("<p>");
            for (int i = 0; i < numOfRubricSubQuestions; i++) {
                String subQuestionFragment =
                        StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1)
                        + ") " + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i));
                subQuestionListHtml.append(subQuestionFragment);
                subQuestionListHtml.append("<br>");
            }
            subQuestionListHtml.append("</p>");
        }

        String additionalInfo = Templates.populateTemplate(
                FormTemplates.RUBRIC_ADDITIONAL_INFO,
                Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
                Slots.RUBRIC_ADDITIONAL_INFO_FRAGMENTS, subQuestionListHtml.toString());

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    private String getRecipientStatsHeaderFragmentHtml(String header) {
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_RESULT_RECIPIENT_STATS_HEADER_FRAGMENT,
                Slots.STATS_TITLE, header);
    }

    public String getRecipientStatsHeaderHtml() {
        StringBuilder headerBuilder = new StringBuilder(100);
        DecimalFormat dfWeight = new DecimalFormat("#.##");
        StringBuilder choicesHtmlBuilder = new StringBuilder(100);

        for (int i = 0; i < rubricChoices.size(); i++) {
            String weight = dfWeight.format(rubricWeights.get(i));
            String html = getRecipientStatsHeaderFragmentHtml(rubricChoices.get(i) + " (Weight: " + weight + ")");
            choicesHtmlBuilder.append(html);
        }

        headerBuilder.append(getRecipientStatsHeaderFragmentHtml("Team"))
                     .append(getRecipientStatsHeaderFragmentHtml("Recipient Name"))
                     .append(getRecipientStatsHeaderFragmentHtml("Sub Question"))
                     .append(choicesHtmlBuilder.toString())
                     .append(getRecipientStatsHeaderFragmentHtml("Total"))
                     .append(getRecipientStatsHeaderFragmentHtml("Average"));

        return headerBuilder.toString();
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
                                                  FeedbackQuestionAttributes question, String studentEmail,
                                                  FeedbackSessionResultsBundle bundle, String view) {

        List<FeedbackResponseAttributes> responsesForStatistics =
                filterResponsesForStatistics(responses, question, studentEmail, bundle, view);

        FeedbackRubricQuestionDetails fqd =
                (FeedbackRubricQuestionDetails) question.getQuestionDetails();

        FeedbackParticipantType recipientType = question.getRecipientType();
        boolean isExcludingSelfOptionAvailable =
                recipientType.equals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);

        DecimalFormat weightFormat = new DecimalFormat("#.##");

        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_RESULT_STATS_HEADER_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {

            String header = SanitizationHelper.sanitizeForHtml(rubricChoices.get(i))
                          + (fqd.hasAssignedWeights
                            ? "<span style=\"font-weight:normal;\"> (Weight: "
                              + weightFormat.format(rubricWeights.get(i)) + ")</span>"
                            : "");

            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate, Slots.RUBRIC_CHOICE_VALUE, header);
            tableHeaderFragmentHtml.append(tableHeaderCell).append(System.lineSeparator());
        }

        if (fqd.hasAssignedWeights) {
            String tableHeaderAverageCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.RUBRIC_CHOICE_VALUE, "Average");
            tableHeaderFragmentHtml.append(tableHeaderAverageCell).append(System.lineSeparator());
        }

        int[][] responseFrequency = RubricStatistics.calculateResponseFrequency(responsesForStatistics, fqd);
        float[][] rubricStats = RubricStatistics.calculatePercentageFrequencyAndAverage(fqd, responseFrequency);

        StringBuilder tableBodyHtml = getQuestionResultsStatisticsBodyHtml(fqd, responseFrequency, rubricStats);

        StringBuilder tableBodyExcludingSelfHtml;

        if (isExcludingSelfOptionAvailable) {

            int[][] responseFrequencyExcludingSelf =
                    RubricStatistics.calculateResponseFrequencyExcludingSelf(responsesForStatistics, fqd);
            float[][] rubricStatsExcludingSelf = RubricStatistics.calculatePercentageFrequencyAndAverage(fqd,
                    responseFrequencyExcludingSelf);
            tableBodyExcludingSelfHtml = getQuestionResultsStatisticsBodyHtml(fqd,
                    responseFrequencyExcludingSelf, rubricStatsExcludingSelf);
        } else {
            tableBodyExcludingSelfHtml = new StringBuilder();
            tableBodyExcludingSelfHtml.append(System.lineSeparator());
        }

        String statsTitle = "Response Summary";

        if ("student".equals(view)) {
            if (responses.size() == responsesForStatistics.size()) {
                statsTitle = "Response Summary (of visible responses)";
            } else {
                statsTitle = "Response Summary (of received responses)";
            }
        }

        String recipientStatsHtml = "";

        if (hasAssignedWeights) {
            List<Map.Entry<String, RubricRecipientStatistics>> recipientStatsList =
                    getPerRecipientStatisticsSorted(responses, bundle);
            StringBuilder bodyBuilder = new StringBuilder(100);

            for (Map.Entry<String, RubricRecipientStatistics> entry : recipientStatsList) {
                RubricRecipientStatistics stats = entry.getValue();
                bodyBuilder.append(stats.getHtmlForAllSubQuestions());
            }

            recipientStatsHtml = Templates.populateTemplate(FormTemplates.RUBRIC_RESULT_RECIPIENT_STATS,
                    Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, getRecipientStatsHeaderHtml(),
                    Slots.TABLE_BODY_HTML, bodyBuilder.toString());
        }

        return Templates.populateTemplate(
                FormTemplates.RUBRIC_RESULT_STATS,
                Slots.STATS_TITLE, statsTitle,
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml.toString(),
                Slots.TABLE_BODY_HTML, tableBodyHtml.toString(),
                Slots.TABLE_BODY_EXCLUDING_SELF_HTML, tableBodyExcludingSelfHtml.toString(),
                Slots.EXCLUDING_SELF_OPTION_VISIBLE, isExcludingSelfOptionAvailable ? "" : "hidden",
                Slots.RUBRIC_RECIPIENT_STATS_HTML, recipientStatsHtml);
    }

    /**
     * Returns the rendered HTML body of Rubric statistics.
     *
     * @param fqd the details of Rubric Question
     * @param responseFrequency the frequency of the responses for each sub question
     * @param rubricStats the percentage frequency and average for each sub question
     */
    private StringBuilder getQuestionResultsStatisticsBodyHtml(FeedbackRubricQuestionDetails fqd,
                                                               int[][] responseFrequency, float[][] rubricStats) {

        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfAverage = new DecimalFormat("0.00");

        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_RESULT_STATS_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_RESULT_STATS_BODY;

        StringBuilder tableBodyHtml = new StringBuilder();

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            boolean isSubQuestionRespondedTo = responseFrequency[i][numOfRubricChoices] > 0;

            for (int j = 0; j < numOfRubricChoices; j++) {
                String percentageFrequencyString = isSubQuestionRespondedTo
                        ? df.format(rubricStats[i][j] * 100) + "%"
                        : STATISTICS_NO_VALUE_STRING;
                String tableBodyCell = Templates.populateTemplate(tableBodyFragmentTemplate,
                        Slots.RUBRIC_PERCENTAGE_FREQUENCY_OR_AVERAGE,
                        percentageFrequencyString + " (" + responseFrequency[i][j] + ")");
                tableBodyFragmentHtml.append(tableBodyCell).append(System.lineSeparator());

            }

            if (fqd.hasAssignedWeights) {
                String averageString = isSubQuestionRespondedTo
                        ? dfAverage.format(rubricStats[i][numOfRubricChoices])
                        : STATISTICS_NO_VALUE_STRING;
                String tableBodyAverageCell = Templates.populateTemplate(tableBodyFragmentTemplate,
                        Slots.RUBRIC_PERCENTAGE_FREQUENCY_OR_AVERAGE, averageString);
                tableBodyFragmentHtml.append(tableBodyAverageCell).append(System.lineSeparator());

            }

            // Get entire row
            String tableRow = Templates.populateTemplate(tableBodyTemplate,
                    Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                            + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)),
                    Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow).append(System.lineSeparator());
        }
        return tableBodyHtml;
    }

    /**
     * Returns a list of FeedbackResponseAttributes filtered according to view, question recipient type
     * for the Statistics Table.
     */
    private List<FeedbackResponseAttributes> filterResponsesForStatistics(
            List<FeedbackResponseAttributes> responses, FeedbackQuestionAttributes question,
            String studentEmail, FeedbackSessionResultsBundle bundle, String view) {

        boolean isViewedByStudent = "student".equals(view);
        if (!isViewedByStudent) {
            return responses;
        }

        FeedbackParticipantType recipientType = question.getRecipientType();

        boolean isFilteringSkipped = recipientType.equals(FeedbackParticipantType.INSTRUCTORS)
                || recipientType.equals(FeedbackParticipantType.NONE)
                || recipientType.equals(FeedbackParticipantType.SELF);

        if (isFilteringSkipped) {
            return responses;
        }

        boolean isFilteringByTeams = recipientType.equals(FeedbackParticipantType.OWN_TEAM)
                || recipientType.equals(FeedbackParticipantType.TEAMS);

        List<FeedbackResponseAttributes> receivedResponses = new ArrayList<>();
        String recipientString = isFilteringByTeams ? bundle.getTeamNameForEmail(studentEmail) : studentEmail;

        for (FeedbackResponseAttributes response : responses) {
            boolean isReceivedResponse = response.recipient.equals(recipientString);
            if (isReceivedResponse) {
                receivedResponses.add(response);
            }
        }

        return receivedResponses;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        StringBuilder csv = new StringBuilder();
        DecimalFormat dfWeight = new DecimalFormat("#.##");

        // table header
        for (int i = 0; i < rubricChoices.size(); i++) {

            String header = rubricChoices.get(i)
                          + (hasAssignedWeights
                            ? " (Weight: " + dfWeight.format(rubricWeights.get(i)) + ")"
                            : "");

            csv.append(',').append(SanitizationHelper.sanitizeForCsv(header));
        }

        if (hasAssignedWeights) {
            csv.append(",Average");
        }

        csv.append(System.lineSeparator());

        // table body
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfAverage = new DecimalFormat("0.00");

        int[][] responseFrequency = RubricStatistics.calculateResponseFrequency(responses, this);
        float[][] rubricStats = RubricStatistics.calculatePercentageFrequencyAndAverage(this,
                responseFrequency);

        for (int i = 0; i < rubricSubQuestions.size(); i++) {
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);
            csv.append(SanitizationHelper.sanitizeForCsv(alphabeticalIndex + ") " + rubricSubQuestions.get(i)));
            boolean isSubQuestionRespondedTo = responseFrequency[i][numOfRubricChoices] > 0;
            for (int j = 0; j < rubricChoices.size(); j++) {
                String percentageFrequencyString = isSubQuestionRespondedTo
                                                 ? df.format(rubricStats[i][j] * 100) + "%"
                                                 : STATISTICS_NO_VALUE_STRING;
                csv.append("," + percentageFrequencyString + " (" + responseFrequency[i][j] + ")");
            }

            if (hasAssignedWeights) {
                String averageString = isSubQuestionRespondedTo
                                     ? dfAverage.format(rubricStats[i][rubricWeights.size()])
                                     : STATISTICS_NO_VALUE_STRING;
                csv.append(',').append(averageString);
            }

            csv.append(System.lineSeparator());
        }

        if (hasAssignedWeights) {
            csv.append(System.lineSeparator());
            csv.append(getRecipientStatsCsvHeader());
            csv.append(getPerRecipientStatisticsCsv(responses, bundle));
        }

        return csv.toString();
    }

    public List<Map.Entry<String, RubricRecipientStatistics>> getPerRecipientStatisticsSorted(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        Map<String, RubricRecipientStatistics> recipientToRecipientStats = new HashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            recipientToRecipientStats.computeIfAbsent(response.recipient, recipient -> {
                String recipientTeam = bundle.getTeamNameForEmail(recipient);
                String recipientName = bundle.getNameForEmail(recipient);
                return new RubricRecipientStatistics(recipient, recipientName, recipientTeam);
            })
                .addResponseToRecipientStats(response);
        }

        List<Map.Entry<String, RubricRecipientStatistics>> recipientStatsList =
                new LinkedList<>(recipientToRecipientStats.entrySet());
        recipientStatsList.sort(Comparator.comparing((Map.Entry<String, RubricRecipientStatistics> obj) ->
                obj.getValue().recipientTeam.toLowerCase())
                .thenComparing(obj -> obj.getValue().recipientName));
        return recipientStatsList;
    }

    public String getPerRecipientStatisticsCsv(List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        StringBuilder csv = new StringBuilder(100);
        List<Map.Entry<String, RubricRecipientStatistics>> recipientStatsList =
                getPerRecipientStatisticsSorted(responses, bundle);

        for (Map.Entry<String, RubricRecipientStatistics> entry : recipientStatsList) {
            csv.append(entry.getValue().getCsvForAllSubQuestions());
        }

        return csv.toString();
    }

    @Override
    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
        return SanitizationHelper.sanitizeForCsv("All Sub-Questions") + ","
             + SanitizationHelper.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
    }

    @Override
    public String getCsvHeader() {
        return "Choice Value";
    }

    public String getRecipientStatsCsvHeader() {
        StringBuilder header = new StringBuilder(100);
        DecimalFormat dfWeight = new DecimalFormat("#.##");
        String headerFragment = "Team,Recipient Name,Recipient's Email,Sub Question,";

        header.append(headerFragment);

        for (int i = 0; i < numOfRubricChoices; i++) {
            StringBuilder rubricChoiceBuilder = new StringBuilder();

            rubricChoiceBuilder.append(rubricChoices.get(i));

            if (hasAssignedWeights) {
                rubricChoiceBuilder.append(" (Weight: ").append(dfWeight.format(rubricWeights.get(i))).append(')');
            }

            header.append(SanitizationHelper.sanitizeForCsv(rubricChoiceBuilder.toString())).append(',');
        }

        header.append("Total,Average").append(System.lineSeparator());

        return header.toString();
    }

    @Override
    public String getCsvDetailedResponsesHeader(int noOfComments) {
        return "Team" + "," + "Giver's Full Name" + ","
                + "Giver's Last Name" + "," + "Giver's Email" + ","
                + "Recipient's Team" + "," + "Recipient's Full Name" + ","
                + "Recipient's Last Name" + "," + "Recipient's Email" + ","
                + "Sub Question" + "," + getCsvHeader() + "," + "Choice Number"
                + getCsvDetailedFeedbackResponsesCommentsHeader(noOfComments)
                + System.lineSeparator();
    }

    @Override
    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
            FeedbackResponseAttributes feedbackResponseAttributes,
            FeedbackQuestionAttributes question, boolean hasCommentsForResponses) {

        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giver);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giver);
        String giverTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giver);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);

        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipient);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipient);
        String recipientTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipient);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);
        //To show comment only once for each response.
        boolean shouldShowComments = hasCommentsForResponses;
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) feedbackResponseAttributes.getResponseDetails();
        StringBuilder detailedResponsesRow = new StringBuilder(100);
        for (int i = 0; i < frd.answer.size(); i++) {
            //To show comment only once for each response.
            shouldShowComments = i < 1 && shouldShowComments;
            int chosenIndex = frd.answer.get(i);
            String chosenChoiceNumber = "";
            String chosenChoiceValue = "";
            String chosenIndexString = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);

            if (chosenIndex == -1) {
                chosenChoiceValue = Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
            } else {
                chosenChoiceNumber = Integer.toString(chosenIndex + 1);
                chosenChoiceValue = rubricChoices.get(frd.answer.get(i));
            }

            detailedResponsesRow.append(
                    SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenIndexString) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceValue) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceNumber)
                    + (shouldShowComments
                            ? fsrBundle.getCsvDetailedFeedbackResponseCommentsRow(feedbackResponseAttributes) : "")
                    + System.lineSeparator());
        }

        return detailedResponsesRow.toString();
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"RUBRIC\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.RUBRIC + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails(String courseId) {
        // For rubric questions,
        // 1) Description size should be valid
        // 2) At least 2 choices
        // 3) At least 1 sub-question
        // 4) Choices and sub-questions should not be empty
        // 5) Choices must have corresponding weights if weights are assigned

        List<String> errors = new ArrayList<>();

        if (!isValidDescriptionSize()) {
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_DESC_INVALID_SIZE);
        }

        if (numOfRubricChoices < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES);
        }

        if (this.numOfRubricSubQuestions < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
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

        for (String subQn : rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }

        if (hasAssignedWeights && rubricChoices.size() != rubricWeights.size()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);
        }

        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        return new ArrayList<>();
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    List<Double> getRubricWeights() {
        return new ArrayList<>(rubricWeights);
    }

    public int getNumOfRubricChoices() {
        return numOfRubricChoices;
    }

    public void setNumOfRubricChoices(int numOfRubricChoices) {
        this.numOfRubricChoices = numOfRubricChoices;
    }

    public List<String> getRubricChoices() {
        return rubricChoices;
    }

    public int getNumOfRubricSubQuestions() {
        return numOfRubricSubQuestions;
    }

    public void setNumOfRubricSubQuestions(int numOfRubricSubQuestions) {
        this.numOfRubricSubQuestions = numOfRubricSubQuestions;
    }

    public List<String> getRubricSubQuestions() {
        return rubricSubQuestions;
    }

    /**
     * Class to store any stats related to a recipient.
     */
    private class RubricRecipientStatistics {
        String recipientEmail;
        String recipientName;
        String recipientTeam;
        int[][] numOfResponsesPerSubQuestionPerChoice;
        double[] totalPerSubQuestion;
        int[] respondentsPerSubQuestion;

        RubricRecipientStatistics(String recipientEmail, String recipientName, String recipientTeam) {
            this.recipientEmail = recipientEmail;
            this.recipientName = recipientName;
            this.recipientTeam = recipientTeam;
            numOfResponsesPerSubQuestionPerChoice = new int[getNumOfRubricSubQuestions()][getNumOfRubricChoices()];
            totalPerSubQuestion = new double[getNumOfRubricSubQuestions()];
            respondentsPerSubQuestion = new int[getNumOfRubricSubQuestions()];
        }

        public void addResponseToRecipientStats(FeedbackResponseAttributes response) {
            if (!response.recipient.equalsIgnoreCase(recipientEmail)) {
                return;
            }

            FeedbackRubricResponseDetails rubricResponse = (FeedbackRubricResponseDetails) response.getResponseDetails();

            for (int i = 0; i < getNumOfRubricSubQuestions(); i++) {
                int choice = rubricResponse.getAnswer(i);

                if (choice >= 0) {
                    ++numOfResponsesPerSubQuestionPerChoice[i][choice];
                    totalPerSubQuestion[i] += getRubricWeights().get(choice);
                    respondentsPerSubQuestion[i]++;
                }
            }
        }

        /**
         * Returns a HTML string which contains a sequence of "td" tags.
         * The "td" tags have data related to a sub question.
         * The sequence of "td" tags are not enclosed in a "tr" tag.
         */
        public String getHtmlForSubQuestion(int subQuestion) {
            StringBuilder html = new StringBuilder(100);
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(subQuestion + 1);
            String subQuestionString = SanitizationHelper.sanitizeForHtml(alphabeticalIndex + ") "
                    + getRubricSubQuestions().get(subQuestion));
            DecimalFormat df = new DecimalFormat("0.00");

            List<String> cols = new ArrayList<>();

            // <td> entries which display recipient identification details and rubric subQuestion
            cols.add(recipientTeam);
            cols.add(recipientName);
            cols.add(subQuestionString);

            // <td> entries which display number of responses per subQuestion per rubric choice
            for (int i = 0; i < getNumOfRubricChoices(); i++) {
                cols.add(Integer.toString(numOfResponsesPerSubQuestionPerChoice[subQuestion][i]));
            }

            // <td> entries which display aggregate statistics
            cols.add(df.format(totalPerSubQuestion[subQuestion]));
            cols.add(respondentsPerSubQuestion[subQuestion] == 0 ? "0.00"
                    : df.format(totalPerSubQuestion[subQuestion] / respondentsPerSubQuestion[subQuestion]));

            // Generate HTML for all <td> entries using template
            for (String col : cols) {
                html.append(
                        Templates.populateTemplate(FormTemplates.RUBRIC_RESULT_RECIPIENT_STATS_BODY_ROW_FRAGMENT,
                        Slots.RUBRIC_RECIPIENT_STAT_CELL, col));
            }

            return html.toString();
        }

        /**
         * Returns a HTML string which contains a sequence of "tr" tags.
         * The "tr" tags enclose a sequence of "td" tags which have data related to a sub question.
         * The sequence of "tr" tags are not enclosed in a "tbody" tag.
         */
        public String getHtmlForAllSubQuestions() {
            StringBuilder html = new StringBuilder(100);

            for (int i = 0; i < getNumOfRubricSubQuestions(); i++) {
                String subQuestionStats = getHtmlForSubQuestion(i);
                html.append(Templates.populateTemplate(
                        FormTemplates.RUBRIC_RESULT_RECIPIENT_STATS_BODY_FRAGMENT,
                        Slots.RUBRIC_RECIPIENT_STAT_ROW, subQuestionStats));
            }

            return html.toString();
        }

        public String getCsvForSubQuestion(int subQuestion) {
            StringBuilder csv = new StringBuilder(100);
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(subQuestion + 1);
            String subQuestionString = SanitizationHelper.sanitizeForCsv(alphabeticalIndex + ") "
                    + getRubricSubQuestions().get(subQuestion));
            DecimalFormat df = new DecimalFormat("0.00");

            // Append recipient identification details and rubric subQuestion
            csv.append(recipientTeam).append(',')
               .append(recipientName).append(',')
               .append(recipientEmail).append(',')
               .append(subQuestionString);

            // Append number of responses per subQuestion per rubric choice
            for (int i = 0; i < getNumOfRubricChoices(); i++) {
                csv.append(',').append(Integer.toString(numOfResponsesPerSubQuestionPerChoice[subQuestion][i]));
            }

            // Append aggregate statistics
            csv.append(',').append(df.format(totalPerSubQuestion[subQuestion])).append(',')
               .append(respondentsPerSubQuestion[subQuestion] == 0 ? "0.00"
                       : df.format(totalPerSubQuestion[subQuestion] / respondentsPerSubQuestion[subQuestion]))
               .append(System.lineSeparator());

            return csv.toString();
        }

        public String getCsvForAllSubQuestions() {
            StringBuilder csv = new StringBuilder(100);

            for (int i = 0; i < getNumOfRubricSubQuestions(); i++) {
                csv.append(getCsvForSubQuestion(i));
            }

            return csv.toString();
        }
    }

    /**
     * Class to calculate the statistics of responses for a rubric question.
     */
    private static class RubricStatistics {

        private RubricStatistics() {
            // utility class
        }

        /**
         * Returns the frequency of being selected for each choice of each sub-question
         * and the total number of responses for each sub-question.
         *
         * <p>Last element in each row stores the total number of responses for the sub-question.
         *
         * <p>e.g.<br>
         * responseFrequency[subQuestionIndex][choiceIndex]
         * -> is the number of times choiceIndex is chosen for subQuestionIndex.<br>
         * responseFrequency[subQuestionIndex][numOfRubricChoices]
         * -> is the total number of the responses for the given sub-question.
         */
        public static int[][] calculateResponseFrequency(List<FeedbackResponseAttributes> responses,
                                                         FeedbackRubricQuestionDetails questionDetails) {
            int numOfRubricSubQuestions = questionDetails.getNumOfRubricSubQuestions();
            int numOfRubricChoices = questionDetails.getNumOfRubricChoices();
            int responseTotalIndex = numOfRubricChoices;

            int[][] responseFrequency = new int[numOfRubricSubQuestions][numOfRubricChoices + 1];
            // count frequencies
            for (FeedbackResponseAttributes response : responses) {
                FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) response.getResponseDetails();
                for (int i = 0; i < numOfRubricSubQuestions; i++) {
                    int chosenChoice = frd.getAnswer(i);
                    if (chosenChoice != -1) {
                        responseFrequency[i][chosenChoice] += 1;
                        responseFrequency[i][responseTotalIndex] += 1;
                    }
                }
            }
            return responseFrequency;
        }

        /**
         * Returns the frequency of being selected for each choice of each sub-question
         * and the total number of responses for each sub-question for excluding self.
         */
        public static int[][] calculateResponseFrequencyExcludingSelf(List<FeedbackResponseAttributes> responses,
                                                         FeedbackRubricQuestionDetails questionDetails) {

            List<FeedbackResponseAttributes> responsesExcludingSelf = responses.stream()
                    .filter(response -> !response.giver.equals(response.recipient)).collect(Collectors.toList());
            return calculateResponseFrequency(responsesExcludingSelf, questionDetails);
        }

        /**
         * Returns the calculated percentage frequencies for each choice and average value for each sub-question
         * The percentage value between [0,1] of each choice being selected for the sub-question.
         *
         * <p>Values are set to 0 if there are no responses to that sub-question.
         * Average value is set to 0 if there are no assigned weights.
         *
         * <p>e.g.<br>
         * percentageFrequencyAndAverageValue[subQuestionIndex][choiceIndex]
         * -> is the percentage choiceIndex is chosen for subQuestionIndex.<br>
         * percentageFrequencyAndAverageValue[subQuestionIndex][numOfRubricChoices]
         * -> is the average weight of the responses for the given sub-question.
         *
         * @param responseFrequency decides whether the value returned is for excluding-self or including-self.
         */
        public static float[][] calculatePercentageFrequencyAndAverage(FeedbackRubricQuestionDetails questionDetails,
                                                         int [][] responseFrequency) {
            Assumption.assertNotNull("Response Frequency should be initialised and calculated first.",
                                     (Object[]) responseFrequency);

            int numOfRubricSubQuestions = questionDetails.getNumOfRubricSubQuestions();
            int numOfRubricChoices = questionDetails.getNumOfRubricChoices();
            int responseTotalIndex = numOfRubricChoices;

            float[][] percentageFrequencyAndAverage = new float[numOfRubricSubQuestions][numOfRubricChoices + 1];
            // calculate percentage frequencies and average value
            for (int i = 0; i < percentageFrequencyAndAverage.length; i++) {
                int totalForSubQuestion = responseFrequency[i][responseTotalIndex];
                //continue to next row if no response for this sub-question
                if (totalForSubQuestion == 0) {
                    continue;
                }
                // divide responsesFrequency by totalForSubQuestion to get percentage
                for (int j = 0; j < numOfRubricChoices; j++) {
                    percentageFrequencyAndAverage[i][j] = (float) responseFrequency[i][j] / totalForSubQuestion;
                }
                // calculate the average for each sub-question
                if (questionDetails.hasAssignedWeights()) {
                    for (int j = 0; j < numOfRubricChoices; j++) {
                        float choiceWeight =
                                (float) (questionDetails.getRubricWeights().get(j)
                                        * percentageFrequencyAndAverage[i][j]);
                        percentageFrequencyAndAverage[i][numOfRubricChoices] += choiceWeight;
                    }
                }
            }
            return percentageFrequencyAndAverage;
        }

    }

}

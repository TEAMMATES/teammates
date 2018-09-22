package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {
    private static final Logger log = Logger.getLogger();

    private boolean hasAssignedWeights;
    private List<Double> mcqWeights;
    private double mcqOtherWeight;
    private int numOfMcqChoices;
    private List<String> mcqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;
    private StudentAttributes studentDoingQuestion;

    public FeedbackMcqQuestionDetails() {
        super(FeedbackQuestionType.MCQ);

        this.hasAssignedWeights = false;
        this.mcqWeights = new ArrayList<>();
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.mcqOtherWeight = 0;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    public int getNumOfMcqChoices() {
        return numOfMcqChoices;
    }

    public List<String> getMcqChoices() {
        return mcqChoices;
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public List<Double> getMcqWeights() {
        return new ArrayList<>(mcqWeights);
    }

    public double getMcqOtherWeight() {
        return mcqOtherWeight;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {

        int numOfMcqChoices = 0;
        List<String> mcqChoices = new LinkedList<>();
        boolean mcqOtherEnabled = false; // TODO change this when implementing "other, please specify" field

        if ("on".equals(HttpRequestHelper.getValueFromParamMap(
                                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG))) {
            mcqOtherEnabled = true;
        }

        String generatedMcqOptions =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS);

        if (generatedMcqOptions.equals(FeedbackParticipantType.NONE.toString())) {
            String numMcqChoicesCreatedString =
                    HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                           Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for MCQ", numMcqChoicesCreatedString);
            int numMcqChoicesCreated = Integer.parseInt(numMcqChoicesCreatedString);

            for (int i = 0; i < numMcqChoicesCreated; i++) {
                String paramName = Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i;
                String mcqChoice = HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
                if (mcqChoice != null && !mcqChoice.trim().isEmpty()) {
                    mcqChoices.add(mcqChoice);
                    numOfMcqChoices++;
                }
            }

            String hasAssignedWeightsString = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED);
            boolean hasAssignedWeights = "on".equals(hasAssignedWeightsString);
            List<Double> mcqWeights = getMcqWeights(
                    requestParameters, numMcqChoicesCreated, hasAssignedWeights);
            double mcqOtherWeight = getMcqOtherWeight(requestParameters, mcqOtherEnabled, hasAssignedWeights);
            setMcqQuestionDetails(
                    numOfMcqChoices, mcqChoices, mcqOtherEnabled, hasAssignedWeights, mcqWeights, mcqOtherWeight);
        } else {
            setMcqQuestionDetails(FeedbackParticipantType.valueOf(generatedMcqOptions));
        }
        return true;
    }

    private List<Double> getMcqWeights(Map<String, String[]> requestParameters,
            int numMcqChoicesCreated, boolean hasAssignedWeights) {
        List<Double> mcqWeights = new ArrayList<>();

        if (!hasAssignedWeights) {
            return mcqWeights;
        }

        for (int i = 0; i < numMcqChoicesCreated; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i);
            String weight = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-" + i);

            if (choice != null && !choice.trim().isEmpty() && weight != null) {
                try {
                    // Do not add weight to mcqWeights if the weight cannot be parsed
                    mcqWeights.add(Double.parseDouble(weight));
                } catch (NumberFormatException e) {
                    log.severe("Failed to parse weight for MCQ question: " + weight);
                }
            }
        }

        return mcqWeights;
    }

    private double getMcqOtherWeight(Map<String, String[]> requestParameters,
            boolean mcqOtherEnabled, boolean hasAssignedWeights) {

        double mcqOtherWeight = 0;

        if (!hasAssignedWeights || !mcqOtherEnabled) {
            return mcqOtherWeight;
        }

        String weightOther = HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT);

        Assumption.assertNotNull("Null 'other' weight of MCQ question", weightOther);
        Assumption.assertNotEmpty("Empty 'other' weight of MCQ question", weightOther);

        try {
            // Do not assign value to mcqOtherWeight if the weight can not be parsed.
            mcqOtherWeight = Double.parseDouble(weightOther);
        } catch (NumberFormatException e) {
            log.severe("Failed to parse \"other\" weight of MCQ question: " + weightOther);
        }
        return mcqOtherWeight;
    }

    private void setMcqQuestionDetails(int numOfMcqChoices, List<String> mcqChoices, boolean otherEnabled,
            boolean hasAssignedWeights, List<Double> mcqWeights, double mcqOtherWeight) {
        this.numOfMcqChoices = numOfMcqChoices;
        this.mcqChoices = mcqChoices;
        this.otherEnabled = otherEnabled;
        this.hasAssignedWeights = hasAssignedWeights;
        this.mcqWeights = mcqWeights;
        this.mcqOtherWeight = mcqOtherWeight;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    private void setMcqQuestionDetails(FeedbackParticipantType generateOptionsFor) {
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        Assumption.assertTrue(
                "Can only generate students, students (excluding self), teams, teams (excluding self) or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS
                || generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF
                || generateOptionsFor == FeedbackParticipantType.TEAMS
                || generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF
                || generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MCQ;
    }

    public boolean getOtherEnabled() {
        return otherEnabled;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMcqQuestionDetails newMcqDetails = (FeedbackMcqQuestionDetails) newDetails;

        if (this.numOfMcqChoices != newMcqDetails.numOfMcqChoices
                || !this.mcqChoices.containsAll(newMcqDetails.mcqChoices)
                || !newMcqDetails.mcqChoices.containsAll(this.mcqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMcqDetails.generateOptionsFor) {
            return true;
        }

        return this.otherEnabled != newMcqDetails.otherEnabled;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails,
            StudentAttributes student) {
        studentDoingQuestion = student;
        FeedbackMcqResponseDetails existingMcqResponse = (FeedbackMcqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        Boolean isOtherSelected = existingMcqResponse.isOtherOptionAnswer();

        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED,
                                    existingMcqResponse.getAnswerString().equals(choices.get(i)) ? "checked" : "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(System.lineSeparator());
        }
        if (otherEnabled) {
            String otherOptionFragmentTemplate = FormTemplates.MCQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                    Templates.populateTemplate(otherOptionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.TEXT_DISABLED, sessionIsOpen && isOtherSelected ? "" : "disabled",
                            Slots.CHECKED, isOtherSelected ? "checked" : "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MCQ_PARAM_IS_OTHER_OPTION_ANSWER,
                                    Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER,
                            Slots.MCQ_CHOICE_VALUE,
                                    SanitizationHelper.sanitizeForHtml(existingMcqResponse.getOtherFieldContent()),
                            Slots.MCQ_OTHER_OPTION_ANSWER, isOtherSelected ? "1" : "0");
            optionListHtml.append(otherOptionFragment).append(System.lineSeparator());
        }
        return Templates.populateTemplate(
                FormTemplates.MCQ_SUBMISSION_FORM,
                Slots.MCQ_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString());
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student) {
        studentDoingQuestion = student;
        List<String> choices = generateOptionList(courseId);

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;

        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(System.lineSeparator());
        }

        if (otherEnabled) {
            String otherOptionFragmentTemplate = FormTemplates.MCQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                       Templates.populateTemplate(otherOptionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.TEXT_DISABLED, "disabled",
                            Slots.CHECKED, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MCQ_PARAM_IS_OTHER_OPTION_ANSWER,
                                    Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER,
                            Slots.MCQ_CHOICE_VALUE, "",
                            Slots.MCQ_OTHER_OPTION_ANSWER, "0");
            optionListHtml.append(otherOptionFragment).append(System.lineSeparator());
        }

        return Templates.populateTemplate(
                FormTemplates.MCQ_SUBMISSION_FORM,
                Slots.MCQ_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString());
    }

    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<>();

        switch (generateOptionsFor) {
        case NONE:
            optionList = mcqChoices;
            break;
        case STUDENTS:
            //fallthrough
        case STUDENTS_EXCLUDING_SELF:
            List<StudentAttributes> studentList = StudentsLogic.inst().getStudentsForCourse(courseId);

            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
                studentList.removeIf(studentInList -> studentInList.email.equals(studentDoingQuestion.email));
            }

            for (StudentAttributes student : studentList) {
                optionList.add(student.name + " (" + student.team + ")");
            }

            optionList.sort(null);
            break;
        case TEAMS:
            //fallthrough
        case TEAMS_EXCLUDING_SELF:
            try {
                List<TeamDetailsBundle> teamList = CoursesLogic.inst().getTeamsForCourse(courseId);

                if (generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
                    teamList.removeIf(teamInList -> teamInList.name.equals(studentDoingQuestion.team));
                }

                for (TeamDetailsBundle team : teamList) {
                    optionList.add(team.name);
                }

                optionList.sort(null);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail("Course disappeared");
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorList =
                    InstructorsLogic.inst().getInstructorsForCourse(courseId);

            for (InstructorAttributes instructor : instructorList) {
                optionList.add(instructor.name);
            }

            optionList.sort(null);
            break;
        default:
            Assumption.fail("Trying to generate options for neither students, teams nor instructors");
            break;
        }

        return optionList;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MCQ_EDIT_FORM_OPTIONFRAGMENT;
        DecimalFormat weightFormat = new DecimalFormat("#.##");

        // Create MCQ options
        for (int i = 0; i < numOfMcqChoices; i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(mcqChoices.get(i)),
                            Slots.MCQ_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE);

            optionListHtml.append(optionFragment).append(System.lineSeparator());
        }

        // Create MCQ weights
        StringBuilder weightFragmentHtml = new StringBuilder();
        String weightFragmentTemplate = FormTemplates.MCQ_EDIT_FORM_WEIGHTFRAGMENT;
        for (int i = 0; i < numOfMcqChoices; i++) {
            String weightFragment =
                    Templates.populateTemplate(weightFragmentTemplate,
                            Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.MCQ_WEIGHT, hasAssignedWeights ? weightFormat.format(mcqWeights.get(i)) : "0",
                            Slots.MCQ_PARAM_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT);
            weightFragmentHtml.append(weightFragment).append(System.lineSeparator());
        }

        // Create MCQ other weight value
        String mcqOtherWeightValue = hasAssignedWeights && otherEnabled ? weightFormat.format(mcqOtherWeight) : "0";

        return Templates.populateTemplate(
                FormTemplates.MCQ_EDIT_FORM,
                Slots.MCQ_EDIT_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.NUMBER_OF_CHOICE_CREATED, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                Slots.MCQ_NUM_OF_MCQ_CHOICES, Integer.toString(numOfMcqChoices),
                Slots.CHECKED_OTHER_OPTION_ENABLED, otherEnabled ? "checked" : "",
                Slots.MCQ_PARAM_OTHER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTION,
                Slots.MCQ_PARAM_OTHER_OPTION_FLAG, Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG,
                Slots.MCQ_CHECKED_GENERATED_OPTION, generateOptionsFor == FeedbackParticipantType.NONE ? "" : "checked",
                Slots.MCQ_GENERATED_OPTIONS, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS,
                Slots.GENERATE_OPTIONS_FOR_VALUE, generateOptionsFor.toString(),
                Slots.STUDENT_SELECTED, generateOptionsFor == FeedbackParticipantType.STUDENTS ? "selected" : "",
                Slots.STUDENTS_TO_STRING, FeedbackParticipantType.STUDENTS.toString(),
                Slots.STUDENT_EXCLUDING_SELF_SELECTED,
                    generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF ? "selected" : "",
                Slots.STUDENTS_EXCLUDING_SELF_TO_STRING, FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString(),
                Slots.TEAM_SELECTED, generateOptionsFor == FeedbackParticipantType.TEAMS ? "selected" : "",
                Slots.TEAMS_TO_STRING, FeedbackParticipantType.TEAMS.toString(),
                Slots.TEAM_EXCLUDING_SELF_SELECTED,
                    generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF ? "selected" : "",
                Slots.TEAMS_EXCLUDING_SELF_TO_STRING, FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString(),
                Slots.INSTRUCTOR_SELECTED, generateOptionsFor == FeedbackParticipantType.INSTRUCTORS ? "selected" : "",
                Slots.INSTRUCTORS_TO_STRING, FeedbackParticipantType.INSTRUCTORS.toString(),
                Slots.MCQ_TOOLTIPS_ASSIGN_WEIGHT, Const.Tooltips.FEEDBACK_QUESTION_MCQ_ASSIGN_WEIGHTS,
                Slots.MCQ_PARAM_HAS_ASSIGN_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED,
                Slots.MCQ_EDIT_FORM_WEIGHT_FRAGMENTS, weightFragmentHtml.toString(),
                Slots.MCQ_PARAM_OTHER_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT,
                Slots.MCQ_OTHER_WEIGHT, mcqOtherWeightValue,
                Slots.MCQ_ASSIGN_WEIGHT_CHECKBOX, hasAssignedWeights ? "checked" : "");
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        numOfMcqChoices = 2;
        mcqChoices.add("");
        mcqChoices.add("");
        hasAssignedWeights = false;

        return "<div id=\"mcqForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(200);
        String optionFragmentTemplate = FormTemplates.MCQ_ADDITIONAL_INFO_FRAGMENT;

        if (generateOptionsFor != FeedbackParticipantType.NONE) {
            String optionHelpText = String.format(
                    "<br>The options for this question is automatically generated from the list of all %s in this course.",
                    generateOptionsFor.toString().toLowerCase());
            optionListHtml.append(optionHelpText);
        }

        if (numOfMcqChoices > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < numOfMcqChoices; i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(mcqChoices.get(i)));

                optionListHtml.append(optionFragment);
            }
        }
        if (otherEnabled) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate, Slots.MCQ_CHOICE_VALUE, "Others");
            optionListHtml.append(optionFragment);
        }
        optionListHtml.append("</ul>");

        String additionalInfo = Templates.populateTemplate(
                FormTemplates.MCQ_ADDITIONAL_INFO,
                Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
                Slots.MCQ_ADDITIONAL_INFO_FRAGMENTS, optionListHtml.toString());

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {

        if ("student".equals(view) || responses.isEmpty()) {
            return "";
        }

        StringBuilder responseSummaryFragments = new StringBuilder();

        MCQStatistics mcqStats = new MCQStatistics(this);
        Map<String, Integer> answerFrequency = mcqStats.collateAnswerFrequency(responses);
        // Do not calculate weighted percentage if weights are not enabled.
        Map<String, Double> weightedPercentagePerOption =
                hasAssignedWeights ? mcqStats.calculateWeightedPercentagePerOption(answerFrequency)
                : new LinkedHashMap<>();

        DecimalFormat df = new DecimalFormat("#.##");

        for (String key : answerFrequency.keySet()) {
            int count = answerFrequency.get(key);
            // If weights are allowed, show the corresponding weights of a choice.
            String weightString = "";
            if ("Other".equals(key)) {
                weightString = hasAssignedWeights ? df.format(mcqOtherWeight) : "-";
            } else {
                weightString = hasAssignedWeights ? df.format(mcqWeights.get(mcqChoices.indexOf(key))) : "-";
            }

            responseSummaryFragments.append(Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                    Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(key),
                    Slots.MCQ_WEIGHT, weightString,
                    Slots.COUNT, Integer.toString(count),
                    Slots.PERCENTAGE, df.format(100 * (double) count / responses.size()),
                    Slots.WEIGHTED_PERCENTAGE,
                            hasAssignedWeights ? df.format(weightedPercentagePerOption.get(key)) : "-"));
        }

        // If weights are assigned, create the per recipient statistics table,
        // otherwise pass an empty string in it's place.
        String recipientStatsHtml = "";
        if (hasAssignedWeights) {
            // Sort the list of responseAttributes based on recipient team and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = mcqStats.getResponseAttributesSorted(responses, bundle);
            String header = mcqStats.getRecipientStatsHeaderHtml();
            String body = mcqStats.getPerRecipientStatsBodyHtml(sortedResponses, bundle);

            recipientStatsHtml = Templates.populateTemplate(
                    FormTemplates.MCQ_RESULT_RECIPIENT_STATS,
                    Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, header,
                    Slots.TABLE_BODY_HTML, body);
        }
        return Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS,
                Slots.FRAGMENTS, responseSummaryFragments.toString(),
                Slots.MCQ_RECIPIENT_STATS_HTML, recipientStatsHtml);
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
        MCQStatistics mcqStats = new MCQStatistics(this);
        Map<String, Integer> answerFrequency = mcqStats.collateAnswerFrequency(responses);
        // Add the Response Summary Statistics to the CSV String.
        csv.append(mcqStats.getResponseSummaryStatsCsv(answerFrequency, responses.size()));

        // If weights are assigned, add the 'Per Recipient Statistics' to the CSV string.
        if (hasAssignedWeights) {
            csv.append(System.lineSeparator())
                .append("Per Recipient Statistics").append(System.lineSeparator())
                .append(mcqStats.getPerRecipientResponseStatsCsv(responses, bundle));
        }
        return csv.toString();
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"MCQ\"><a href=\"javascript:;\"> "
               + Const.FeedbackQuestionTypeNames.MCQ + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails(String courseId) {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (numOfMcqChoices < Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES
                        + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If weights are enabled, number of choices and weights should be same.
            // If user enters an invalid weight for a valid choice,
            // the mcqChoices.size() will be greater than mcqWeights.size(),
            // in that case, trigger this error.
            if (hasAssignedWeights && mcqChoices.size() != mcqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!mcqWeights.isEmpty() || mcqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, but other option is disabled, and mcqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && mcqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, and any of the weights have negative value,
            // trigger this error.
            if (hasAssignedWeights && !mcqWeights.isEmpty()) {
                for (double weight : mcqWeights) {
                    if (weight < 0) {
                        errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
                    }
                }
                // If 'Other' option is enabled, and other weight has negative value,
                // trigger this error.
                if (otherEnabled && mcqOtherWeight < 0) {
                    errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
                }
            }
        }
        //TODO: check that mcq options do not repeat. needed?

        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseAttributes response : responses) {
            FeedbackMcqResponseDetails frd = (FeedbackMcqResponseDetails) response.getResponseDetails();

            if (!otherEnabled && generateOptionsFor == FeedbackParticipantType.NONE
                    && !mcqChoices.contains(frd.getAnswerString())) {
                errors.add(frd.getAnswerString() + Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION);
            }
        }
        return errors;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return true;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    /**
     * Class that contains methods which are used to calculate both MCQ and MSQ response statistics.
     */
    public abstract static class MultipleOptionStatistics {
        protected boolean hasAssignedWeights;
        protected List<String> choices;
        protected List<Double> weights;
        protected double otherWeight;
        protected boolean otherEnabled;
        protected int numOfChoices;

        /**
         * Calculates the answer frequency for each option based on the received responses for a question.
         */
        protected abstract Map<String, Integer> collateAnswerFrequency(List<FeedbackResponseAttributes> responses);

        /**
         * Calculates the weighted percentage for each option.
         * weighted percentage = (response count per option) * (weight of that option) / totalWeightedResponseCount<br>
         * where as, totalWeightedResponseCount is defined as:<br>
         * totalWeightedResponseCount += [response count of option i * weight of option i] for all options.
         * @param answerFrequency Response count of each option.
         */
        public Map<String, Double> calculateWeightedPercentagePerOption(Map<String, Integer> answerFrequency) {
            Map<String, Double> weightedPercentagePerOption = new LinkedHashMap<>();

            Assumption.assertTrue("Weights should be enabled when calling the function", hasAssignedWeights);
            double totalWeightedResponseCount = calculateTotalWeightedResponseCount(answerFrequency);

            for (int i = 0; i < choices.size(); i++) {
                String option = choices.get(i);
                double weight = weights.get(i);
                weightedPercentagePerOption.put(option, weight);
            }

            if (otherEnabled) {
                weightedPercentagePerOption.put("Other", otherWeight);
            }

            for (String key : weightedPercentagePerOption.keySet()) {
                int frequency = answerFrequency.get(key);
                double weight = weightedPercentagePerOption.get(key);
                double weightedPercentage = totalWeightedResponseCount == 0 ? 0
                        : 100 * ((frequency * weight) / totalWeightedResponseCount);

                // Replace the value by the actual weighted percentage.
                weightedPercentagePerOption.put(key, weightedPercentage);
            }
            return weightedPercentagePerOption;
        }

        /**
         * Calculates the sum of the product of response count and weight of that option, for all options.
         * totalWeightedResponseCount += [(responseCount of option i) * (weight of option i)] for all options.
         */
        public double calculateTotalWeightedResponseCount(Map<String, Integer> answerFrequency) {
            double totalWeightedResponseCount = 0;
            for (String choice : answerFrequency.keySet()) {
                double weight = "Other".equals(choice) ? otherWeight : weights.get(choices.indexOf(choice));
                int responseCount = answerFrequency.get(choice);
                totalWeightedResponseCount += responseCount * weight;
            }
            return totalWeightedResponseCount;
        }

        /**
         * Returns a list of {@link FeedbackResponseAttributes} sorted by comparing recipient Team name and
         * recipient name for each recipient email.
         * @param unsortedResponses The list of unsorted responses that needs to be sorted.
         * @param bundle Result bundle that is used to retrieve recipientTeamName and recipientName for each recipient.
         */
        public List<FeedbackResponseAttributes> getResponseAttributesSorted(
                List<FeedbackResponseAttributes> unsortedResponses, FeedbackSessionResultsBundle bundle) {
            List<FeedbackResponseAttributes> responses = new LinkedList<>(unsortedResponses);

            responses.sort(Comparator
                    .comparing((FeedbackResponseAttributes obj) -> bundle.getTeamNameForEmail(obj.recipient))
                    .thenComparing(obj -> bundle.getNameForEmail(obj.recipient)));

            return responses;
        }

        /**
         * Generates statistics for each recipient for 'Per recipient statistics' to be used for
         * both the results page and csv files.
         * The specific stats that are generated are -<br>
         * Team, Name, Response count for each option, Total, Average.
         * @param recipientEmail Email of the recipient whose statistics should be calculated
         * @param recipientResponses Map containing the response count of each choice for the recipient
         * @param bundle Feedback session results bundle to get the team name and name of the recipient
         * @return List of strings containing the 'Per recipient stats' of the recipient
         */
        public List<String> generateStatisticsForEachRecipient(String recipientEmail,
                Map<String, Integer> recipientResponses, FeedbackSessionResultsBundle bundle) {

            Assumption.assertTrue("Weights should be enabled when calling the function", hasAssignedWeights);
            List<String> recipientStats = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.00");

            String recipientName = bundle.getNameForEmail(recipientEmail);
            String recipientTeam = bundle.getTeamNameForEmail(recipientEmail);
            double total = 0;
            double average = 0;
            int numOfResponsesForThisRecipient = 0;

            recipientStats.add(recipientTeam);
            recipientStats.add(recipientName);

            for (Map.Entry<String, Integer> countPerChoice : recipientResponses.entrySet()) {
                String choice = countPerChoice.getKey();
                int responseCount = countPerChoice.getValue();

                double weight = 0;
                recipientStats.add(Integer.toString(responseCount));

                // Get the weight of the choice.
                if ("Other".equals(choice)) {
                    weight = otherWeight;
                } else {
                    weight = weights.get(choices.indexOf(choice));
                }
                // Add the total weight of all responses of this choice to total.
                total += responseCount * weight;
                numOfResponsesForThisRecipient += responseCount;
            }

            recipientStats.add(df.format(total));
            average = numOfResponsesForThisRecipient == 0 ? 0 : total / numOfResponsesForThisRecipient;
            recipientStats.add(df.format(average));

            return recipientStats;
        }

        private String getRecipientStatsHeaderFragmentHtml(String header) {
            return Templates.populateTemplate(
                    FormTemplates.MCQ_RESULT_RECIPIENT_STATS_HEADER_FRAGMENT,
                    Slots.STATS_TITLE, header);
        }

        /**
         * Returns the html string for 'Per Recipient Statistics' table header.
         */
        public String getRecipientStatsHeaderHtml() {
            StringBuilder headerBuilder = new StringBuilder(100);
            DecimalFormat df = new DecimalFormat("#.##");
            StringBuilder choicesHtmlBuilder = new StringBuilder(100);

            for (int i = 0; i < choices.size(); i++) {
                String weight = df.format(weights.get(i));
                String html = getRecipientStatsHeaderFragmentHtml(choices.get(i) + " [" + weight + "]");
                choicesHtmlBuilder.append(html);
            }
            if (otherEnabled) {
                String otherWeightString = df.format(otherWeight);
                String html = getRecipientStatsHeaderFragmentHtml("Other" + " [" + otherWeightString + "]");
                choicesHtmlBuilder.append(html);
            }

            headerBuilder.append(getRecipientStatsHeaderFragmentHtml("Team"))
                         .append(getRecipientStatsHeaderFragmentHtml("Recipient Name"))
                         .append(choicesHtmlBuilder.toString())
                         .append(getRecipientStatsHeaderFragmentHtml("Total"))
                         .append(getRecipientStatsHeaderFragmentHtml("Average"));

            return headerBuilder.toString();
        }

        /**
         * Returns a String containing the Response Summary statistics for CSV files.
         */
        public String getResponseSummaryStatsCsv(Map<String, Integer> answerFrequency, int totalResponseCount) {

            String header = "";

            StringBuilder fragments = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#.##");

            // If weights are assigned, CSV file should include 'Weight' and 'Weighted Percentage' column as well.
            if (hasAssignedWeights) {
                header = "Choice, Weight, Response Count, Percentage (%), Weighted Percentage (%)";
                Map<String, Double> weightedPercentagePerOption = calculateWeightedPercentagePerOption(answerFrequency);

                for (String key : answerFrequency.keySet()) {
                    int responseCount = answerFrequency.get(key);
                    String weightString = "";
                    if ("Other".equals(key)) {
                        weightString = df.format(otherWeight);
                    } else {
                        weightString = df.format(weights.get(choices.indexOf(key)));
                    }

                    fragments.append(SanitizationHelper.sanitizeForCsv(key)).append(',')
                             .append(SanitizationHelper.sanitizeForCsv(weightString)).append(',')
                             .append(Integer.toString(responseCount)).append(',')
                             .append(df.format(100 * (double) responseCount / totalResponseCount)).append(',')
                             .append(df.format(weightedPercentagePerOption.get(key))).append(System.lineSeparator());
                }
            } else {
                header = "Choice, Response Count, Percentage (%)";

                answerFrequency.forEach((key, value) -> fragments.append(SanitizationHelper.sanitizeForCsv(key)).append(',')
                        .append(value.toString()).append(',')
                        .append(df.format(100 * (double) value / totalResponseCount)).append(System.lineSeparator()));
            }

            return header + System.lineSeparator() + fragments.toString();
        }

        public String getPerRecipientResponseStatsHeaderCsv() {
            StringBuilder header = new StringBuilder(100);
            DecimalFormat df = new DecimalFormat("#.##");

            header.append("Team, Recipient Name,");

            for (int i = 0; i < numOfChoices; i++) {
                String choiceString = choices.get(i) + " [" + df.format(weights.get(i)) + "]";
                header.append(SanitizationHelper.sanitizeForCsv(choiceString)).append(',');
            }
            if (otherEnabled) {
                String otherOptionString = "Other [" + df.format(otherWeight) + "]";
                header.append(SanitizationHelper.sanitizeForCsv(otherOptionString)).append(',');
            }
            header.append("Total, Average").append(System.lineSeparator());
            return header.toString();
        }

        /**
         * Returns the 'Per Recipient' stats body part for CSV files.<br>
         * @param responses The response attribute list should be sorted first before passing as an argument.
         * @param bundle Feedback session results bundle
         */
        protected String getPerRecipientResponseStatsBodyCsv(List<FeedbackResponseAttributes> responses,
                FeedbackSessionResultsBundle bundle) {
            StringBuilder bodyBuilder = new StringBuilder(100);
            Map<String, Map<String, Integer>> perRecipientResponses = calculatePerRecipientResponseCount(responses);

            for (Map.Entry<String, Map<String, Integer>> entry : perRecipientResponses.entrySet()) {
                String recipient = entry.getKey();
                Map<String, Integer> responsesForRecipient = entry.getValue();
                String perRecipientStats = getPerRecipientResponseStatsBodyFragmentCsv(
                        recipient, responsesForRecipient, bundle);
                bodyBuilder.append(perRecipientStats);
            }

            return bodyBuilder.toString();

        }

        /**
         * Returns a string containing a per recipient response stats for a single recipient.
         */
        private String getPerRecipientResponseStatsBodyFragmentCsv(String recipientEmail,
                Map<String, Integer> recipientResponses, FeedbackSessionResultsBundle bundle) {
            StringBuilder fragments = new StringBuilder(100);
            List<String> statsForEachRecipient = generateStatisticsForEachRecipient(
                    recipientEmail, recipientResponses, bundle);

            // Add each column data in fragments
            fragments.append(String.join(", ", statsForEachRecipient) + System.lineSeparator());
            return fragments.toString();
        }

        /**
         * Returns a Map containing response counts for each option for every recipient.
         */
        protected abstract Map<String, Map<String, Integer>> calculatePerRecipientResponseCount(
                List<FeedbackResponseAttributes> responses);

    }

    /**
     * Class to calculate result statistics of responses for MCQ questions.
     */
    private static class MCQStatistics extends MultipleOptionStatistics {

        MCQStatistics(FeedbackMcqQuestionDetails mcqDetails) {
            this.choices = mcqDetails.getMcqChoices();
            this.numOfChoices = choices.size();
            this.weights = mcqDetails.getMcqWeights();
            this.otherEnabled = mcqDetails.getOtherEnabled();
            this.hasAssignedWeights = mcqDetails.hasAssignedWeights();
            this.otherWeight = mcqDetails.getMcqOtherWeight();
        }

        /**
         * Calculates the answer frequency for each option based on the received responses.
         */
        protected Map<String, Integer> collateAnswerFrequency(List<FeedbackResponseAttributes> responses) {
            Map<String, Integer> answerFrequency = new LinkedHashMap<>();

            for (String option : choices) {
                answerFrequency.put(option, 0);
            }

            if (otherEnabled) {
                answerFrequency.put("Other", 0);
            }

            for (FeedbackResponseAttributes response : responses) {
                FeedbackResponseDetails responseDetails = response.getResponseDetails();
                boolean isOtherOptionAnswer =
                        ((FeedbackMcqResponseDetails) responseDetails).isOtherOptionAnswer();
                String key = isOtherOptionAnswer ? "Other" : responseDetails.getAnswerString();

                answerFrequency.put(key, answerFrequency.getOrDefault(key, 0) + 1);
            }

            return answerFrequency;
        }

        /**
         * Returns a Map containing response counts for each option for every recipient.
         */
        protected Map<String, Map<String, Integer>> calculatePerRecipientResponseCount(
                List<FeedbackResponseAttributes> responses) {
            Map<String, Map<String, Integer>> perRecipientResponse = new LinkedHashMap<>();

            responses.forEach(response -> {
                perRecipientResponse.computeIfAbsent(response.recipient, key -> {
                    // construct default value for responseCount
                    Map<String, Integer> responseCountPerOption = new LinkedHashMap<>();
                    for (String choice : choices) {
                        responseCountPerOption.put(choice, 0);
                    }
                    if (otherEnabled) {
                        responseCountPerOption.put("Other", 0);
                    }
                    return responseCountPerOption;
                });
                perRecipientResponse.computeIfPresent(response.recipient, (key, responseCountPerOption) -> {
                    // update responseCount here
                    FeedbackMcqResponseDetails frd = (FeedbackMcqResponseDetails) response.getResponseDetails();
                    boolean isOtherAnswer = frd.isOtherOptionAnswer();
                    String answer = isOtherAnswer ? "Other" : frd.getAnswerString();

                    responseCountPerOption.computeIfPresent(answer, (choice, count) -> {
                        return ++count;
                    });
                    return responseCountPerOption;
                });
            });
            return perRecipientResponse;
        }

        // Generate Recipient Response statistics for csv files.

        /**
         * Returns a String containing the 'Per Recipient Statistics' stats for CSV files for all recipients.
         */
        public String getPerRecipientResponseStatsCsv(List<FeedbackResponseAttributes> responses,
                FeedbackSessionResultsBundle bundle) {
            String header = getPerRecipientResponseStatsHeaderCsv();
            // Get the response attributes sorted based on Recipient Team name and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = getResponseAttributesSorted(responses, bundle);
            String body = getPerRecipientResponseStatsBodyCsv(sortedResponses, bundle);

            return header + body;
        }

        // Generate Recipient Response statistics for result page.

        /**
         * Returns a HTML string which contains a sequence of "tr" tags.
         * The "tr" tags enclose a sequence of "td" tags which have data related to a sub question.
         * The sequence of "tr" tags are not enclosed in a "tbody" tag.
         */
        public String getPerRecipientStatsBodyHtml(List<FeedbackResponseAttributes> responses,
                FeedbackSessionResultsBundle bundle) {
            StringBuilder bodyBuilder = new StringBuilder(100);
            Map<String, Map<String, Integer>> perRecipientResponses = calculatePerRecipientResponseCount(responses);

            for (Map.Entry<String, Map<String, Integer>> entry : perRecipientResponses.entrySet()) {
                String recipient = entry.getKey();
                Map<String, Integer> responsesForRecipient = entry.getValue();
                String statsRow = getPerRecipientStatsBodyFragmentHtml(recipient, responsesForRecipient, bundle);
                bodyBuilder.append(Templates.populateTemplate(FormTemplates.MCQ_RESULT_RECIPIENT_STATS_BODY_FRAGMENT,
                        Slots.MCQ_RECIPIENT_STAT_ROW, statsRow));
            }

            return bodyBuilder.toString();
        }

        /**
         * Returns a HTML string which contains a sequence of "td" tags.
         * The "td" tags have data related to a sub question.
         * The sequence of "td" tags are not enclosed in a "tr" tag.
         */
        private String getPerRecipientStatsBodyFragmentHtml(String recipientEmail,
                Map<String, Integer> recipientResponses, FeedbackSessionResultsBundle bundle) {
            StringBuilder html = new StringBuilder(100);

            List<String> cols = generateStatisticsForEachRecipient(recipientEmail, recipientResponses, bundle);

            // Generate HTML for all <td> entries using template
            for (String col : cols) {
                html.append(
                        Templates.populateTemplate(FormTemplates.MCQ_RESULT_RECIPIENT_STATS_BODY_ROW_FRAGMENT,
                        Slots.MCQ_RECIPIENT_STAT_CELL, col));
            }

            return html.toString();
        }
    }

}

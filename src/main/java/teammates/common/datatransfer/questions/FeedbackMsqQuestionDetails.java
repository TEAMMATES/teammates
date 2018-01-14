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
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackMsqQuestionDetails extends FeedbackQuestionDetails {
    private int numOfMsqChoices;
    private List<String> msqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;
    private int maxSelectableChoices;
    private int minSelectableChoices;

    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);

        this.numOfMsqChoices = 0;
        this.msqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
        this.maxSelectableChoices = Integer.MIN_VALUE;
        this.minSelectableChoices = Integer.MIN_VALUE;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        int numOfMsqChoices = 0;
        List<String> msqChoices = new LinkedList<>();
        boolean msqOtherEnabled = false;

        String otherOptionFlag =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG);

        if ("on".equals(otherOptionFlag)) {
            msqOtherEnabled = true;
        }

        int msqMaxSelectableChoices = Integer.MIN_VALUE;
        int msqMinSelectableChoices = Integer.MIN_VALUE;
        String maxSelectableChoicesParam = HttpRequestHelper.getValueFromParamMap(requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES);
        String minSelectableChoicesParam = HttpRequestHelper.getValueFromParamMap(requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES);

        if (maxSelectableChoicesParam != null) {
            msqMaxSelectableChoices = Integer.parseInt(maxSelectableChoicesParam);
        }

        if (minSelectableChoicesParam != null) {
            msqMinSelectableChoices = Integer.parseInt(minSelectableChoicesParam);
        }

        this.maxSelectableChoices = msqMaxSelectableChoices;
        this.minSelectableChoices = msqMinSelectableChoices;
        String generatedMsqOptions =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS);
        if (generatedMsqOptions.equals(FeedbackParticipantType.NONE.toString())) {
            String numMsqChoicesCreatedString =
                    HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                           Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for MSQ", numMsqChoicesCreatedString);
            int numMsqChoicesCreated = Integer.parseInt(numMsqChoicesCreatedString);

            for (int i = 0; i < numMsqChoicesCreated; i++) {
                String msqChoice =
                        HttpRequestHelper.getValueFromParamMap(
                                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-" + i);
                if (msqChoice != null && !msqChoice.trim().isEmpty()) {
                    msqChoices.add(msqChoice);
                    numOfMsqChoices++;
                }
            }

            setMsqQuestionDetails(numOfMsqChoices, msqChoices, msqOtherEnabled);
        } else {
            String courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
            setMsqQuestionDetails(FeedbackParticipantType.valueOf(generatedMsqOptions), courseId);
        }
        return true;
    }

    private void setMsqQuestionDetails(int numOfMsqChoices, List<String> msqChoices, boolean otherEnabled) {
        this.numOfMsqChoices = numOfMsqChoices;
        this.msqChoices = msqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    private void setMsqQuestionDetails(FeedbackParticipantType generateOptionsFor, String courseId) {
        this.msqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        this.numOfMsqChoices = generateOptionList(courseId).size();
        Assumption.assertTrue("Can only generate students, teams or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS
                || generateOptionsFor == FeedbackParticipantType.TEAMS
                || generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MSQ;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    public boolean getOtherEnabled() {
        return otherEnabled;
    }

    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

        if (this.numOfMsqChoices != newMsqDetails.numOfMsqChoices
                || !this.msqChoices.containsAll(newMsqDetails.msqChoices)
                || !newMsqDetails.msqChoices.containsAll(this.msqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMsqDetails.generateOptionsFor) {
            return true;
        }

        if (this.maxSelectableChoices == Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if max selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices == Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if min selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices != Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE
                && this.minSelectableChoices < newMsqDetails.minSelectableChoices) {
            // A more strict min selectable choices restriction is placed
            return true;
        }

        if (this.maxSelectableChoices != Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE
                && this.maxSelectableChoices > newMsqDetails.maxSelectableChoices) {
            // A more strict max selectable choices restriction is placed
            return true;
        }

        return this.otherEnabled != newMsqDetails.otherEnabled;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        FeedbackMsqResponseDetails existingMsqResponse = (FeedbackMsqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        Boolean isOtherSelected = existingMsqResponse.isOtherOptionAnswer();

        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED, existingMsqResponse.contains(choices.get(i)) ? "checked" : "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(choices.get(i)),
                            Slots.MSQ_CHOICE_TEXT, SanitizationHelper.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        if (otherEnabled) {
            String otherOptionFragmentTemplate = FormTemplates.MSQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                    Templates.populateTemplate(otherOptionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.TEXT_DISABLED, sessionIsOpen && isOtherSelected ? "" : "disabled",
                            Slots.CHECKED, isOtherSelected ? "checked" : "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_PARAM_IS_OTHER_OPTION_ANSWER,
                                    Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER,
                            Slots.MSQ_CHOICE_VALUE,
                                    SanitizationHelper.sanitizeForHtml(existingMsqResponse.getOtherFieldContent()),
                            Slots.MSQ_OTHER_OPTION_ANSWER, isOtherSelected ? "1" : "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }

        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        if (!isMinSelectableChoicesEnabled) {
            // additional checkbox for user to submit a blank response ("None of the above")
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED, existingMsqResponse.contains("") ? "checked" : "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_CHOICE_VALUE, "",
                            Slots.MSQ_CHOICE_TEXT, "<i>" + Const.NONE_OF_THE_ABOVE + "</i>");
            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.MSQ_SUBMISSION_FORM,
                Slots.MSQ_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.MSQ_IS_MAX_SELECTABLE_CHOICES_ENABLED, isMaxSelectableChoicesEnabled ? "" : "disabled",
                Slots.MSQ_IS_MIN_SELECTABLE_CHOICES_ENABLED, isMinSelectableChoicesEnabled ? "" : "disabled",
                Slots.MSQ_DISPLAY_MAX_SELECTABLE_CHOICES_HINT, isMaxSelectableChoicesEnabled ? "" : "hidden",
                Slots.MSQ_DISPLAY_MIN_SELECTABLE_CHOICES_HINT, isMinSelectableChoicesEnabled ? "" : "hidden",
                Slots.MSQ_PARAM_MAX_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES,
                Slots.MSQ_PARAM_MIN_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES,
                Slots.MSQ_MAX_SELECTABLE_CHOICES,
                        isMaxSelectableChoicesEnabled ? Integer.toString(maxSelectableChoices) : "-1",
                Slots.MSQ_MIN_SELECTABLE_CHOICES,
                        isMinSelectableChoicesEnabled ? Integer.toString(minSelectableChoices) : "-1");
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        List<String> choices = generateOptionList(courseId);

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(choices.get(i)),
                            Slots.MSQ_CHOICE_TEXT, SanitizationHelper.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment);
            optionListHtml.append(Const.EOL);
        }

        if (otherEnabled) {
            String otherOptionFragmentTemplate = FormTemplates.MSQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                       Templates.populateTemplate(otherOptionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.TEXT_DISABLED, "disabled",
                            Slots.CHECKED, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_PARAM_IS_OTHER_OPTION_ANSWER,
                                    Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER,
                            Slots.MSQ_CHOICE_VALUE, "",
                            Slots.MSQ_OTHER_OPTION_ANSWER, "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }

        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        if (!isMinSelectableChoicesEnabled) {
            // additional checkbox for user to submit a blank response ("None of the above")
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CHECKED, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.MSQ_CHOICE_VALUE, "",
                            Slots.MSQ_CHOICE_TEXT, "<i>" + Const.NONE_OF_THE_ABOVE + "</i>");
            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.MSQ_SUBMISSION_FORM,
                Slots.MSQ_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.MSQ_IS_MAX_SELECTABLE_CHOICES_ENABLED, isMaxSelectableChoicesEnabled ? "" : "disabled",
                Slots.MSQ_IS_MIN_SELECTABLE_CHOICES_ENABLED, isMinSelectableChoicesEnabled ? "" : "disabled",
                Slots.MSQ_DISPLAY_MAX_SELECTABLE_CHOICES_HINT, isMaxSelectableChoicesEnabled ? "" : "hidden",
                Slots.MSQ_DISPLAY_MIN_SELECTABLE_CHOICES_HINT, isMinSelectableChoicesEnabled ? "" : "hidden",
                Slots.MSQ_PARAM_MAX_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES,
                Slots.MSQ_PARAM_MIN_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES,
                Slots.MSQ_MAX_SELECTABLE_CHOICES,
                        isMaxSelectableChoicesEnabled ? Integer.toString(maxSelectableChoices) : "-1",
                Slots.MSQ_MIN_SELECTABLE_CHOICES,
                        isMinSelectableChoicesEnabled ? Integer.toString(minSelectableChoices) : "-1");
    }

    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<>();

        switch (generateOptionsFor) {
        case NONE:
            optionList = msqChoices;
            break;
        case STUDENTS:
            List<StudentAttributes> studentList =
                    StudentsLogic.inst().getStudentsForCourse(courseId);

            for (StudentAttributes student : studentList) {
                optionList.add(student.name + " (" + student.team + ")");
            }

            optionList.sort(null);
            break;
        case TEAMS:
            try {
                List<TeamDetailsBundle> teamList =
                        CoursesLogic.inst().getTeamsForCourse(courseId);

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
                    InstructorsLogic.inst().getInstructorsForCourse(
                            courseId);

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

        String optionFragmentTemplate = FormTemplates.MSQ_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < msqChoices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(msqChoices.get(i)),
                            Slots.MSQ_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        boolean isMaxSelectableChoicesDisabled = maxSelectableChoices == Integer.MIN_VALUE;
        boolean isMinSelectableChoicesDisabled = minSelectableChoices == Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.MSQ_EDIT_FORM,
                Slots.MSQ_EDIT_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.NUMBER_OF_CHOICE_CREATED, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                Slots.MSQ_NUMBER_OF_CHOICES, Integer.toString(numOfMsqChoices),
                Slots.CHECKED_OTHER_OPTION_ENABLED, otherEnabled ? "checked" : "",
                Slots.MSQ_PARAM_OTHER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTION,
                Slots.MSQ_PARAM_OTHER_OPTION_FLAG, Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG,
                Slots.MSQ_CHECKED_GENERATED_OPTIONS, generateOptionsFor == FeedbackParticipantType.NONE ? "" : "checked",
                Slots.GENERATED_OPTIONS, Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                Slots.GENERATE_OPTIONS_FOR_VALUE, generateOptionsFor.toString(),
                Slots.STUDENT_SELECTED, generateOptionsFor == FeedbackParticipantType.STUDENTS ? "selected" : "",
                Slots.STUDENTS_TO_STRING, FeedbackParticipantType.STUDENTS.toString(),
                Slots.TEAM_SELECTED, generateOptionsFor == FeedbackParticipantType.TEAMS ? "selected" : "",
                Slots.TEAMS_TO_STRING, FeedbackParticipantType.TEAMS.toString(),
                Slots.INSTRUCTOR_SELECTED, generateOptionsFor == FeedbackParticipantType.INSTRUCTORS ? "selected" : "",
                Slots.INSTRUCTORS_TO_STRING, FeedbackParticipantType.INSTRUCTORS.toString(),
                Slots.MSQ_IS_MAX_SELECTABLE_CHOICES_ENABLED, isMaxSelectableChoicesDisabled ? "" : "checked",
                Slots.MSQ_IS_MIN_SELECTABLE_CHOICES_ENABLED, isMinSelectableChoicesDisabled ? "" : "checked",
                Slots.MSQ_PARAM_ENABLED_MAX_SELECTABLE_CHOICES,
                        Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ENABLE_MAX_SELECTABLE_CHOICES,
                Slots.MSQ_PARAM_ENABLED_MIN_SELECTABLE_CHOICES,
                        Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ENABLE_MIN_SELECTABLE_CHOICES,
                Slots.MSQ_PARAM_MAX_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES,
                Slots.MSQ_PARAM_MIN_SELECTABLE_CHOICES, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES,
                Slots.MSQ_MAX_SELECTABLE_CHOICES,
                        isMaxSelectableChoicesDisabled ? "2" : Integer.toString(maxSelectableChoices),
                Slots.MSQ_MIN_SELECTABLE_CHOICES,
                        isMinSelectableChoicesDisabled ? "1" : Integer.toString(minSelectableChoices));
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        numOfMsqChoices = 2;
        msqChoices.add("");
        msqChoices.add("");

        return "<div id=\"msqForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(200);
        String optionFragmentTemplate = FormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;

        if (generateOptionsFor != FeedbackParticipantType.NONE) {
            String optionHelpText = String.format(
                    "<br>The options for this question is automatically generated from the list of all %s in this course.",
                    generateOptionsFor.toString().toLowerCase());
            optionListHtml.append(optionHelpText);
        }

        if (numOfMsqChoices > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < numOfMsqChoices; i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(msqChoices.get(i)));

                optionListHtml.append(optionFragment);
            }

            if (otherEnabled) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate, Slots.MSQ_CHOICE_VALUE, "Other");
                optionListHtml.append(optionFragment);
            }

            optionListHtml.append("</ul>");
        }

        String additionalInfo = Templates.populateTemplate(
                FormTemplates.MSQ_ADDITIONAL_INFO,
                Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
                Slots.MSQ_ADDITIONAL_INFO_FRAGMENTS, optionListHtml.toString());

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

        Map<String, Integer> answerFrequency = new LinkedHashMap<>();
        int numChoicesSelected = getNumberOfResponses(responses, answerFrequency);
        if (numChoicesSelected == -1) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.##");

        StringBuilder fragments = new StringBuilder();
        answerFrequency.forEach((key, value) ->
                fragments.append(Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                                Slots.MCQ_CHOICE_VALUE, key,
                                Slots.COUNT, value.toString(),
                                Slots.PERCENTAGE,
                                df.format(100 * divideOrReturnZero(value, numChoicesSelected)))));

        //Use same template as MCQ for now, until they need to be different.
        return Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS, Slots.FRAGMENTS, fragments.toString());
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        Map<String, Integer> answerFrequency = new LinkedHashMap<>();
        int numChoicesSelected = getNumberOfResponses(responses, answerFrequency);
        if (numChoicesSelected == -1) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder fragments = new StringBuilder();
        answerFrequency.forEach((key, value) -> fragments.append(SanitizationHelper.sanitizeForCsv(key) + ','
                + value.toString() + ','
                + df.format(100 * divideOrReturnZero(value, numChoicesSelected))
                + Const.EOL));

        return "Choice, Response Count, Percentage" + Const.EOL
               + fragments + Const.EOL;
    }

    @Override
    public String getCsvHeader() {
        List<String> sanitizedChoices = SanitizationHelper.sanitizeListForCsv(msqChoices);
        return "Feedbacks:," + StringHelper.toString(sanitizedChoices, ",");
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"MSQ\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.MSQ + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE
                && numOfMsqChoices < Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                       + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".");
        }

        //TODO: check that msq options do not repeat. needed?

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;
        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        if (isMaxSelectableChoicesEnabled) {
            if (numOfMsqChoices < maxSelectableChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL);
            } else if (maxSelectableChoices < 2) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MAX_SELECTABLE_CHOICES);
            }
        }

        if (isMinSelectableChoicesEnabled && minSelectableChoices < 1) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MIN_SELECTABLE_CHOICES);
        }

        if (isMaxSelectableChoicesEnabled && isMinSelectableChoicesEnabled
                && minSelectableChoices > maxSelectableChoices) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_SELECTABLE_EXCEEDED_MAX_SELECTABLE);
        }

        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackMsqResponseDetails frd = (FeedbackMsqResponseDetails) response.getResponseDetails();
            if (!otherEnabled) {
                List<String> validChoices = msqChoices;
                validChoices.add("");
                if (!validChoices.containsAll(frd.answers) && generateOptionsFor == FeedbackParticipantType.NONE) {
                    errors.add(frd.getAnswerString() + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
                }
            }

            if (maxSelectableChoices != Integer.MIN_VALUE && frd.answers.size() > maxSelectableChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL
                        + maxSelectableChoices);
            }
        }
        return errors;
    }

    /**
     * Checks if the question has been skipped.
     * MSQ allows a blank response, as that represents "None of the above"
     */
    @Override
    public boolean isQuestionSkipped(String[] answer) {
        return answer == null;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public int getNumOfMsqChoices() {
        return numOfMsqChoices;
    }

    public List<String> getMsqChoices() {
        return msqChoices;
    }

    /**
     * Returns maximum selectable choices for this MSQ question.
     * @return Integer.MIN_VALUE if not set by instructor.
     */
    public int getMaxSelectableChoices() {
        return maxSelectableChoices;
    }

    /**
     * Getting number of responses.
     * @return -1 if there is no empty response else number of response.
     */
    private int getNumberOfResponses(
            List<FeedbackResponseAttributes> responses, Map<String, Integer> answerFrequency) {
        boolean isContainsNonEmptyResponse = false; // we will only show stats if there is at least one nonempty response

        for (String option : msqChoices) {
            answerFrequency.put(option, 0);
        }

        if (otherEnabled) {
            answerFrequency.put("Other", 0);
        }

        int numChoicesSelected = 0;
        for (FeedbackResponseAttributes response : responses) {
            List<String> answerStrings =
                    ((FeedbackMsqResponseDetails) response.getResponseDetails()).getAnswerStrings();
            boolean isOtherOptionAnswer =
                    ((FeedbackMsqResponseDetails) response.getResponseDetails()).isOtherOptionAnswer();
            String otherAnswer = "";

            if (isOtherOptionAnswer) {
                answerFrequency.put("Other", answerFrequency.getOrDefault("Other", 0) + 1);

                numChoicesSelected++;
                // remove other answer temporarily to calculate stats for other options
                otherAnswer = answerStrings.get(answerStrings.size() - 1);
                answerStrings.remove(otherAnswer);
            }

            int numNonEmptyChoicesSelected = getNumberOfNonEmptyResponsesOfQuestion(answerStrings, answerFrequency);
            if (numNonEmptyChoicesSelected > 0) {
                isContainsNonEmptyResponse = true;
                numChoicesSelected += numNonEmptyChoicesSelected;
            }

            // restore other answer if any
            if (isOtherOptionAnswer) {
                answerStrings.add(otherAnswer);
            }
        }

        if (!isContainsNonEmptyResponse) {
            return -1;
        }

        return numChoicesSelected;
    }

    private int getNumberOfNonEmptyResponsesOfQuestion(List<String> answerStrings, Map<String, Integer> answerFrequency) {
        int numChoices = 0;
        for (String answerString : answerStrings) {
            if (answerString.isEmpty()) {
                continue;
            }

            numChoices++;

            answerFrequency.put(answerString, answerFrequency.getOrDefault(answerString, 0) + 1);
        }
        return numChoices;
    }

    private double divideOrReturnZero(double numerator, int denominator) {
        return (denominator == 0) ? 0 : numerator / denominator;
    }

}

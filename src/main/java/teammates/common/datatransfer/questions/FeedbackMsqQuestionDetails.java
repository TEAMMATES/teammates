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
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails.MultipleOptionStatistics;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
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
    private static final Logger log = Logger.getLogger();
    private List<String> msqChoices;
    private boolean otherEnabled;
    private boolean hasAssignedWeights;
    private List<Double> msqWeights;
    private double msqOtherWeight;
    private FeedbackParticipantType generateOptionsFor;
    private int maxSelectableChoices;
    private int minSelectableChoices;
    private StudentAttributes studentDoingQuestion;

    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);

        this.msqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
        this.maxSelectableChoices = Integer.MIN_VALUE;
        this.minSelectableChoices = Integer.MIN_VALUE;
        this.hasAssignedWeights = false;
        this.msqWeights = new ArrayList<>();
        this.msqOtherWeight = 0;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
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
                                                       Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS);
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
                }
            }
            String hasAssignedWeightsString = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED);
            boolean hasAssignedWeights = "on".equals(hasAssignedWeightsString);
            List<Double> msqWeights = getMsqWeights(
                    requestParameters, numMsqChoicesCreated, hasAssignedWeights);
            double msqOtherWeight = getMsqOtherWeight(requestParameters, msqOtherEnabled, hasAssignedWeights);

            setMsqQuestionDetails(msqChoices, msqOtherEnabled, hasAssignedWeights, msqWeights, msqOtherWeight);
        } else {
            setMsqQuestionDetails(FeedbackParticipantType.valueOf(generatedMsqOptions));
        }
        return true;
    }

    private List<Double> getMsqWeights(Map<String, String[]> requestParameters,
            int numMsqChoicesCreated, boolean hasAssignedWeights) {
        List<Double> msqWeights = new ArrayList<>();

        if (!hasAssignedWeights) {
            return msqWeights;
        }

        for (int i = 0; i < numMsqChoicesCreated; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-" + i);
            String weight = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-" + i);

            if (choice != null && !choice.trim().isEmpty() && weight != null) {
                try {
                    // Do not add weight to msqWeights if the weight cannot be parsed
                    msqWeights.add(Double.parseDouble(weight));
                } catch (NumberFormatException e) {
                    log.warning("Failed to parse weight for MSQ question: " + weight);
                }
            }
        }

        return msqWeights;
    }

    private double getMsqOtherWeight(Map<String, String[]> requestParameters,
            boolean msqOtherEnabled, boolean hasAssignedWeights) {
        double msqOtherWeight = 0;

        if (!hasAssignedWeights || !msqOtherEnabled) {
            return msqOtherWeight;
        }

        String otherWeight = HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT);

        Assumption.assertNotNull("Null 'other' weight of MSQ question", otherWeight);
        Assumption.assertNotEmpty("Empty 'other' weight of MSQ question", otherWeight);

        try {
            // Do not assign value to msqOtherWeight if the weight can not be parsed.
            msqOtherWeight = Double.parseDouble(otherWeight);
        } catch (NumberFormatException e) {
            log.warning("Failed to parse \"other\" weight of MSQ question: " + otherWeight);
        }
        return msqOtherWeight;
    }

    private void setMsqQuestionDetails(List<String> msqChoices, boolean otherEnabled,
            boolean hasAssignedWeights, List<Double> msqWeights, double msqOtherWeight) {
        this.msqChoices = msqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
        this.hasAssignedWeights = hasAssignedWeights;
        this.msqWeights = msqWeights;
        this.msqOtherWeight = msqOtherWeight;
    }

    private void setMsqQuestionDetails(FeedbackParticipantType generateOptionsFor) {
        this.msqChoices = new ArrayList<>();
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
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

        if (this.msqChoices.size() != newMsqDetails.msqChoices.size()
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
            int totalNumRecipients, FeedbackResponseDetails existingResponseDetails, StudentAttributes student) {
        studentDoingQuestion = student;
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
            optionListHtml.append(optionFragment).append(System.lineSeparator());
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
            optionListHtml.append(otherOptionFragment).append(System.lineSeparator());
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
            optionListHtml.append(optionFragment).append(System.lineSeparator());
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
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student) {
        studentDoingQuestion = student;
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
            optionListHtml.append(System.lineSeparator());
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
            optionListHtml.append(otherOptionFragment).append(System.lineSeparator());
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
            optionListHtml.append(optionFragment).append(System.lineSeparator());
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

    public int getNumOfChoicesForMsq(String courseId, FeedbackParticipantType generateOptionsFor) {
        if (generateOptionsFor == FeedbackParticipantType.NONE) {
            return msqChoices.size();
        }

        if (generateOptionsFor == FeedbackParticipantType.STUDENTS
                || generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
            List<StudentAttributes> studentList = StudentsLogic.inst().getStudentsForCourse(courseId);
            int sizeOfStudentlist = studentList.size();

            return generateOptionsFor == FeedbackParticipantType.STUDENTS ? sizeOfStudentlist : sizeOfStudentlist - 1;
        }

        if (generateOptionsFor == FeedbackParticipantType.TEAMS
                || generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
            try {
                List<TeamDetailsBundle> teamList = CoursesLogic.inst().getTeamsForCourse(courseId);
                int sizeOfTeamlist = teamList.size();

                return generateOptionsFor == FeedbackParticipantType.TEAMS ? sizeOfTeamlist : sizeOfTeamlist - 1;
            } catch (EntityDoesNotExistException e) {
                Assumption.fail("Course disappeared");
            }
        }
        List<InstructorAttributes> instructorList = InstructorsLogic.inst().getInstructorsForCourse(courseId);

        return instructorList.size();
    }

    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<>();

        switch (generateOptionsFor) {
        case NONE:
            optionList = msqChoices;
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
        DecimalFormat weightFormat = new DecimalFormat("#.##");

        String optionFragmentTemplate = FormTemplates.MSQ_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < msqChoices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(msqChoices.get(i)),
                            Slots.MSQ_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE);

            optionListHtml.append(optionFragment).append(System.lineSeparator());
        }

        // Create MSQ weights
        StringBuilder weightFragmentHtml = new StringBuilder();
        String weightFragmentTemplate = FormTemplates.MSQ_EDIT_FORM_WEIGHTFRAGMENT;
        for (int i = 0; i < msqChoices.size(); i++) {
            String weightFragment =
                    Templates.populateTemplate(weightFragmentTemplate,
                            Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.MSQ_WEIGHT, hasAssignedWeights ? weightFormat.format(msqWeights.get(i)) : "0",
                            Slots.MSQ_PARAM_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT);
            weightFragmentHtml.append(weightFragment).append(System.lineSeparator());
        }

        // Create MSQ other weight value
        String msqOtherWeightValue = hasAssignedWeights && otherEnabled ? weightFormat.format(msqOtherWeight) : "0";

        boolean isMaxSelectableChoicesDisabled = maxSelectableChoices == Integer.MIN_VALUE;
        boolean isMinSelectableChoicesDisabled = minSelectableChoices == Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.MSQ_EDIT_FORM,
                Slots.MSQ_EDIT_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.NUMBER_OF_CHOICE_CREATED, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                Slots.MSQ_NUMBER_OF_CHOICES, Integer.toString(msqChoices.size()),
                Slots.CHECKED_OTHER_OPTION_ENABLED, otherEnabled ? "checked" : "",
                Slots.MSQ_PARAM_OTHER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTION,
                Slots.MSQ_PARAM_OTHER_OPTION_FLAG, Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG,
                Slots.MSQ_CHECKED_GENERATED_OPTIONS, generateOptionsFor == FeedbackParticipantType.NONE ? "" : "checked",
                Slots.MSQ_GENERATED_OPTIONS, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS,
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
                        isMinSelectableChoicesDisabled ? "1" : Integer.toString(minSelectableChoices),
                Slots.MSQ_TOOLTIPS_ASSIGN_WEIGHT, Const.Tooltips.FEEDBACK_QUESTION_MSQ_ASSIGN_WEIGHTS,
                Slots.MSQ_PARAM_HAS_ASSIGN_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED,
                Slots.MSQ_EDIT_FORM_WEIGHT_FRAGMENTS, weightFragmentHtml.toString(),
                Slots.MSQ_PARAM_OTHER_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT,
                Slots.MSQ_OTHER_WEIGHT, msqOtherWeightValue,
                Slots.MSQ_ASSIGN_WEIGHT_CHECKBOX, hasAssignedWeights ? "checked" : "");
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        msqChoices.add("");
        msqChoices.add("");
        hasAssignedWeights = false;

        return "<div id=\"msqForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    // Confusing Ternary flagged for the `else if` condition below.
    // Note: Exclusion to this rule will be added in future PMD patch.
    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(200);
        String optionFragmentTemplate = FormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;

        if (generateOptionsFor != FeedbackParticipantType.NONE) {
            String optionHelpText = String.format(
                    "<br>The options for this question is automatically generated from the list of all %s in this course.",
                    generateOptionsFor.toString().toLowerCase());
            optionListHtml.append(optionHelpText);
        } else if (!msqChoices.isEmpty()) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (String msqChoice : msqChoices) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.MSQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(msqChoice));

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

        MSQStatistics msqStats = new MSQStatistics(this);

        Map<String, Integer> answerFrequency = msqStats.collateAnswerFrequency(responses);
        int numChoicesSelected = getNumberOfResponses(answerFrequency);
        if (numChoicesSelected == -1) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.##");

        StringBuilder fragments = new StringBuilder();

        // Do not calculate weighted percentage if weights are not enabled.
        Map<String, Double> weightedPercentagePerOption =
                hasAssignedWeights ? msqStats.calculateWeightedPercentagePerOption(answerFrequency)
                : new LinkedHashMap<>();

        for (String key : answerFrequency.keySet()) {
            int count = answerFrequency.get(key);
            // If weights are allowed, show the corresponding weights of a choice.
            String weightString = "";
            if ("Other".equals(key)) {
                weightString = hasAssignedWeights ? df.format(msqOtherWeight) : "-";
            } else {
                weightString = hasAssignedWeights ? df.format(msqWeights.get(msqChoices.indexOf(key))) : "-";
            }
            // Reuse Mcq result template until there is any reason to use a separate template.
            fragments.append(Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                    Slots.MCQ_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(key),
                    Slots.MCQ_WEIGHT, weightString,
                    Slots.COUNT, Integer.toString(count),
                    Slots.PERCENTAGE, df.format(100 * divideOrReturnZero(count, numChoicesSelected)),
                    Slots.WEIGHTED_PERCENTAGE,
                            hasAssignedWeights ? df.format(weightedPercentagePerOption.get(key)) : "-"));
        }

        // If weights are assigned, create the per recipient statistics table,
        // otherwise pass an empty string in it's place.
        String recipientStatsHtml = "";
        if (hasAssignedWeights) {
            // Sort responses based on recipient team and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = msqStats.getResponseAttributesSorted(responses, bundle);
            String header = msqStats.getRecipientStatsHeaderHtml();
            String body = msqStats.getPerRecipientStatsBodyHtml(sortedResponses, bundle);

            // Reuse Mcq result template until there is any reason to use a separate template.
            recipientStatsHtml = Templates.populateTemplate(
                    FormTemplates.MCQ_RESULT_RECIPIENT_STATS,
                    Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, header,
                    Slots.TABLE_BODY_HTML, body);
        }

        // Reuse MCQ result templates until there is a reason to use separate templates.
        return Templates.populateTemplate(FormTemplates.MCQ_RESULT_STATS,
                Slots.FRAGMENTS, fragments.toString(),
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

        MSQStatistics msqStats = new MSQStatistics(this);
        Map<String, Integer> answerFrequency = msqStats.collateAnswerFrequency(responses);
        int numChoicesSelected = getNumberOfResponses(answerFrequency);
        if (numChoicesSelected == -1) {
            return "";
        }
        StringBuilder csv = new StringBuilder();

        csv.append(msqStats.getResponseSummaryStatsCsv(answerFrequency, numChoicesSelected));

        // Create 'Per recipient Stats' for csv if weights are enabled.
        if (hasAssignedWeights) {
            String header = msqStats.getPerRecipientResponseStatsHeaderCsv();
            // Get the response attributes sorted based on Recipient Team name and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = msqStats.getResponseAttributesSorted(responses, bundle);
            String body = msqStats.getPerRecipientResponseStatsBodyCsv(sortedResponses, bundle);
            String perRecipientStatsCsv = header + body;

            // Add per recipient stats to csv string
            csv.append(System.lineSeparator())
                .append("Per Recipient Statistics").append(System.lineSeparator())
                .append(perRecipientStatsCsv);
        }
        return csv.toString();
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
    public List<String> validateQuestionDetails(String courseId) {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (msqChoices.size() < Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                           + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If weights are enabled, number of choices and weights should be same.
            // If a user enters an invalid weight for a valid choice,
            // the msqChoices.size() will be greater than msqWeights.size(), in that case
            // trigger this error.
            if (hasAssignedWeights && msqChoices.size() != msqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!msqWeights.isEmpty() || msqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weight is enabled, but other option is disabled, and msqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && msqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are negative, trigger this error.
            if (hasAssignedWeights && !msqWeights.isEmpty()) {
                for (double weight : msqWeights) {
                    if (weight < 0) {
                        errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
                    }
                }
                // If 'Other' option is enabled, and other weight has negative value,
                // trigger this error.
                if (otherEnabled && msqOtherWeight < 0) {
                    errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
                }
            }
        }

        //TODO: check that msq options do not repeat. needed?

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;
        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        if (isMaxSelectableChoicesEnabled) {
            int numOfMsqChoicesForGeneratedOptions = getNumOfChoicesForMsq(courseId, generateOptionsFor);
            if (numOfMsqChoicesForGeneratedOptions < maxSelectableChoices) {
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

    public List<String> getMsqChoices() {
        return msqChoices;
    }

    public List<Double> getMsqWeights() {
        return msqWeights;
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public double getMsqOtherWeight() {
        return msqOtherWeight;
    }

    /**
     * Returns maximum selectable choices for this MSQ question.
     * @return Integer.MIN_VALUE if not set by instructor.
     */
    public int getMaxSelectableChoices() {
        return maxSelectableChoices;
    }

    /**
     * Returns number of non-empty responses.<br>
     * <p>
     * <em>Note:</em> Response can be empty when <b>'None of the above'</b> option is selected.
     * We don't count responses that select 'None of the above' option.</p>
     */
    private int getNumberOfResponses(Map<String, Integer> answerFrequency) {
        int numChoicesSelected = 0;

        for (String choice : answerFrequency.keySet()) {
            numChoicesSelected += answerFrequency.get(choice);
        }

        // we will only show stats if there is at least one nonempty response
        if (numChoicesSelected == 0) {
            return -1;
        }

        return numChoicesSelected;
    }

    private double divideOrReturnZero(double numerator, int denominator) {
        return (denominator == 0) ? 0 : numerator / denominator;
    }

    /**
     * Calculates the Response Statistics for MSQ questions.
     */
    private static class MSQStatistics extends MultipleOptionStatistics {

        MSQStatistics(FeedbackMsqQuestionDetails msqDetails) {
            this.choices = msqDetails.getMsqChoices();
            this.numOfChoices = choices.size();
            this.weights = msqDetails.getMsqWeights();
            this.otherEnabled = msqDetails.getOtherEnabled();
            this.hasAssignedWeights = msqDetails.hasAssignedWeights();
            this.otherWeight = msqDetails.getMsqOtherWeight();
        }

        /**
         * Calculates the answer frequency for each option based on the received responses for a question.
         * <p>
         *   <strong>Note:</strong> Empty answers which denotes the <code>None of the above</code> option are ignored.
         * </p>
         * @param responses The list of response attributes.
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
                FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetails();
                updateResponseCountPerOptionForResponse(responseDetails, answerFrequency);
            }
            return answerFrequency;
        }

        /**
         * Updates the number of responses per option for each response in responseCountPerOption map.
         */
        private void updateResponseCountPerOptionForResponse(FeedbackMsqResponseDetails responseDetails,
                Map<String, Integer> responseCountPerOption) {
            List<String> answerStrings = responseDetails.getAnswerStrings();
            boolean isOtherOptionAnswer = responseDetails.isOtherOptionAnswer();
            String otherAnswer = "";

            if (isOtherOptionAnswer) {
                responseCountPerOption.put("Other", responseCountPerOption.get("Other") + 1);

                // remove other answer temporarily to calculate stats for other options
                otherAnswer = responseDetails.getOtherFieldContent();
                answerStrings.remove(otherAnswer);
            }

            for (String answerString : answerStrings) {
                // Answer string is empty when 'None of the above' option is selected,
                // in that case, don't count that response.
                if (answerString.isEmpty()) {
                    continue;
                }
                responseCountPerOption.put(answerString, responseCountPerOption.getOrDefault(answerString, 0) + 1);
            }

            // restore other answer if any
            if (isOtherOptionAnswer) {
                answerStrings.add(otherAnswer);
            }
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
                    FeedbackMsqResponseDetails frd = (FeedbackMsqResponseDetails) response.getResponseDetails();
                    updateResponseCountPerOptionForResponse(frd, responseCountPerOption);
                    return responseCountPerOption;
                });
            });
            return perRecipientResponse;
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
            // Reuse Mcq result template until there is any reason to use a separate template.
            for (String col : cols) {
                html.append(
                        Templates.populateTemplate(FormTemplates.MCQ_RESULT_RECIPIENT_STATS_BODY_ROW_FRAGMENT,
                        Slots.MCQ_RECIPIENT_STAT_CELL, col));
            }

            return html.toString();
        }

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
                // Reuse Mcq result template until there is any reason to use a separate template.
                bodyBuilder.append(Templates.populateTemplate(FormTemplates.MCQ_RESULT_RECIPIENT_STATS_BODY_FRAGMENT,
                        Slots.MCQ_RECIPIENT_STAT_ROW, statsRow));
            }

            return bodyBuilder.toString();
        }

    }
}

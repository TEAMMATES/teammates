package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestionFormTemplates;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {
    private int numOfMcqChoices;
    private List<String> mcqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;

    public FeedbackMcqQuestionDetails() {
        super(FeedbackQuestionType.MCQ);
        
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    public int getNumOfMcqChoices() {
        return numOfMcqChoices;
    }

    public List<String> getMcqChoices() {
        return mcqChoices;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        
        int numOfMcqChoices = 0;
        List<String> mcqChoices = new LinkedList<String>();
        boolean mcqOtherEnabled = false; // TODO change this when implementing "other, please specify" field
        
        if ("on".equals(HttpRequestHelper.getValueFromParamMap(
                                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG))) {
            mcqOtherEnabled = true;
        }
        
        String generatedMcqOptions =
                HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS);
        
        if (generatedMcqOptions.equals(FeedbackParticipantType.NONE.toString())) {
            String numMcqChoicesCreatedString = HttpRequestHelper.getValueFromParamMap(
                                                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for MCQ", numMcqChoicesCreatedString);
            int numMcqChoicesCreated = Integer.parseInt(numMcqChoicesCreatedString);
            
            for (int i = 0; i < numMcqChoicesCreated; i++) {
                String mcqChoice = HttpRequestHelper.getValueFromParamMap(
                                                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i);
                if (mcqChoice != null && !mcqChoice.trim().isEmpty()) {
                    mcqChoices.add(mcqChoice);
                    numOfMcqChoices++;
                }
            }
            
            setMcqQuestionDetails(numOfMcqChoices, mcqChoices, mcqOtherEnabled);
        } else {
            setMcqQuestionDetails(FeedbackParticipantType.valueOf(generatedMcqOptions));
        }
        return true;
    }

    private void setMcqQuestionDetails(int numOfMcqChoices,
            List<String> mcqChoices,
            boolean otherEnabled) {
        this.numOfMcqChoices = numOfMcqChoices;
        this.mcqChoices = mcqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }
    
    private void setMcqQuestionDetails(FeedbackParticipantType generateOptionsFor) {
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        Assumption.assertTrue("Can only generate students, teams or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS
                || generateOptionsFor == FeedbackParticipantType.TEAMS
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
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
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
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        FeedbackMcqResponseDetails existingMcqResponse = (FeedbackMcqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        Boolean isOtherSelected = existingMcqResponse.isOtherOptionAnswer();
        
        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${checked}", existingMcqResponse.getAnswerString().equals(choices.get(i)) ? "checked" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        if (otherEnabled) {
            String otherOptionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                    Templates.populateTemplate(otherOptionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${text-disabled}", sessionIsOpen && isOtherSelected ? "" : "disabled",
                            "${checked}", isOtherSelected ? "checked" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER,
                            "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(existingMcqResponse.getOtherFieldContent()),
                            "${mcqOtherOptionAnswer}", isOtherSelected ? "1" : "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
                "${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        if (otherEnabled) {
            String otherOptionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                       Templates.populateTemplate(otherOptionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${text-disabled}", "disabled",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER,
                            "${mcqChoiceValue}", "",
                            "${mcqOtherOptionAnswer}", "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }
        
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
                "${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<String>();

        switch (generateOptionsFor) {
        case NONE:
            optionList = mcqChoices;
            break;
        case STUDENTS:
            List<StudentAttributes> studentList = StudentsLogic.inst().getStudentsForCourse(courseId);

            for (StudentAttributes student : studentList) {
                optionList.add(student.name + " (" + student.team + ")");
            }
            
            Collections.sort(optionList);
            break;
        case TEAMS:
            try {
                List<TeamDetailsBundle> teamList = CoursesLogic.inst().getTeamsForCourse(courseId);
                
                for (TeamDetailsBundle team : teamList) {
                    optionList.add(team.name);
                }
                
                Collections.sort(optionList);
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

            Collections.sort(optionList);
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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_EDIT_FORM_OPTIONFRAGMENT;
        
        for (int i = 0; i < numOfMcqChoices; i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(mcqChoices.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_EDIT_FORM,
                "${mcqEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}",
                        Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfMcqChoices}", Integer.toString(numOfMcqChoices),
                "${checkedOtherOptionEnabled}", otherEnabled ? "checked" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG}",
                        Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG,
                "${checkedGeneratedOptions}", (generateOptionsFor == FeedbackParticipantType.NONE) ? "" : "checked",
                "${Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS}", Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                "${generateOptionsForValue}", generateOptionsFor.toString(),
                "${studentSelected}", generateOptionsFor == FeedbackParticipantType.STUDENTS ? "selected" : "",
                "${FeedbackParticipantType.STUDENTS.toString()}", FeedbackParticipantType.STUDENTS.toString(),
                "${teamSelected}", generateOptionsFor == FeedbackParticipantType.TEAMS ? "selected" : "",
                "${FeedbackParticipantType.TEAMS.toString()}", FeedbackParticipantType.TEAMS.toString(),
                "${instructorSelected}", generateOptionsFor == FeedbackParticipantType.INSTRUCTORS ? "selected" : "",
                "${FeedbackParticipantType.INSTRUCTORS.toString()}", FeedbackParticipantType.INSTRUCTORS.toString());
        
        return html;
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        numOfMcqChoices = 2;
        mcqChoices.add("");
        mcqChoices.add("");
        
        return "<div id=\"mcqForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(200);
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_ADDITIONAL_INFO_FRAGMENT;
        
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
                                "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(mcqChoices.get(i)));

                optionListHtml.append(optionFragment);
            }
        }
        if (otherEnabled) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate, "${mcqChoiceValue}", "Others");
            optionListHtml.append(optionFragment);
        }
        optionListHtml.append("</ul>");
        
        String additionalInfo = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_ADDITIONAL_INFO,
                "${questionTypeName}", getQuestionTypeDisplayName(),
                "${mcqAdditionalInfoFragments}", optionListHtml.toString());
        
        String html = Templates.populateTemplate(
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
        
        if ("student".equals(view) || responses.isEmpty()) {
            return "";
        }
        
        StringBuilder fragments = new StringBuilder();
        Map<String, Integer> answerFrequency = new LinkedHashMap<String, Integer>();
        
        for (String option : mcqChoices) {
            answerFrequency.put(option, 0);
        }
        
        if (otherEnabled) {
            answerFrequency.put("Other", 0);
        }
        
        for (FeedbackResponseAttributes response : responses) {
            String answerString = response.getResponseDetails().getAnswerString();
            Boolean isOtherOptionAnswer = ((FeedbackMcqResponseDetails) (response.getResponseDetails())).isOtherOptionAnswer();
            
            if (isOtherOptionAnswer) {
                if (!answerFrequency.containsKey("Other")) {
                    answerFrequency.put("Other", 0);
                }
                answerFrequency.put("Other", answerFrequency.get("Other") + 1);
            } else {
                if (!answerFrequency.containsKey(answerString)) {
                    answerFrequency.put(answerString, 0);
                }
                answerFrequency.put(answerString, answerFrequency.get(answerString) + 1);
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, Integer> entry : answerFrequency.entrySet()) {
            fragments.append(Templates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                                "${mcqChoiceValue}", Sanitizer.sanitizeForHtml(entry.getKey()),
                                "${count}", entry.getValue().toString(),
                                "${percentage}", df.format(100 * (double) entry.getValue() / responses.size())));
        }
        
        return Templates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS,
                                                              "${fragments}", fragments.toString());
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }
        
        StringBuilder fragments = new StringBuilder();
        Map<String, Integer> answerFrequency = new LinkedHashMap<String, Integer>();
        
        for (String option : mcqChoices) {
            answerFrequency.put(option, 0);
        }
        
        if (otherEnabled) {
            answerFrequency.put("Other", 0);
        }
        
        for (FeedbackResponseAttributes response : responses) {
            String answerString = response.getResponseDetails().getAnswerString();
            Boolean isOtherOptionAnswer = ((FeedbackMcqResponseDetails) (response.getResponseDetails())).isOtherOptionAnswer();
            
            if (isOtherOptionAnswer) {
                if (!answerFrequency.containsKey("Other")) {
                    answerFrequency.put("Other", 0);
                }
                answerFrequency.put("Other", answerFrequency.get("Other") + 1);
            } else {
                if (!answerFrequency.containsKey(answerString)) {
                    answerFrequency.put(answerString, 0);
                }
                answerFrequency.put(answerString, answerFrequency.get(answerString) + 1);
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, Integer> entry : answerFrequency.entrySet()) {
            fragments.append(Sanitizer.sanitizeForCsv(entry.getKey())).append(',')
                     .append(entry.getValue().toString()).append(',')
                     .append(df.format(100 * (double) entry.getValue() / responses.size())).append(Const.EOL);
        }
        
        return "Choice, Response Count, Percentage" + Const.EOL
               + fragments.toString();
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"MCQ\"><a> " + Const.FeedbackQuestionTypeNames.MCQ + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if (generateOptionsFor == FeedbackParticipantType.NONE
                && numOfMcqChoices < Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".");
        }
        //TODO: check that mcq options do not repeat. needed?
        
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        
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
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }
}

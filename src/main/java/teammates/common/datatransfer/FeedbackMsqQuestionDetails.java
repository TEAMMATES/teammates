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
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestionFormTemplates;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackMsqQuestionDetails extends FeedbackQuestionDetails {
    private int numOfMsqChoices;
    private List<String> msqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;
    
    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);
        
        this.numOfMsqChoices = 0;
        this.msqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        int numOfMsqChoices = 0;
        List<String> msqChoices = new LinkedList<String>();
        boolean msqOtherEnabled = false;
        
        String otherOptionFlag =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG);
        
        if ("on".equals(otherOptionFlag)) {
            msqOtherEnabled = true;
        }
            
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
            setMsqQuestionDetails(FeedbackParticipantType.valueOf(generatedMsqOptions));
        }
        return true;
    }

    private void setMsqQuestionDetails(int numOfMsqChoices,
            List<String> msqChoices,
            boolean otherEnabled) {
        
        this.numOfMsqChoices = numOfMsqChoices;
        this.msqChoices = msqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }
    
    private void setMsqQuestionDetails(FeedbackParticipantType generateOptionsFor) {
        
        this.numOfMsqChoices = 0;
        this.msqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        Assumption.assertTrue("Can only generate students, teams or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS
                || generateOptionsFor == FeedbackParticipantType.TEAMS
                || generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MSQ;
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
        
        return this.otherEnabled != newMsqDetails.otherEnabled;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        FeedbackMsqResponseDetails existingMsqResponse = (FeedbackMsqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        Boolean isOtherSelected = existingMsqResponse.isOtherOptionAnswer();
        
        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${checked}", existingMsqResponse.contains(choices.get(i)) ? "checked" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${msqChoiceValue}", Sanitizer.sanitizeForHtml(choices.get(i)),
                            "${msqChoiceText}", Sanitizer.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        if (otherEnabled) {
            String otherOptionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                    Templates.populateTemplate(otherOptionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${text-disabled}", sessionIsOpen && isOtherSelected ? "" : "disabled",
                            "${checked}", isOtherSelected ? "checked" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER,
                            "${msqChoiceValue}", Sanitizer.sanitizeForHtml(existingMsqResponse.getOtherFieldContent()),
                            "${msqOtherOptionAnswer}", isOtherSelected ? "1" : "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }
        
        // additional checkbox for user to submit a blank response ("None of the above")
        String optionFragment =
                Templates.populateTemplate(optionFragmentTemplate,
                        "${qnIdx}", Integer.toString(qnIdx),
                        "${responseIdx}", Integer.toString(responseIdx),
                        "${disabled}", sessionIsOpen ? "" : "disabled",
                        "${checked}", existingMsqResponse.contains("") ? "checked" : "",
                        "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                        "${msqChoiceValue}", "",
                        "${msqChoiceText}", "<i>" + Const.NONE_OF_THE_ABOVE + "</i>");
        optionListHtml.append(optionFragment).append(Const.EOL);
        
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
                "${msqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        List<String> choices = generateOptionList(courseId);
                
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < choices.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${msqChoiceValue}", Sanitizer.sanitizeForHtml(choices.get(i)),
                            "${msqChoiceText}", Sanitizer.sanitizeForHtml(choices.get(i)));
            optionListHtml.append(optionFragment);
            optionListHtml.append(Const.EOL);
        }
        
        if (otherEnabled) {
            String otherOptionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT;
            String otherOptionFragment =
                       Templates.populateTemplate(otherOptionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${text-disabled}", "disabled",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER,
                            "${msqChoiceValue}", "",
                            "${msqOtherOptionAnswer}", "0");
            optionListHtml.append(otherOptionFragment).append(Const.EOL);
        }
        
        // additional checkbox for user to submit a blank response ("None of the above")
        String optionFragment =
                Templates.populateTemplate(optionFragmentTemplate,
                        "${qnIdx}", Integer.toString(qnIdx),
                        "${responseIdx}", Integer.toString(responseIdx),
                        "${disabled}", sessionIsOpen ? "" : "disabled",
                        "${checked}", "",
                        "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                        "${msqChoiceValue}", "",
                        "${msqChoiceText}", "<i>" + Const.NONE_OF_THE_ABOVE + "</i>");
        optionListHtml.append(optionFragment).append(Const.EOL);

        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
                "${msqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }
    
    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<String>();

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
            
            Collections.sort(optionList);
            break;
        case TEAMS:
            try {
                List<TeamDetailsBundle> teamList =
                        CoursesLogic.inst().getTeamsForCourse(courseId);
                
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
                    InstructorsLogic.inst().getInstructorsForCourse(
                            courseId);

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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < numOfMsqChoices; i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${msqChoiceValue}", Sanitizer.sanitizeForHtml(msqChoices.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_EDIT_FORM,
                "${msqEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}",
                        Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfMsqChoices}", Integer.toString(numOfMsqChoices),
                "${checkedOtherOptionEnabled}", otherEnabled ? "checked" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG}",
                        Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG,
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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        
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
                                "${msqChoiceValue}", Sanitizer.sanitizeForHtml(msqChoices.get(i)));
                
                optionListHtml.append(optionFragment);
            }
            
            if (otherEnabled) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate, "${msqChoiceValue}", "Other");
                optionListHtml.append(optionFragment);
            }
            
            optionListHtml.append("</ul>");
        }
        
        String additionalInfo = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString());
        
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
        
        boolean isContainsNonEmptyResponse = false; // we will only show stats if there is at least one nonempty response

        Map<String, Integer> answerFrequency = new LinkedHashMap<String, Integer>();
        
        for (String option : msqChoices) {
            answerFrequency.put(option, 0);
        }
        
        if (otherEnabled) {
            answerFrequency.put("Other", 0);
        }
        
        int numChoicesSelected = 0;
        for (FeedbackResponseAttributes response : responses) {
            List<String> answerStrings = ((FeedbackMsqResponseDetails) response.getResponseDetails()).getAnswerStrings();
            Boolean isOtherOptionAnswer = ((FeedbackMsqResponseDetails) (response.getResponseDetails())).isOtherOptionAnswer();
            String otherAnswer = "";
            
            if (isOtherOptionAnswer) {
                if (!answerFrequency.containsKey("Other")) {
                    answerFrequency.put("Other", 0);
                }
                
                answerFrequency.put("Other", answerFrequency.get("Other") + 1);
                
                numChoicesSelected++;
                // remove other answer temporarily to calculate stats for other options
                otherAnswer = answerStrings.get(answerStrings.size() - 1);
                answerStrings.remove(otherAnswer);
            }
            
            for (String answerString : answerStrings) {
                if (answerString.isEmpty()) {
                    continue;
                }
                
                isContainsNonEmptyResponse = true;
                numChoicesSelected++;
                
                if (!answerFrequency.containsKey(answerString)) {
                    answerFrequency.put(answerString, 0);
                }
                answerFrequency.put(answerString, answerFrequency.get(answerString) + 1);
            }
            
            // restore other answer if any
            if (isOtherOptionAnswer) {
                answerStrings.add(otherAnswer);
            }
        }
        
        if (!isContainsNonEmptyResponse) {
            return "";
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        StringBuilder fragments = new StringBuilder();
        for (Entry<String, Integer> entry : answerFrequency.entrySet()) {
            fragments.append(Templates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                                "${mcqChoiceValue}", entry.getKey(),
                                "${count}", entry.getValue().toString(),
                                "${percentage}", df.format(100 * (double) entry.getValue() / numChoicesSelected)));

        }
        //Use same template as MCQ for now, until they need to be different.
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

        Map<String, Integer> answerFrequency = new LinkedHashMap<String, Integer>();
        boolean isContainsNonEmptyResponse = false; // we will only show stats if there is at least one nonempty response
        
        for (String option : msqChoices) {
            answerFrequency.put(option, 0);
        }
        
        if (otherEnabled) {
            answerFrequency.put("Other", 0);
        }
        
        int numChoicesSelected = 0;
        
        for (FeedbackResponseAttributes response : responses) {
            List<String> answerStrings = ((FeedbackMsqResponseDetails) response.getResponseDetails()).getAnswerStrings();
            Boolean isOtherOptionAnswer = ((FeedbackMsqResponseDetails) (response.getResponseDetails())).isOtherOptionAnswer();
            String otherAnswer = "";
            
            if (isOtherOptionAnswer) {
                if (!answerFrequency.containsKey("Other")) {
                    answerFrequency.put("Other", 0);
                }
                answerFrequency.put("Other", answerFrequency.get("Other") + 1);
                
                numChoicesSelected++;
                // remove other answer temporarily to calculate stats for other options
                otherAnswer = answerStrings.get(answerStrings.size() - 1);
                answerStrings.remove(otherAnswer);
            }
            
            for (String answerString : answerStrings) {
                if (answerString.isEmpty()) {
                    continue;
                }
                isContainsNonEmptyResponse = true;
                numChoicesSelected++;
                if (!answerFrequency.containsKey(answerString)) {
                    answerFrequency.put(answerString, 0);
                }
                answerFrequency.put(answerString, answerFrequency.get(answerString) + 1);
                
            }
            
            // restore other answer if any
            if (isOtherOptionAnswer) {
                answerStrings.add(otherAnswer);
            }
        }
        
        if (!isContainsNonEmptyResponse) {
            return "";
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder fragments = new StringBuilder();
        for (Entry<String, Integer> entry : answerFrequency.entrySet()) {
            fragments.append(Sanitizer.sanitizeForCsv(entry.getKey()) + ','
                             + entry.getValue().toString() + ','
                             + df.format(100 * (double) entry.getValue() / numChoicesSelected) + Const.EOL);
        }

        
        return "Choice, Response Count, Percentage" + Const.EOL
               + fragments + Const.EOL;
    }

    @Override
    public String getCsvHeader() {
        List<String> sanitizedChoices = Sanitizer.sanitizeListForCsv(msqChoices);
        return "Feedbacks:," + StringHelper.toString(sanitizedChoices, ",");
    }
    
    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"MSQ\"><a>" + Const.FeedbackQuestionTypeNames.MSQ + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if (generateOptionsFor == FeedbackParticipantType.NONE
                && numOfMsqChoices < Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".");
        }
        //TODO: check that msq options do not repeat. needed?
        
        return errors;
    }
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackMsqResponseDetails frd = (FeedbackMsqResponseDetails) response.getResponseDetails();
            if (!otherEnabled) {
                List<String> validChoices = msqChoices;
                validChoices.add("");
                if (!validChoices.containsAll(frd.answers) && generateOptionsFor == FeedbackParticipantType.NONE) {
                    errors.add(frd.getAnswerString() + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION);
                }
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

}

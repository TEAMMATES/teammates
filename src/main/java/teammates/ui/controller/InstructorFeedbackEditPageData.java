package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackQuestionFeedbackPathSettings;
import teammates.ui.template.FeedbackQuestionTableRow;
import teammates.ui.template.FeedbackQuestionVisibilitySettings;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageData extends PageData {
    
    private FeedbackSessionsForm fsForm;
    private List<FeedbackQuestionEditForm> qnForms;
    private FeedbackQuestionEditForm newQnForm;
    private FeedbackSessionPreviewForm previewForm;
    private FeedbackQuestionCopyTable copyQnForm;
    private String statusForAjax;
    private boolean hasError;
    
    public InstructorFeedbackEditPageData(AccountAttributes account) {
        super(account);
        
    }
    

    public void init(FeedbackSessionAttributes feedbackSession, List<FeedbackQuestionAttributes> questions,
                     List<FeedbackQuestionAttributes> copiableQuestions, 
                     Map<String, Boolean> questionHasResponses,
                     List<StudentAttributes> studentList, List<InstructorAttributes> instructorList,
                     InstructorAttributes instructor) {
        Assumption.assertNotNull(feedbackSession);
        
        buildFsForm(feedbackSession);
        
        qnForms = new ArrayList<FeedbackQuestionEditForm>();
        for (FeedbackQuestionAttributes question : questions) {
            buildExistingQuestionForm(feedbackSession.feedbackSessionName, 
                                      questions.size(), questionHasResponses, 
                                      instructor.courseId, question);
        }
        
        buildNewQuestionForm(feedbackSession, questions.size() + 1);
        
        buildPreviewForm(feedbackSession, studentList, instructorList);
        
        buildCopyQnForm(feedbackSession, copiableQuestions, instructor);
    }
    
    private void buildPreviewForm(FeedbackSessionAttributes feedbackSession,
                                    List<StudentAttributes> studentList,
                                    List<InstructorAttributes> instructorList) {
        previewForm = new FeedbackSessionPreviewForm(feedbackSession.courseId, feedbackSession.feedbackSessionName, 
                                                     getPreviewAsStudentOptions(studentList), 
                                                     getPreviewAsInstructorOptions(instructorList));
    }


    private void buildFsForm(FeedbackSessionAttributes feedbackSession) {
        buildBasicFsForm(feedbackSession, buildFsFormAdditionalSettings(feedbackSession));
    }
    
    private void buildBasicFsForm(FeedbackSessionAttributes fsa, FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        String fsDeleteLink = getInstructorFeedbackDeleteLink(fsa.courseId, fsa.feedbackSessionName, "");
        String copyToLink = getInstructorFeedbackEditCopyLink();
        
        fsForm = FeedbackSessionsForm.getFsFormForExistingFs(fsa, additionalSettings, 
                                                             fsDeleteLink, copyToLink);
    }
    
    private FeedbackSessionsAdditionalSettingsFormSegment buildFsFormAdditionalSettings(FeedbackSessionAttributes newFeedbackSession) {
        return FeedbackSessionsAdditionalSettingsFormSegment.getFormSegmentWithExistingValues(this, newFeedbackSession); 
    }   


    private void buildCopyQnForm(FeedbackSessionAttributes feedbackSession,
                                    List<FeedbackQuestionAttributes> copiableQuestions,
                                    InstructorAttributes instructor) {
        List<FeedbackQuestionTableRow> copyQuestionRows = buildCopyQuestionsModalRows(copiableQuestions,
                                                                                      instructor);
        copyQnForm = new FeedbackQuestionCopyTable(feedbackSession.courseId, feedbackSession.feedbackSessionName, 
                                                   copyQuestionRows);
    }


    private List<FeedbackQuestionTableRow> buildCopyQuestionsModalRows(List<FeedbackQuestionAttributes> copiableQuestions,
                                                                       InstructorAttributes instructor) {
        List<FeedbackQuestionTableRow> copyQuestionRows = new ArrayList<FeedbackQuestionTableRow>();
        if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
            for (FeedbackQuestionAttributes question : copiableQuestions) {
                String courseId = question.courseId;
                String fsName = question.feedbackSessionName;
                String qnType = question.getQuestionDetails().getQuestionTypeDisplayName();
                String qnText = question.getQuestionDetails().questionText;
                String qnId = question.getId();
                
                FeedbackQuestionTableRow row = new FeedbackQuestionTableRow(courseId, fsName, qnType, qnText, qnId);
                copyQuestionRows.add(row);
            }
        }
        return copyQuestionRows;
    }

    private void buildExistingQuestionForm(String feedbackSessionName,
                                           int questionsSize,
                                           Map<String, Boolean> questionHasResponses,
                                           String courseId, FeedbackQuestionAttributes question) {
        FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
        qnForm.setAction(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT);
        qnForm.setCourseId(courseId);
        
        qnForm.setFeedbackSessionName(feedbackSessionName);
        qnForm.setQuestion(question);
        qnForm.setQuestionNumberSuffix("-" + question.questionNumber);
        
        qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questionsSize));
        
        FeedbackQuestionFeedbackPathSettings feedbackPathSettings = new FeedbackQuestionFeedbackPathSettings();
        feedbackPathSettings.setGiverParticipantOptions(getParticipantOptions(question, true));
        feedbackPathSettings.setRecipientParticipantOptions(getParticipantOptions(question, false));
        
        boolean isNumberOfEntitiesToGiveFeedbackToChecked = question.numberOfEntitiesToGiveFeedbackTo != Const.MAX_POSSIBLE_RECIPIENTS;
        feedbackPathSettings.setNumberOfEntitiesToGiveFeedbackToChecked(isNumberOfEntitiesToGiveFeedbackToChecked);
        feedbackPathSettings.setNumOfEntitiesToGiveFeedbackToValue(isNumberOfEntitiesToGiveFeedbackToChecked
                                                                   ?  question.numberOfEntitiesToGiveFeedbackTo
                                                                   :  1);
        qnForm.setFeedbackPathSettings(feedbackPathSettings);
        
        
        // maps for setting visibility
        Map<String, Boolean> isGiverNameVisibleFor = new HashMap<String, Boolean>();
        for (FeedbackParticipantType giverType : question.showGiverNameTo) {
            isGiverNameVisibleFor.put(giverType.name(), true);
        }
        
        Map<String, Boolean> isRecipientNameVisibleFor = new HashMap<String, Boolean>();
        for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
            isRecipientNameVisibleFor.put(recipientType.name(), true);
        }
        
        Map<String, Boolean> isResponsesVisibleFor = new HashMap<String, Boolean>();
        for (FeedbackParticipantType participantType : question.showResponsesTo) {
            isResponsesVisibleFor.put(participantType.name(), true);
        }
        FeedbackQuestionVisibilitySettings visibilitySettings = new FeedbackQuestionVisibilitySettings(
                                                                        question.getVisibilityMessage(),
                                                                        isResponsesVisibleFor,
                                                                        isGiverNameVisibleFor, 
                                                                        isRecipientNameVisibleFor);
        qnForm.setVisibilitySettings(visibilitySettings);
        
        qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));
        
        qnForm.setQuestionSpecificEditFormHtml(question.getQuestionDetails().getQuestionSpecificEditFormHtml(question.questionNumber));
        qnForm.setEditable(false);
        
        qnForms.add(qnForm);
    }


    private void buildNewQuestionForm(FeedbackSessionAttributes feedbackSession, int nextQnNum) {
      
        Url doneEditingLink = new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                .withUserId(account.googleId)
                                .withCourseId(feedbackSession.courseId)
                                .withSessionName(feedbackSession.feedbackSessionName);
        newQnForm = FeedbackQuestionEditForm.getNewQnForm(doneEditingLink.toString(), feedbackSession,
                                                          getQuestionTypeChoiceOptions(), getParticipantOptions(null, true),
                                                          getParticipantOptions(null, false), getQuestionNumberOptions(nextQnNum), 
                                                          getNewQuestionSpecificEditFormHtml());
    }

    /**
     * Returns a list of HTML options for selecting participant type.
     * Used in instructorFeedbackEdit.jsp for selecting the participant type for a new question.
     * isGiver refers to the feedback path (!isGiver == feedback's recipient)
     */
    private List<ElementTag> getParticipantOptions(FeedbackQuestionAttributes question, boolean isGiver) {
        List<ElementTag> result = new ArrayList<ElementTag>();
        for (FeedbackParticipantType option : FeedbackParticipantType.values()) {
            
            boolean isValidGiver = isGiver && option.isValidGiver();
            boolean isValidRecipient = !isGiver && option.isValidRecipient();
            
            if (isValidGiver || isValidRecipient) {
                String participantName = isValidGiver ? option.toDisplayGiverName()
                                                      : option.toDisplayRecipientName();
                
                boolean isSelected = false;
                // for existing questions
                if (question != null) {
                    boolean isGiverType = isValidGiver && question.giverType == option;
                    boolean isRecipientType = isValidRecipient && question.recipientType == option;
                    
                    isSelected = (isGiverType || isRecipientType); 
                }
                
                ElementTag optionTag = createOption(participantName, option.toString(), isSelected);
                result.add(optionTag);
            }
        }
        return result;
    }

    
    private List<ElementTag> getQuestionNumberOptions(int numQuestions) {
        List<ElementTag> options = new ArrayList<ElementTag>();
        
        for (int opt = 1; opt < numQuestions + 1; opt++) {
            ElementTag option = createOption(String.valueOf(opt), String.valueOf(opt), false);
            options.add(option);
        }
        
        return options;
    }
    
    /**
     * Returns String of HTML containing a list of options for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    public String getQuestionTypeChoiceOptions() {
        StringBuilder options = new StringBuilder();
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            options.append(type.getFeedbackQuestionDetailsInstance().getQuestionTypeChoiceOption());
        }
        return options.toString();
    }
    
    /**
     * Get all question specific edit forms
     * Used in instructorFeedbackEdit.jsp for new question
     * @return
     */
    public String getNewQuestionSpecificEditFormHtml() {
        String newQuestionSpecificEditForms = "";
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            newQuestionSpecificEditForms +=
                    type.getFeedbackQuestionDetailsInstance().getNewQuestionSpecificEditFormHtml();
        }
        return newQuestionSpecificEditForms;
    }

    private List<ElementTag> getPreviewAsInstructorOptions(List<InstructorAttributes> instructorList) {
        List<ElementTag> results = new ArrayList<ElementTag>();
        
        for (InstructorAttributes instructor : instructorList) {
            ElementTag option = createOption(instructor.name, instructor.email);
            results.add(option);
        }
        
        return results;
    }
    
    private List<ElementTag> getPreviewAsStudentOptions(List<StudentAttributes> studentList) {
        List<ElementTag> results = new ArrayList<ElementTag>();
        
        for (StudentAttributes student : studentList) {
            ElementTag option = createOption("[" + student.team + "] " + student.name, student.email);
            results.add(option);
        }
        
        return results;
    }


    public FeedbackSessionsForm getFsForm() {
        return fsForm;
    }
    
    public List<FeedbackQuestionEditForm> getQnForms() {
        return qnForms;
    }
    
    public FeedbackQuestionEditForm getNewQnForm() {
        return newQnForm;
    }
    
    public FeedbackSessionPreviewForm getPreviewForm() {
        return previewForm;
    }
    
    public FeedbackQuestionCopyTable getCopyQnForm() {
        return copyQnForm;
    }

    public String getStatusForAjax() {
        return statusForAjax;
    }

    public void setStatusForAjax(String statusForAjax) {
        this.statusForAjax = statusForAjax;
    }

    public boolean getHasError() {
        return hasError;
    }

    public void setHasError(boolean value) {
        this.hasError = value;
    }
}

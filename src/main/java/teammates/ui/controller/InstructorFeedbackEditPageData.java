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
import teammates.ui.template.AdditionalSettingsFormSegment;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackQuestionFeedbackPathSettings;
import teammates.ui.template.FeedbackQuestionTableRow;
import teammates.ui.template.FeedbackQuestionVisibilitySettings;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageData extends PageData {
    
    private String emptyFsMsg = Const.StatusMessages.FEEDBACK_QUESTION_EMPTY; 
    private FeedbackSessionsForm fsForm;
    private List<FeedbackQuestionEditForm> qnForms;
    private FeedbackQuestionEditForm newQnForm;
    private FeedbackSessionPreviewForm previewForm;
    private FeedbackQuestionCopyTable copyQnForm;
    
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
        
        buildNewQuestionForm(feedbackSession, questions);
        
        buildPreviewForm(feedbackSession, studentList, instructorList);
        
        buildCopyQnForm(feedbackSession, copiableQuestions, instructor);
    }


    private void buildFsForm(FeedbackSessionAttributes feedbackSession) {
        buildBasicFsForm(feedbackSession, buildFsFormAdditionalSettings(feedbackSession));
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


    private void buildPreviewForm(FeedbackSessionAttributes feedbackSession,
                                    List<StudentAttributes> studentList,
                                    List<InstructorAttributes> instructorList) {
        previewForm = new FeedbackSessionPreviewForm(feedbackSession.courseId, feedbackSession.feedbackSessionName, 
                                                     getPreviewAsStudentOptions(studentList), 
                                                     getPreviewAsInstructorOptions(instructorList));
    }


    private void buildExistingQuestionForm(String feedbackSessionName,
                                           int questionsSize,
                                           Map<String, Boolean> questionHasResponses,
                                           String courseId, FeedbackQuestionAttributes question) {
        FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
        qnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT));
        qnForm.setCourseId(courseId);
        
        qnForm.setFeedbackSessionName(feedbackSessionName);
        qnForm.setQuestion(question);
        qnForm.setQuestionNumberSuffix("-" + question.questionNumber);
        
        qnForm.setNumOfQuestionsOnPage(questionsSize);
        qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questionsSize));
        qnForm.setQuestionText(question.getQuestionDetails().questionText);
        
        FeedbackQuestionFeedbackPathSettings feedbackPathSettings = new FeedbackQuestionFeedbackPathSettings();
        FeedbackQuestionVisibilitySettings visibilitySettings = new FeedbackQuestionVisibilitySettings();
        qnForm.setFeedbackPathSettings(feedbackPathSettings);
        qnForm.setVisibilitySettings(visibilitySettings);
        feedbackPathSettings.setGiverParticipantOptions(getParticipantOptions(question, true));
        feedbackPathSettings.setRecipientParticipantOptions(getParticipantOptions(question, false));
        
        
        // maps for setting visibility
        Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType giverType : question.showGiverNameTo) {
            isGiverNameVisible.put(giverType.name(), true);
        }
        visibilitySettings.setGiverNameVisibleFor(isGiverNameVisible);
        
        Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
            isRecipientNameVisible.put(recipientType.name(), true);
        }
        visibilitySettings.setRecipientNameVisibleFor(isRecipientNameVisible);
        
        Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType participantType : question.showResponsesTo) {
            isResponsesVisible.put(participantType.name(), true);
        }
        visibilitySettings.setResponseVisibleFor(isResponsesVisible);
        
        feedbackPathSettings.setNumberOfEntitiesToGiveFeedbackToChecked(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS);
        feedbackPathSettings.setNumOfEntitiesToGiveFeedbackToValue(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ?
                                                                     1 :
                                                                     question.numberOfEntitiesToGiveFeedbackTo);
        qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));
        
        visibilitySettings.setVisibilityMessages(question.getVisibilityMessage());
        
        qnForm.setQuestionSpecificEditFormHtml(question.getQuestionDetails().getQuestionSpecificEditFormHtml(question.questionNumber));
        qnForm.setEditable(false);
        
        qnForms.add(qnForm);
    }


    private void buildNewQuestionForm(FeedbackSessionAttributes feedbackSession,
                                      List<FeedbackQuestionAttributes> questions) {
        newQnForm = new FeedbackQuestionEditForm();
        newQnForm.setDoneEditingLink(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                        .withUserId(account.googleId)
                                        .withCourseId(feedbackSession.courseId)
                                        .withSessionName(feedbackSession.feedbackSessionName));
        
        newQnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD));
        newQnForm.setCourseId(feedbackSession.courseId);
        newQnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
        newQnForm.setQuestionNumberSuffix("");
        
        newQnForm.setQuestionTypeOptions(getQuestionTypeChoiceOptions());
      
        FeedbackQuestionFeedbackPathSettings feedbackPathSettings = new FeedbackQuestionFeedbackPathSettings();
        FeedbackQuestionVisibilitySettings visibilitySettings = new FeedbackQuestionVisibilitySettings();
        
        newQnForm.setFeedbackPathSettings(feedbackPathSettings);
        newQnForm.setVisibilitySettings(visibilitySettings);
        
        feedbackPathSettings.setGiverParticipantOptions(getParticipantOptions(null, true));
        feedbackPathSettings.setRecipientParticipantOptions(getParticipantOptions(null, false));
        feedbackPathSettings.setNumOfEntitiesToGiveFeedbackToValue(1);
        
        newQnForm.setNumOfQuestionsOnPage(questions.size());
        newQnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size() + 1));
      
        newQnForm.setQuestionSpecificEditFormHtml(getNewQuestionSpecificEditFormHtml());
        newQnForm.setEditable(true);
        
        setDefaultVisibilityOptions(visibilitySettings, feedbackPathSettings);
    }


    private void setDefaultVisibilityOptions(FeedbackQuestionVisibilitySettings visibilityOptions,
                                             FeedbackQuestionFeedbackPathSettings feedbackPathSettings) {
        Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
        Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
        Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
        
        List<FeedbackParticipantType> participantVisiblity = new ArrayList<FeedbackParticipantType>();
        participantVisiblity.add(FeedbackParticipantType.INSTRUCTORS);
        participantVisiblity.add(FeedbackParticipantType.RECEIVER);
        
        for (FeedbackParticipantType participant : participantVisiblity) {
            isGiverNameVisible.put(participant.name(), true);
            isRecipientNameVisible.put(participant.name(), true);
            isResponsesVisible.put(participant.name(), true);
        }
        
        visibilityOptions.setGiverNameVisibleFor(isGiverNameVisible);
        visibilityOptions.setRecipientNameVisibleFor(isRecipientNameVisible);
        visibilityOptions.setResponseVisibleFor(isResponsesVisible);
    }
    
    
    private void buildBasicFsForm(FeedbackSessionAttributes newFeedbackSession, AdditionalSettingsFormSegment additionalSettings) {
        fsForm = FeedbackSessionsForm.getFsFormForExistingFs(this, newFeedbackSession, additionalSettings);
    }

    private AdditionalSettingsFormSegment buildFsFormAdditionalSettings(FeedbackSessionAttributes newFeedbackSession) {
        
        return AdditionalSettingsFormSegment.getFormSegmentWithExistingValues(this, newFeedbackSession); 
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

    public String getEmptyFsMsg() {
        return emptyFsMsg;
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
                    boolean isGiverType     = isValidGiver     && question.giverType == option;
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
     * Returns a list of HTML options for selecting question type.
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


}
package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
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
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.AdditionalSettingsFormSegment;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackQuestionGeneralSettings;
import teammates.ui.template.FeedbackQuestionTableRow;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageData extends PageData {

    private static final String SAVE_CHANGES = "Save Changes";
    
    private String emptyFsMsg = Const.StatusMessages.FEEDBACK_QUESTION_EMPTY; 
    private FeedbackSessionsForm fsForm;
    private List<FeedbackQuestionEditForm> qnForms;
    private FeedbackQuestionEditForm newQnForm;
    private FeedbackSessionPreviewForm previewForm;
    private FeedbackQuestionCopyTable copyQnForm;
    
    public InstructorFeedbackEditPageData(AccountAttributes account) {
        super(account);
        
    }
    

    public void init(FeedbackSessionAttributes feedbackSession, 
                     List<FeedbackQuestionAttributes> questions,
                     List<FeedbackQuestionAttributes> copiableQuestions,
                     Map<String, Boolean> questionHasResponses,
                     List<StudentAttributes> studentList,
                     List<InstructorAttributes> instructorList,
                     InstructorAttributes instructor) {
        Assumption.assertNotNull(feedbackSession);
        //form for editing the fs
        buildBasicFsForm(feedbackSession);
        fsForm.setAdditionalSettings(buildFsFormAdditionalSettings(feedbackSession));
        
        //forms for editing questions
        qnForms = new ArrayList<FeedbackQuestionEditForm>();
        for (FeedbackQuestionAttributes question : questions) {
            // build question edit form
            buildExistingQuestionForm(feedbackSession, questions, questionHasResponses, instructor, question);
        }
        
        // new qn form
        buildNewQuestionForm(feedbackSession, questions, instructor);
        
        // preview form
        buildPreviewForm(feedbackSession, copiableQuestions, studentList, instructorList, instructor);
        
    }


    private void buildPreviewForm(FeedbackSessionAttributes feedbackSession,
                                    List<FeedbackQuestionAttributes> copiableQuestions,
                                    List<StudentAttributes> studentList,
                                    List<InstructorAttributes> instructorList, InstructorAttributes instructor) {
        buildPreviewForm(feedbackSession, studentList, instructorList);
        
        // copy questions modal
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
        copyQnForm = new FeedbackQuestionCopyTable(feedbackSession.courseId, feedbackSession.feedbackSessionName, 
                                                       copyQuestionRows);
    }


    private void buildPreviewForm(FeedbackSessionAttributes feedbackSession,
                                    List<StudentAttributes> studentList,
                                    List<InstructorAttributes> instructorList) {
        previewForm = new FeedbackSessionPreviewForm(feedbackSession.courseId, feedbackSession.feedbackSessionName, 
                                                     getPreviewAsStudentOptions(studentList), 
                                                     getPreviewAsInstructorOptions(instructorList));
    }


    private void buildExistingQuestionForm(FeedbackSessionAttributes feedbackSession,
                                    List<FeedbackQuestionAttributes> questions,
                                    Map<String, Boolean> questionHasResponses,
                                    InstructorAttributes instructor, FeedbackQuestionAttributes question) {
        FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
        qnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT));
        qnForm.setCourseId(instructor.courseId);
        qnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
        qnForm.setQuestion(question);
        qnForm.setQuestionDetails(question.getQuestionDetails());
        
        qnForm.setNumOfQuestionsOnPage(questions.size());
        qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size()));
        qnForm.setQuestionText(question.getQuestionDetails().questionText);
        
        FeedbackQuestionGeneralSettings generalSettings = new FeedbackQuestionGeneralSettings();
        qnForm.setGeneralSettings(generalSettings);
        generalSettings.setGiverParticipantOptions(getParticipantOptions(question, true));
        generalSettings.setRecipientParticipantOptions(getParticipantOptions(question, false));
        
        
        // maps for setting visibility
        Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType giverType : question.showGiverNameTo) {
            isGiverNameVisible.put(giverType.name(), true);
        }
        generalSettings.setIsGiverNameVisible(isGiverNameVisible);
        
        Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
            isRecipientNameVisible.put(recipientType.name(), true);
        }
        generalSettings.setIsRecipientNameVisible(isRecipientNameVisible);
        
        Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
        for (FeedbackParticipantType participantType : question.showResponsesTo) {
            isResponsesVisible.put(participantType.name(), true);
        }
        generalSettings.setIsResponseVisible(isResponsesVisible);
        
        generalSettings.setNumberOfEntitiesToGiveFeedbackToChecked(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS);
        generalSettings.setNumOfEntitiesToGiveFeedbackToValue(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ?
                                                     1 :
                                                     question.numberOfEntitiesToGiveFeedbackTo);
        qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));
        
        List<String> visibilityMessages = question.getVisibilityMessage();
        generalSettings.setVisibilityMessages(visibilityMessages);
        
        qnForm.setQuestionSpecificEditFormHtml(question.getQuestionDetails().getQuestionSpecificEditFormHtml(question.questionNumber));
        
        qnForms.add(qnForm);
    }


    private void buildNewQuestionForm(FeedbackSessionAttributes feedbackSession,
                                    List<FeedbackQuestionAttributes> questions,
                                    InstructorAttributes instructor) {
        newQnForm = new FeedbackQuestionEditForm();
        newQnForm.doneEditingLink = new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                        .withUserId(account.googleId)
                                        .withCourseId(feedbackSession.courseId)
                                        .withSessionName(feedbackSession.feedbackSessionName);
        
        
        newQnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD));
        newQnForm.setCourseId(instructor.courseId);
        newQnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
        
        newQnForm.questionTypeOptions = getQuestionTypeChoiceOptions();
      
        FeedbackQuestionGeneralSettings generalSettings = new FeedbackQuestionGeneralSettings();  
        newQnForm.setGeneralSettings(generalSettings);
        generalSettings.setGiverParticipantOptions(getParticipantOptions(null, true));
        generalSettings.setRecipientParticipantOptions(getParticipantOptions(null, false));
        newQnForm.setNumOfQuestionsOnPage(questions.size());
        newQnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size() + 1));
      
        newQnForm.setQuestionSpecificEditFormHtml(getNewQuestionSpecificEditFormHtml());
        
        setDefaultVisibilityOptions(generalSettings);
    }


    private void setDefaultVisibilityOptions(FeedbackQuestionGeneralSettings generalSettings) {
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
        generalSettings.setIsGiverNameVisible(isGiverNameVisible);
        generalSettings.setIsRecipientNameVisible(isRecipientNameVisible);
        generalSettings.setIsResponseVisible(isResponsesVisible);
    }
    
    
    private void buildBasicFsForm(FeedbackSessionAttributes newFeedbackSession) {
        fsForm = new FeedbackSessionsForm();
        
        fsForm.setFsDeleteLink(new Url(getInstructorFeedbackSessionDeleteLink(newFeedbackSession.courseId, newFeedbackSession.feedbackSessionName, "")));
        fsForm.setCopyToLink(new Url(getFeedbackSessionEditCopyLink()));
        
        fsForm.setCourseIdForNewSession(newFeedbackSession.courseId);
        
        fsForm.setFsNameEditable(false);
        fsForm.setFsName(newFeedbackSession.feedbackSessionName);
        
        fsForm.setCourseIdEditable(false);
        fsForm.setCourses(null);
        
        fsForm.setFeedbackSessionTypeEditable(false);
        fsForm.setFeedbackSessionTypeOptions(null);

        fsForm.setFeedbackSessionNameForSessionList(null);
        
        fsForm.setCoursesSelectField(null);
        
        fsForm.setTimezoneSelectField(getTimeZoneOptionsAsElementTags(newFeedbackSession.timeZone));
        
        
        fsForm.setInstructions(sanitizeForHtml(newFeedbackSession.instructions.getValue()));
        
        fsForm.setFsStartDate(TimeHelper.formatDate(newFeedbackSession.startTime));
        
        Date date;
        date = newFeedbackSession.startTime;
        fsForm.setFsStartTimeOptions(getTimeOptionsAsElementTags(date));
        
        fsForm.setFsEndDate(TimeHelper.formatDate(newFeedbackSession.endTime));
        
        
        date = newFeedbackSession.endTime;
        fsForm.setFsEndTimeOptions(getTimeOptionsAsElementTags(date));
        
        fsForm.setGracePeriodOptions(getGracePeriodOptionsAsElementTags(newFeedbackSession.gracePeriod));
        
        fsForm.setSubmitButtonDisabled(false);
        fsForm.setFormSubmitAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE));
        fsForm.setSubmitButtonText(SAVE_CHANGES);
        fsForm.setSubmitButtonVisible(false);
    }

    private AdditionalSettingsFormSegment buildFsFormAdditionalSettings(FeedbackSessionAttributes newFeedbackSession) {
        
        Date date;
        AdditionalSettingsFormSegment additionalSettings = new AdditionalSettingsFormSegment(); 
        boolean hasSessionVisibleDate = !TimeHelper.isSpecialTime(newFeedbackSession.sessionVisibleFromTime);
        additionalSettings.setSessionVisibleDateButtonChecked(hasSessionVisibleDate);
        additionalSettings.setSessionVisibleDateValue(hasSessionVisibleDate ? 
                                                      TimeHelper.formatDate(newFeedbackSession.sessionVisibleFromTime) :
                                                      "");
        additionalSettings.setSessionVisibleDateDisabled(!hasSessionVisibleDate);
        
        date = hasSessionVisibleDate ? newFeedbackSession.sessionVisibleFromTime : null;   
        additionalSettings.setSessionVisibleTimeOptions(getTimeOptionsAsElementTags(date));
        
        additionalSettings.setSessionVisibleAtOpenChecked(Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(
                                                                   newFeedbackSession.sessionVisibleFromTime));
        
        additionalSettings.setSessionVisiblePrivateChecked(Const.TIME_REPRESENTS_NEVER.equals(
                                                               newFeedbackSession.sessionVisibleFromTime));
                        
        boolean hasResultVisibleDate = !TimeHelper.isSpecialTime(newFeedbackSession.resultsVisibleFromTime);
        
        additionalSettings.setResponseVisibleDateChecked(hasResultVisibleDate);
        
        additionalSettings.setResponseVisibleDateValue(hasResultVisibleDate ? 
                                                       TimeHelper.formatDate(newFeedbackSession.resultsVisibleFromTime) :
                                                       "");
        
        additionalSettings.setResponseVisibleDateDisabled(!hasResultVisibleDate);
        
        date = hasResultVisibleDate ? newFeedbackSession.resultsVisibleFromTime :  null;
        additionalSettings.setResponseVisibleTimeOptions(getTimeOptionsAsElementTags(date));
        
        additionalSettings.setResponseVisibleImmediatelyChecked(Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(newFeedbackSession.resultsVisibleFromTime));
        
        additionalSettings.setResponseVisiblePublishManuallyChecked(
                                 (Const.TIME_REPRESENTS_LATER.equals(newFeedbackSession.resultsVisibleFromTime) 
                                  || Const.TIME_REPRESENTS_NOW.equals(newFeedbackSession.resultsVisibleFromTime)));
        
        additionalSettings.setResponseVisibleNeverChecked(Const.TIME_REPRESENTS_NEVER.equals(newFeedbackSession.resultsVisibleFromTime));
        
        additionalSettings.setSendClosingEmailChecked(newFeedbackSession.isClosingEmailEnabled);
        additionalSettings.setSendOpeningEmailChecked(newFeedbackSession.isOpeningEmailEnabled);
        additionalSettings.setSendPublishedEmailChecked(newFeedbackSession.isPublishedEmailEnabled);
        
        return additionalSettings;
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
                
                boolean selected = false;
                // for existing questions
                if (question != null) {
                    boolean isGiverType     = isValidGiver     && question.giverType == option;
                    boolean isRecipientType = isValidRecipient && question.recipientType == option;
                    
                    selected = (isGiverType || isRecipientType); 
                }
                
                ElementTag optionTag = createOption(participantName, option.toString(), selected);
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
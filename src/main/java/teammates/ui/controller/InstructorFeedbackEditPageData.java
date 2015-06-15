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
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.AdditionalSettingsFormSegment;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageData extends PageData {

    private FeedbackSessionsForm fsForm;
    private List<FeedbackQuestionEditForm> qnForms;
    private FeedbackQuestionEditForm newQnForm;
    
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
        
        buildBasicFsForm(feedbackSession);
        buildFsFormAdditionalSettings(feedbackSession);
        
        qnForms = new ArrayList<FeedbackQuestionEditForm>();
        for (FeedbackQuestionAttributes question : questions) {
            // build question edit form
            FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
            qnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT));
            qnForm.setCourseId(instructor.courseId);
            qnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
            qnForm.setQuestion(question);
            qnForm.setQuestionDetails(question.getQuestionDetails());
            qnForm.setGiverParticipantOptions(getParticipantOptions(question, true));
            qnForm.setRecipientParticipantOptions(getParticipantOptions(question, false));
            qnForm.setNumOfQuestionsOnPage(questions.size());
            qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size()));
            qnForm.setQuestionText(question.getQuestionDetails().questionText);
            
            // maps 
            
            Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType giverType : question.showGiverNameTo) {
                isGiverNameVisible.put(giverType.name(), true);
            }
            qnForm.setIsGiverNameVisible(isGiverNameVisible);
            
            Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
                isRecipientNameVisible.put(recipientType.name(), true);
            }
            qnForm.setIsRecipientNameVisible(isRecipientNameVisible);
            
            Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType participantType : question.showResponsesTo) {
                isResponsesVisible.put(participantType.name(), true);
            }
            qnForm.setIsResponseVisible(isResponsesVisible);
            
            qnForm.setNumberOfEntitiesToGiveFeedbackToChecked(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS);
            qnForm.setNumOfEntitiesToGiveFeedbackToValue(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ?
                                                         1 :
                                                         question.numberOfEntitiesToGiveFeedbackTo);
            qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));
            
            List<String> visibilityMessages = question.getVisibilityMessage();
            qnForm.setVisibilityMessages(visibilityMessages);
            
            qnForm.setQuestionSpecificEditFormHtml(question.getQuestionDetails().getQuestionSpecificEditFormHtml(question.questionNumber));
            
            qnForms.add(qnForm);
        }
        
        // new qn form
        newQnForm = new FeedbackQuestionEditForm();
        newQnForm.doneEditingLink = new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                        .withUserId(account.googleId)
                                        .withCourseId(feedbackSession.courseId)
                                        .withSessionName(feedbackSession.feedbackSessionName);
        
        
        newQnForm.setAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD));
        newQnForm.setCourseId(instructor.courseId);
        newQnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
        
        newQnForm.questionTypeOptions = getQuestionTypeChoiceOptions();
      
        newQnForm.setGiverParticipantOptions(getParticipantOptions(null, true));
        newQnForm.setRecipientParticipantOptions(getParticipantOptions(null, false));
        newQnForm.setNumOfQuestionsOnPage(questions.size());
        newQnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size() + 1));
      
        newQnForm.setQuestionSpecificEditFormHtml(getNewQuestionSpecificEditFormHtml());
        
        
    }
    
    
    private void buildBasicFsForm(FeedbackSessionAttributes newFeedbackSession) {
        fsForm = new FeedbackSessionsForm();
        
        fsForm.setCourseIdForNewSession(newFeedbackSession.courseId);
        
        fsForm.setFsNameEditable(false);
        fsForm.setFsName(newFeedbackSession == null ? "" : newFeedbackSession.feedbackSessionName);
        
        fsForm.setCourseIdEditable(false);
        fsForm.setCourses(null);
        
        fsForm.setFeedbackSessionTypeEditable(false);
        fsForm.setFeedbackSessionTypeOptions(null);

        fsForm.setFeedbackSessionNameForSessionList(null);
        
        fsForm.setCoursesSelectField(null);
        
        fsForm.setTimezoneSelectField(getTimeZoneOptionsAsElementTags(newFeedbackSession.timeZone));
        
        
        fsForm.setInstructions(newFeedbackSession == null ?
                               "Please answer all the given questions." :
                                sanitizeForHtml(newFeedbackSession.instructions.getValue()));
        
        fsForm.setFsStartDate(newFeedbackSession == null ?
                              TimeHelper.formatDate(TimeHelper.getNextHour()) :
                              TimeHelper.formatDate(newFeedbackSession.startTime));
        
        Date date;
        date = newFeedbackSession == null ? null : newFeedbackSession.startTime;
        fsForm.setFsStartTimeOptions(getTimeOptionsAsElementTags(date));
        
        fsForm.setFsEndDate(newFeedbackSession == null ?
                            "" : 
                            TimeHelper.formatDate(newFeedbackSession.endTime));
        
        
        date = newFeedbackSession == null ? null : newFeedbackSession.endTime;
        fsForm.setFsEndTimeOptions(getTimeOptionsAsElementTags(date));
        
        fsForm.setGracePeriodOptions(getGracePeriodOptionsAsElementTags(newFeedbackSession.gracePeriod));
        
        fsForm.setSubmitButtonDisabled(false);
        fsForm.setFormSubmitAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD));
        fsForm.setSubmitButtonText("Save Changes");
        fsForm.setSubmitButtonVisible(false);
    }

    private AdditionalSettingsFormSegment buildFsFormAdditionalSettings(FeedbackSessionAttributes newFeedbackSession) {
        
        Date date;
        AdditionalSettingsFormSegment additionalSettings = new AdditionalSettingsFormSegment(); 
        boolean hasSessionVisibleDate = newFeedbackSession != null 
                                        && !TimeHelper.isSpecialTime(newFeedbackSession.sessionVisibleFromTime);
        additionalSettings.setSessionVisibleDateButtonChecked(hasSessionVisibleDate);
        additionalSettings.setSessionVisibleDateValue(hasSessionVisibleDate ? 
                                                      TimeHelper.formatDate(newFeedbackSession.sessionVisibleFromTime) :
                                                      "");
        additionalSettings.setSessionVisibleDateDisabled(!hasSessionVisibleDate);
        
        date = hasSessionVisibleDate ? newFeedbackSession.sessionVisibleFromTime : null;   
        additionalSettings.setSessionVisibleTimeOptions(getTimeOptionsAsElementTags(date));
        
        additionalSettings.setSessionVisibleAtOpenChecked(newFeedbackSession == null 
                                                           || Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(
                                                                   newFeedbackSession.sessionVisibleFromTime));
        
        additionalSettings.setSessionVisiblePrivateChecked(newFeedbackSession != null 
                                                           && Const.TIME_REPRESENTS_NEVER.equals(
                                                               newFeedbackSession.sessionVisibleFromTime));
                        
        boolean hasResultVisibleDate = newFeedbackSession != null 
                                       && !TimeHelper.isSpecialTime(newFeedbackSession.resultsVisibleFromTime);
        
        additionalSettings.setResponseVisibleDateChecked(hasResultVisibleDate);
        
        additionalSettings.setResponseVisibleDateValue(hasResultVisibleDate ? 
                                                       TimeHelper.formatDate(newFeedbackSession.resultsVisibleFromTime) :
                                                       "");
        
        additionalSettings.setResponseVisibleDisabled(!hasResultVisibleDate);
        
        date = hasResultVisibleDate ? newFeedbackSession.resultsVisibleFromTime :  null;
        additionalSettings.setResponseVisibleTimeOptions(getTimeOptionsAsElementTags(date));
        
        additionalSettings.setResponseVisibleImmediatelyChecked((newFeedbackSession != null 
                                                                && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(newFeedbackSession.resultsVisibleFromTime)));
        
        additionalSettings.setResponseVisiblePublishManuallyChecked(
                                 (newFeedbackSession == null 
                                  || Const.TIME_REPRESENTS_LATER.equals(newFeedbackSession.resultsVisibleFromTime) 
                                  || Const.TIME_REPRESENTS_NOW.equals(newFeedbackSession.resultsVisibleFromTime)));
        
        additionalSettings.setResponseVisibleNeverChecked((newFeedbackSession != null  
                                                            && Const.TIME_REPRESENTS_NEVER.equals(newFeedbackSession.resultsVisibleFromTime)));
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
    public List<String> getQuestionTypeChoiceOptions() {
        List<String> options = new ArrayList<String>();
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            options.add(type.getFeedbackQuestionDetailsInstance().getQuestionTypeChoiceOption());
        }
        return options;
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



}
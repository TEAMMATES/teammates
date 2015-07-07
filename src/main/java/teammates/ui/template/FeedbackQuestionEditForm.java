package teammates.ui.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * Data model for adding/editing a single question 
 *
 */
public class FeedbackQuestionEditForm {
    private String actionLink;
    
    private String courseId;
    private String feedbackSessionName;
    
    private String questionNumberSuffix;
    
    // Used for adding a new question
    private String questionTypeOptions;
    private String doneEditingLink;
    
    private boolean isQuestionHasResponses;
    private List<ElementTag> questionNumberOptions;
    
    private FeedbackQuestionAttributes question;
    
    //TODO use element tags or a new class instead of having html in java
    private String questionSpecificEditFormHtml;
    
    private boolean isEditable;
    private FeedbackQuestionFeedbackPathSettings feedbackPathSettings;
    private FeedbackQuestionVisibilitySettings visibilitySettings;
    
    public static FeedbackQuestionEditForm getNewQnForm(String doneEditingLink, FeedbackSessionAttributes feedbackSession,
                                                        String questionTypeChoiceOptions, List<ElementTag> giverOptions,
                                                        List<ElementTag> recipientOptions, List<ElementTag> qnNumOptions, String newQuestionEditForm) {
        
        FeedbackQuestionEditForm newQnForm = new FeedbackQuestionEditForm();
        
        newQnForm.doneEditingLink = doneEditingLink.toString();
        newQnForm.actionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
        newQnForm.courseId = feedbackSession.courseId;
        newQnForm.feedbackSessionName = feedbackSession.feedbackSessionName;
        newQnForm.questionNumberSuffix = "";
        
        newQnForm.questionTypeOptions = questionTypeChoiceOptions;
        
        newQnForm.questionNumberOptions = qnNumOptions;
      
        FeedbackQuestionFeedbackPathSettings feedbackPathSettings = new FeedbackQuestionFeedbackPathSettings();
        FeedbackQuestionVisibilitySettings visibilitySettings = new FeedbackQuestionVisibilitySettings();
        
        newQnForm.feedbackPathSettings = feedbackPathSettings;
        newQnForm.visibilitySettings = visibilitySettings;
        
        feedbackPathSettings.setGiverParticipantOptions(giverOptions);
        feedbackPathSettings.setRecipientParticipantOptions(recipientOptions);
        feedbackPathSettings.setNumOfEntitiesToGiveFeedbackToValue(1);
        
        newQnForm.questionSpecificEditFormHtml = newQuestionEditForm;
        newQnForm.isEditable = true;
        
        setDefaultVisibilityOptions(visibilitySettings, feedbackPathSettings);
        
        return newQnForm;
    }
    
    private static void setDefaultVisibilityOptions(FeedbackQuestionVisibilitySettings visibilityOptions,
                                                    FeedbackQuestionFeedbackPathSettings feedbackPathSettings) {
        Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
        Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
        Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
        
        FeedbackParticipantType[] participantTypes = { FeedbackParticipantType.INSTRUCTORS,
                                                       FeedbackParticipantType.RECEIVER    };
        
        for (FeedbackParticipantType participant : participantTypes) {
           isGiverNameVisible.put(participant.name(), true);
           isRecipientNameVisible.put(participant.name(), true);
           isResponsesVisible.put(participant.name(), true);
        }
        
        visibilityOptions.setGiverNameVisibleFor(isGiverNameVisible);
        visibilityOptions.setRecipientNameVisibleFor(isRecipientNameVisible);
        visibilityOptions.setResponseVisibleFor(isResponsesVisible);
    }
    
    public FeedbackQuestionEditForm() {
    }
    
    
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }
    
    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }
    
    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }
    
    public void setQuestion(FeedbackQuestionAttributes question) {
        this.question = question;
    }
    
    public boolean isQuestionHasResponses() {
        return isQuestionHasResponses;
    }
    
    public void setQuestionHasResponses(boolean isQuestionHasResponses) {
        this.isQuestionHasResponses = isQuestionHasResponses;
    }
    
    public List<ElementTag> getQuestionNumberOptions() {
        return questionNumberOptions;
    }
    
    public void setQuestionNumberOptions(List<ElementTag> questionNumberOptions) {
        this.questionNumberOptions = questionNumberOptions;
    }
    
    public String getQuestionSpecificEditFormHtml() {
        return questionSpecificEditFormHtml;
    }
    
    public void setQuestionSpecificEditFormHtml(String questionSpecificEditFormHtml) {
        this.questionSpecificEditFormHtml = questionSpecificEditFormHtml;
    }
   
    public String getQuestionText() {
        return question.getQuestionDetails().questionText;
    }

    public String getAction() {
        return actionLink;
    }

    public void setAction(String action) {
        this.actionLink = action;
    }

    public String getQuestionTypeOptions() {
        return questionTypeOptions;
    }

    public String getDoneEditingLink() {
        return doneEditingLink;
    }

    public FeedbackQuestionFeedbackPathSettings getFeedbackPathSettings() {
        return feedbackPathSettings;
    }

    public void setFeedbackPathSettings(FeedbackQuestionFeedbackPathSettings generalSettings) {
        this.feedbackPathSettings = generalSettings;
    }

    public String getQuestionNumberSuffix() {
        return questionNumberSuffix;
    }

    public void setQuestionNumberSuffix(String questionNumberSuffix) {
        this.questionNumberSuffix = questionNumberSuffix;
    }

    public void setDoneEditingLink(String doneEditingLink) {
        this.doneEditingLink = doneEditingLink;
    }

    public void setQuestionTypeOptions(String questionTypeOptions) {
        this.questionTypeOptions = questionTypeOptions;
    }

    public FeedbackQuestionVisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }

    public void setVisibilitySettings(FeedbackQuestionVisibilitySettings visibilitySettings) {
        this.visibilitySettings = visibilitySettings;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

}

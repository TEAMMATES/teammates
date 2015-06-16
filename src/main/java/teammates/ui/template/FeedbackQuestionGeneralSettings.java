package teammates.ui.template;

import java.util.List;
import java.util.Map;


/**
 * Data model for the feedback path and visibility settings for the 
 * feedback question form. Used on instructorFeedbackEdit.jsp
 * 
 */
public class FeedbackQuestionGeneralSettings {
    private List<ElementTag> giverParticipantOptions;
    private List<ElementTag> recipientParticipantOptions;
    
    private boolean isNumberOfEntitiesToGiveFeedbackToChecked;
    private int numOfEntitiesToGiveFeedbackToValue;
    
    private List<String> visibilityMessages;
    private Map<String, Boolean> isGiverNameVisible;
    private Map<String, Boolean> isRecipientNameVisible;
    private Map<String, Boolean> isResponseVisible;
    
    public FeedbackQuestionGeneralSettings() {
        
    }

    public List<ElementTag> getGiverParticipantOptions() {
        return giverParticipantOptions;
    }

    public void setGiverParticipantOptions(List<ElementTag> giverParticipantOptions) {
        this.giverParticipantOptions = giverParticipantOptions;
    }

    public List<ElementTag> getRecipientParticipantOptions() {
        return recipientParticipantOptions;
    }

    public void setRecipientParticipantOptions(List<ElementTag> recipientParticipantOptions) {
        this.recipientParticipantOptions = recipientParticipantOptions;
    }

    public boolean isNumberOfEntitiesToGiveFeedbackToChecked() {
        return isNumberOfEntitiesToGiveFeedbackToChecked;
    }

    public void setNumberOfEntitiesToGiveFeedbackToChecked(boolean isNumberOfEntitiesToGiveFeedbackToChecked) {
        this.isNumberOfEntitiesToGiveFeedbackToChecked = isNumberOfEntitiesToGiveFeedbackToChecked;
    }

    public int getNumOfEntitiesToGiveFeedbackToValue() {
        return numOfEntitiesToGiveFeedbackToValue;
    }

    public void setNumOfEntitiesToGiveFeedbackToValue(int numOfEntitiesToGiveFeedbackToValue) {
        this.numOfEntitiesToGiveFeedbackToValue = numOfEntitiesToGiveFeedbackToValue;
    }

    public List<String> getVisibilityMessages() {
        return visibilityMessages;
    }

    public void setVisibilityMessages(List<String> visibilityMessages) {
        this.visibilityMessages = visibilityMessages;
    }

    public Map<String, Boolean> isGiverNameVisible() {
        return isGiverNameVisible;
    }

    public void setIsGiverNameVisible(Map<String, Boolean> isGiverNameVisible) {
        this.isGiverNameVisible = isGiverNameVisible;
    }

    public Map<String, Boolean> isRecipientNameVisible() {
        return isRecipientNameVisible;
    }

    public void setIsRecipientNameVisible(Map<String, Boolean> isRecipientNameVisible) {
        this.isRecipientNameVisible = isRecipientNameVisible;
    }

    public Map<String, Boolean> isResponseVisible() {
        return isResponseVisible;
    }

    public void setIsResponseVisible(Map<String, Boolean> isResponseVisible) {
        this.isResponseVisible = isResponseVisible;
    }
    
}
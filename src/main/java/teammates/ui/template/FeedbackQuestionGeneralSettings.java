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
    private Map<String, Boolean> giverNameVisibleFor;
    private Map<String, Boolean> recipientNameVisibleFor;
    private Map<String, Boolean> responseVisibleFor;
    
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

    public Map<String, Boolean> getGiverNameVisibleFor() {
        return giverNameVisibleFor;
    }

    public void setGiverNameVisibleFor(Map<String, Boolean> isGiverNameVisible) {
        this.giverNameVisibleFor = isGiverNameVisible;
    }

    public Map<String, Boolean> getRecipientNameVisibleFor() {
        return recipientNameVisibleFor;
    }

    public void setRecipientNameVisibleFor(Map<String, Boolean> isRecipientNameVisible) {
        this.recipientNameVisibleFor = isRecipientNameVisible;
    }

    public Map<String, Boolean> getResponseVisibleFor() {
        return responseVisibleFor;
    }

    public void setResponseVisibleFor(Map<String, Boolean> isResponseVisible) {
        this.responseVisibleFor = isResponseVisible;
    }
    
}
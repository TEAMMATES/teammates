package teammates.ui.template;

import java.util.List;


/**
 * Data model for the settings common to all question types,
 * which are the feedback path and visibility settings for the 
 * feedback question form. 
 * 
 * Used on instructorFeedbackEdit.jsp
 */
public class FeedbackQuestionFeedbackPathSettings {
    private List<ElementTag> giverParticipantOptions;
    private List<ElementTag> recipientParticipantOptions;
    
    private boolean isNumberOfEntitiesToGiveFeedbackToChecked;
    private int numOfEntitiesToGiveFeedbackToValue;
    
    
    public FeedbackQuestionFeedbackPathSettings() {
        
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
    
}
package teammates.ui.template;

import teammates.common.datatransfer.FeedbackParticipantType;

/**
 * Data model for the settings common to all question types,
 * which are the feedback path and visibility settings for the
 * feedback question form.
 */
public class FeedbackQuestionFeedbackPathSettings {

    private FeedbackParticipantType selectedGiver;
    private FeedbackParticipantType selectedRecipient;

    private boolean isCommonPathSelected;

    private boolean isNumberOfEntitiesToGiveFeedbackToChecked;
    private int numOfEntitiesToGiveFeedbackToValue;

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

    public boolean isCommonPathSelected() {
        return isCommonPathSelected;
    }

    public void setCommonPathSelected(boolean isCommonPathSelected) {
        this.isCommonPathSelected = isCommonPathSelected;
    }

    public FeedbackParticipantType getSelectedRecipient() {
        return selectedRecipient;
    }

    public void setSelectedRecipient(FeedbackParticipantType selectedRecipient) {
        this.selectedRecipient = selectedRecipient;
    }

    public FeedbackParticipantType getSelectedGiver() {
        return selectedGiver;
    }

    public void setSelectedGiver(FeedbackParticipantType selectedGiver) {
        this.selectedGiver = selectedGiver;
    }
}

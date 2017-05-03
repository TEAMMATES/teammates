package teammates.ui.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackPathAttributes;
import teammates.common.util.SanitizationHelper;


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

    private String customFeedbackPathsSpreadsheetData;

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

    public String getCustomFeedbackPathsSpreadsheetData() {
        return customFeedbackPathsSpreadsheetData;
    }

    public void setCustomFeedbackPathsSpreadsheetData(List<FeedbackPathAttributes> feedbackPaths) {
        List<List<String>> customFeedbackPaths = new ArrayList<List<String>>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            customFeedbackPaths.add(Arrays.asList(feedbackPath.getGiver(),
                                                  feedbackPath.getRecipient()));
        }
        List<String> customFeedbackPathStrings = new ArrayList<String>();
        for (List<String> customFeedbackPath : customFeedbackPaths) {
            customFeedbackPathStrings.add(SanitizationHelper.sanitizeListForCsv(customFeedbackPath).toString());
        }
        customFeedbackPathsSpreadsheetData = SanitizationHelper.sanitizeForHtml(customFeedbackPathStrings).toString();
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

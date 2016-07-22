package teammates.ui.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.FeedbackPathAttributes;
import teammates.common.util.Sanitizer;

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
    
    private String customFeedbackPathsSpreadsheetData;

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
            customFeedbackPathStrings.add(Sanitizer.sanitizeListForCsv(customFeedbackPath).toString());
        }
        customFeedbackPathsSpreadsheetData = Sanitizer.sanitizeForHtml(customFeedbackPathStrings).toString();
    }
}

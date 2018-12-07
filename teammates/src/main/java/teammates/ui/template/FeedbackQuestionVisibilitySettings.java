package teammates.ui.template;

import java.util.List;
import java.util.Map;

public class FeedbackQuestionVisibilitySettings {
    private List<String> visibilityMessages;
    private Map<String, Boolean> giverNameVisibleFor;
    private Map<String, Boolean> recipientNameVisibleFor;
    private Map<String, Boolean> responseVisibleFor;
    private String dropdownMenuLabel;

    public FeedbackQuestionVisibilitySettings(List<String> visibilityMessages,
                                              Map<String, Boolean> responseVisibleFor,
                                              Map<String, Boolean> giverNameVisibleFor,
                                              Map<String, Boolean> recipientNameVisibleFor,
                                              String dropdownMenuLabel) {
        this.visibilityMessages = visibilityMessages;
        this.giverNameVisibleFor = giverNameVisibleFor;
        this.recipientNameVisibleFor = recipientNameVisibleFor;
        this.responseVisibleFor = responseVisibleFor;
        this.setDropdownMenuLabel(dropdownMenuLabel);
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

    public String getDropdownMenuLabel() {
        return dropdownMenuLabel;
    }

    public void setDropdownMenuLabel(String dropdownMenuLabel) {
        this.dropdownMenuLabel = dropdownMenuLabel;
    }
}

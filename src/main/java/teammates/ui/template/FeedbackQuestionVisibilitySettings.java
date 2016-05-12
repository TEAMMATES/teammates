package teammates.ui.template;

import java.util.List;
import java.util.Map;

public class FeedbackQuestionVisibilitySettings {
    private List<String> visibilityMessages;
    private Map<String, Boolean> giverNameVisibleFor;
    private Map<String, Boolean> recipientNameVisibleFor;
    private Map<String, Boolean> responseVisibleFor;
    
    public FeedbackQuestionVisibilitySettings() {
    }
    
    public FeedbackQuestionVisibilitySettings(final List<String> visibilityMessages,
                                              final Map<String, Boolean> responseVisibleFor,
                                              final Map<String, Boolean> giverNameVisibleFor,
                                              final Map<String, Boolean> recipientNameVisibleFor) {
        this.visibilityMessages = visibilityMessages;
        this.giverNameVisibleFor = giverNameVisibleFor;
        this.recipientNameVisibleFor = recipientNameVisibleFor;
        this.responseVisibleFor = responseVisibleFor;
    }

    public List<String> getVisibilityMessages() {
        return visibilityMessages;
    }

    public void setVisibilityMessages(final List<String> visibilityMessages) {
        this.visibilityMessages = visibilityMessages;
    }

    public Map<String, Boolean> getGiverNameVisibleFor() {
        return giverNameVisibleFor;
    }

    public void setGiverNameVisibleFor(final Map<String, Boolean> isGiverNameVisible) {
        this.giverNameVisibleFor = isGiverNameVisible;
    }

    public Map<String, Boolean> getRecipientNameVisibleFor() {
        return recipientNameVisibleFor;
    }

    public void setRecipientNameVisibleFor(final Map<String, Boolean> isRecipientNameVisible) {
        this.recipientNameVisibleFor = isRecipientNameVisible;
    }

    public Map<String, Boolean> getResponseVisibleFor() {
        return responseVisibleFor;
    }

    public void setResponseVisibleFor(final Map<String, Boolean> isResponseVisible) {
        this.responseVisibleFor = isResponseVisible;
    }
}

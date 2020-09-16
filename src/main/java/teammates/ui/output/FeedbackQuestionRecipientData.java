package teammates.ui.output;

/**
 * API output for feedback question recipient.
 */
public class FeedbackQuestionRecipientData extends ApiOutput {

    private String name;
    private String identifier;

    public FeedbackQuestionRecipientData(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }
}

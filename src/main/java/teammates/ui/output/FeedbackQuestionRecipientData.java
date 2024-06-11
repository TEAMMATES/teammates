package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.FeedbackQuestionRecipient;

/**
 * API output for feedback question recipient.
 */
public class FeedbackQuestionRecipientData extends ApiOutput {

    private String name;
    private String identifier;
    @Nullable
    private String section;
    @Nullable
    private String team;

    public FeedbackQuestionRecipientData(FeedbackQuestionRecipient recipient) {
        this.name = recipient.getName();
        this.identifier = recipient.getIdentifier();
        this.section = recipient.getSection();
        this.team = recipient.getTeam();
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSection() {
        return section;
    }

    public String getTeam() {
        return team;
    }
}

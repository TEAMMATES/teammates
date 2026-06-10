package teammates.ui.output;

import teammates.storage.entity.ResponseRecipient;

/**
 * API output for feedback question recipient.
 */
public class FeedbackQuestionRecipientData implements ApiOutput {

    private String name;
    private String identifier;
    private String section;
    private String team;

    public FeedbackQuestionRecipientData(ResponseRecipient recipient) {
        this.name = recipient.getDisplayName();
        this.identifier = recipient.getKey();
        this.section = recipient.getSectionName();
        this.team = recipient.getTeamName();
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

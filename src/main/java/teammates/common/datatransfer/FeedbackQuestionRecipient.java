package teammates.common.datatransfer;

import java.util.Objects;

/**
 * Represents a recipient of a feedback question.
 */
public final class FeedbackQuestionRecipient {

    private String name;
    private String identifier;
    private String section;
    private String team;

    public FeedbackQuestionRecipient(String name, String identifier, String section, String team) {
        this.name = name;
        this.identifier = identifier;
        this.section = section;
        this.team = team;
    }

    public FeedbackQuestionRecipient(String name, String identifier) {
        this(name, identifier, null, null);
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

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackQuestionRecipient otherAttributes = (FeedbackQuestionRecipient) other;
            return Objects.equals(this.name, otherAttributes.name)
                    && Objects.equals(this.identifier, otherAttributes.identifier)
                    && Objects.equals(this.section, otherAttributes.section)
                    && Objects.equals(this.team, otherAttributes.team);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (identifier == null ? 0 : identifier.hashCode());
        result = prime * result + (section == null ? 0 : section.hashCode());
        result = prime * result + (team == null ? 0 : team.hashCode());
        return result;
    }
}

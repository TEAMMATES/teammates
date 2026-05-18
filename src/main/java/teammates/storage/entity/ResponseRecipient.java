package teammates.storage.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import teammates.common.datatransfer.participanttypes.ResponseRecipientType;
import teammates.common.util.Const;

/**
 * Embeddable value object that identifies a recipient of a feedback response.
 */
@Embeddable
public class ResponseRecipient {
    @Column()
    @Enumerated(EnumType.STRING)
    private ResponseRecipientType recipientType;

    @Column(insertable = false, updatable = false)
    private UUID recipientUserId;

    @ManyToOne
    @JoinColumn(name = "recipientUserId")
    private User recipientUser;

    @Column(insertable = false, updatable = false)
    private UUID recipientTeamId;

    @ManyToOne
    @JoinColumn(name = "recipientTeamId")
    private Team recipientTeam;

    public ResponseRecipient() {
        setNoSpecificRecipient();
    }

    public ResponseRecipient(User recipientUser) {
        setRecipientUser(recipientUser);
    }

    public ResponseRecipient(Team recipientTeam) {
        setRecipientTeam(recipientTeam);
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    /**
     * Sets no specific recipient.
     */
    public void setNoSpecificRecipient() {
        this.recipientType = ResponseRecipientType.NO_SPECIFIC_RECIPIENT;
        this.recipientUser = null;
        this.recipientUserId = null;
        this.recipientTeam = null;
        this.recipientTeamId = null;
    }

    /**
     * Sets the recipient user.
     */
    public void setRecipientUser(User recipientUser) {
        if (recipientUser instanceof Instructor) {
            this.recipientType = ResponseRecipientType.INSTRUCTOR;
        } else {
            this.recipientType = ResponseRecipientType.STUDENT;
        }

        this.recipientUser = recipientUser;
        this.recipientUserId = recipientUser == null ? null : recipientUser.getId();
        if (recipientUser != null) {
            this.recipientTeam = null;
            this.recipientTeamId = null;
        }
    }

    public UUID getRecipientTeamId() {
        return recipientTeamId;
    }

    public Team getRecipientTeam() {
        return recipientTeam;
    }

    public ResponseRecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Sets the recipient team.
     */
    public void setRecipientTeam(Team recipientTeam) {
        this.recipientType = ResponseRecipientType.TEAM;
        this.recipientTeam = recipientTeam;
        this.recipientTeamId = recipientTeam == null ? null : recipientTeam.getId();
        if (recipientTeam != null) {
            this.recipientUser = null;
            this.recipientUserId = null;
        }
    }

    /**
     * Gets the team name of the recipient.
     * If the recipient is an instructor, returns the instructor team name.
     * If the recipient is NO_SPECIFIC_RECIPIENT, returns a default text.
     */
    public String getTeamName() {
        if (recipientType == ResponseRecipientType.TEAM) {
            return recipientTeam != null ? recipientTeam.getName() : Const.UNKNOWN_TEAM;
        } else if (recipientType == ResponseRecipientType.NO_SPECIFIC_RECIPIENT) {
            return Const.USER_NOBODY_TEXT;
        }

        // recipient is a user
        if (recipientUser instanceof Student student) {
            return student.getTeamName();
        } else if (recipientUser instanceof Instructor) {
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            return Const.UNKNOWN_TEAM;
        }
    }

    /**
     * Gets the section name of the recipient. If the recipient is an instructor, returns the default section name.
     */
    public String getSectionName() {
        if (recipientType == ResponseRecipientType.TEAM) {
            return recipientTeam != null ? recipientTeam.getSection().getName() : Const.UNKNOWN_SECTION;
        } else if (recipientType == ResponseRecipientType.NO_SPECIFIC_RECIPIENT || recipientUser instanceof Instructor) {
            return Const.DEFAULT_SECTION;
        } else if (recipientUser instanceof Student student) {
            return student.getSectionName();
        } else {
            return Const.UNKNOWN_SECTION;
        }
    }

    public boolean isNoSpecificRecipient() {
        return recipientType == ResponseRecipientType.NO_SPECIFIC_RECIPIENT;
    }

    public boolean isRecipientUser() {
        return recipientType == ResponseRecipientType.STUDENT
                || recipientType == ResponseRecipientType.INSTRUCTOR;
    }

    public boolean isRecipientTeam() {
        return recipientType == ResponseRecipientType.TEAM;
    }

    /**
     * Gets the recipient identifier: team name for team recipients, user email for user recipients.
     */
    public String getIdentifier() {
        switch (recipientType) {
        case TEAM:
            return recipientTeam == null ? Const.UNKNOWN_TEAM : recipientTeam.getName();
        case STUDENT, INSTRUCTOR:
            return recipientUser == null ? Const.UNKNOWN_USER : recipientUser.getEmail();
        case NO_SPECIFIC_RECIPIENT:
        default:
            return Const.GENERAL_QUESTION;
        }
    }

    /**
     * Gets the recipient display name: team name for team recipients, user name for user recipients.
     */
    public String getDisplayName() {
        switch (recipientType) {
        case TEAM:
            return recipientTeam == null ? Const.UNKNOWN_TEAM : recipientTeam.getName();
        case STUDENT, INSTRUCTOR:
            return recipientUser == null ? Const.UNKNOWN_USER : recipientUser.getName();
        case NO_SPECIFIC_RECIPIENT:
        default:
            return Const.USER_NOBODY_TEXT;
        }
    }

    /**
     * Formats the recipient type as a singular noun.
     */
    public String toSingularFormString() {
        switch (recipientType) {
        case TEAM:
            return "team";
        case STUDENT:
            return "student";
        case INSTRUCTOR:
            return "instructor";
        case NO_SPECIFIC_RECIPIENT:
        default:
            return "no specific recipient";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseRecipient)) {
            return false;
        }
        ResponseRecipient other = (ResponseRecipient) o;
        return Objects.equals(getRecipientUserId(), other.getRecipientUserId())
                && Objects.equals(getRecipientTeamId(), other.getRecipientTeamId())
                && recipientType == other.recipientType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecipientTeamId(), getRecipientUserId(), recipientType);
    }

    @Override
    public String toString() {
        switch (recipientType) {
        case TEAM:
            return "ResponseRecipient [recipientTeamId=" + getRecipientTeamId() + "]";
        case STUDENT, INSTRUCTOR:
            return "ResponseRecipient [recipientUserId=" + getRecipientUserId() + "]";
        case NO_SPECIFIC_RECIPIENT:
        default:
            return "ResponseRecipient [No Specific Recipient]";
        }
    }
}

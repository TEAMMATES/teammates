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
    @Column(name = "recipientType")
    @Enumerated(EnumType.STRING)
    private ResponseRecipientType recipientType;

    @Column(name = "recipientUserId", insertable = false, updatable = false)
    private UUID recipientUserId;

    @ManyToOne
    @JoinColumn(name = "recipientUserId")
    private User recipientUser;

    @Column(name = "recipientTeamId", insertable = false, updatable = false)
    private UUID recipientTeamId;

    @ManyToOne
    @JoinColumn(name = "recipientTeamId")
    private Team recipientTeam;

    protected ResponseRecipient() {
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
            return recipientTeam == null ? "Unknown Team" : recipientTeam.getName();
        case STUDENT, INSTRUCTOR:
            return recipientUser == null ? "Unknown User" : recipientUser.getEmail();
        case NO_SPECIFIC_RECIPIENT:
        default:
            return Const.USER_NOBODY_TEXT;
        }
    }

    /**
     * Gets the recipient display name: team name for team recipients, user name for user recipients.
     */
    public String getDisplayName() {
        switch (recipientType) {
        case TEAM:
            return recipientTeam == null ? "Unknown Team" : recipientTeam.getName();
        case STUDENT, INSTRUCTOR:
            return recipientUser == null ? "Unknown User" : recipientUser.getName();
        case NO_SPECIFIC_RECIPIENT:
        default:
            return Const.USER_NOBODY_TEXT;
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

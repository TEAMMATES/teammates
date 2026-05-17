package teammates.storage.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Embeddable value object that identifies a giver (or editor) of a feedback response or comment.
 */
@Embeddable
public class ResponseGiver {
    @Column(name = "giverUserId", insertable = false, updatable = false)
    private UUID giverUserId;

    @ManyToOne
    @JoinColumn(name = "giverUserId")
    private User giverUser;

    @Column(name = "giverTeamId", insertable = false, updatable = false)
    private UUID giverTeamId;

    @ManyToOne
    @JoinColumn(name = "giverTeamId")
    private Team giverTeam;

    protected ResponseGiver() {
        // required by Hibernate
    }

    public ResponseGiver(User giverUser) {
        setGiverUser(giverUser);
    }

    public ResponseGiver(Team giverTeam) {
        setGiverTeam(giverTeam);
    }

    public UUID getGiverUserId() {
        return giverUserId;
    }

    public User getGiverUser() {
        return giverUser;
    }

    /**
     * Sets the giver user.
     */
    public void setGiverUser(User giverUser) {
        this.giverUser = giverUser;
        this.giverUserId = giverUser == null ? null : giverUser.getId();
        if (giverUser != null) {
            this.giverTeam = null;
            this.giverTeamId = null;
        }
    }

    public UUID getGiverTeamId() {
        return giverTeamId;
    }

    public Team getGiverTeam() {
        return giverTeam;
    }

    /**
     * Sets the giver team.
     */
    public void setGiverTeam(Team giverTeam) {
        this.giverTeam = giverTeam;
        this.giverTeamId = giverTeam == null ? null : giverTeam.getId();
        if (giverTeam != null) {
            this.giverUser = null;
            this.giverUserId = null;
        }
    }

    public boolean isGiverUser() {
        return getGiverUserId() != null;
    }

    public boolean isGiverTeam() {
        return getGiverTeamId() != null;
    }

    /**
     * Gets the giver identifier: team name for team givers, user email for user givers.
     */
    public String getIdentifier() {
        if (giverTeam != null) {
            return giverTeam.getName();
        }
        if (giverUser != null) {
            return giverUser.getEmail();
        }
        return "Deleted User";
    }

    /**
     * Gets the giver display name: team name for team givers, user name for user givers.
     */
    public String getDisplayName() {
        if (giverTeam != null) {
            return giverTeam.getName();
        }
        if (giverUser != null) {
            return giverUser.getName();
        }
        return "Deleted User";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseGiver)) {
            return false;
        }
        ResponseGiver other = (ResponseGiver) o;
        return Objects.equals(getGiverUserId(), other.getGiverUserId())
                && Objects.equals(getGiverTeamId(), other.getGiverTeamId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGiverTeamId(), getGiverUserId());
    }

    @Override
    public String toString() {
        if (isGiverTeam()) {
            return "ResponseGiver [giverTeamId=" + getGiverTeamId() + "]";
        } else {
            return "ResponseGiver [giverUserId=" + getGiverUserId() + "]";
        }
    }
}

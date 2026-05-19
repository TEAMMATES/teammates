package teammates.storage.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import teammates.common.util.Const;

/**
 * Embeddable value object that identifies a giver (or editor) of a feedback response or comment.
 */
@Embeddable
public class ResponseGiver {
    @Column(insertable = false, updatable = false)
    private UUID giverUserId;

    @ManyToOne
    @JoinColumn(name = "giverUserId")
    private User giverUser;

    @Column(insertable = false, updatable = false)
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

    public boolean isGiverInstructor() {
        return getGiverUser() instanceof Instructor;
    }

    public boolean isGiverStudent() {
        return getGiverUser() instanceof Student;
    }

    public boolean isGiverTeam() {
        return getGiverTeamId() != null;
    }

    /**
     * Gets the team name of the giver. If the giver is an instructor, returns the instructor team name.
     */
    public String getTeamName() {
        if (isGiverTeam()) {
            return giverTeam != null ? giverTeam.getName() : Const.UNKNOWN_TEAM;
        }
        if (giverUser instanceof Student student) {
            return student.getTeamName();
        }
        if (giverUser instanceof Instructor) {
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        }
        return Const.UNKNOWN_TEAM;
    }

    /**
     * Gets the section name of the giver. If the giver is an instructor, returns the default section name.
     */
    public String getSectionName() {
        if (isGiverTeam()) {
            return giverTeam != null ? giverTeam.getSection().getName() : Const.UNKNOWN_SECTION;
        }
        if (giverUser instanceof Student student) {
            return student.getSectionName();
        }
        if (giverUser instanceof Instructor) {
            return Const.DEFAULT_SECTION;
        }
        return Const.UNKNOWN_SECTION;
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
        return Const.UNKNOWN_USER;
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
        return Const.UNKNOWN_USER;
    }

    /**
     * Formats the giver type as a singular noun.
     */
    public String toSingularFormString() {
        if (isGiverTeam()) {
            return "team";
        }
        if (giverUser instanceof Student) {
            return "student";
        }
        return "instructor";
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

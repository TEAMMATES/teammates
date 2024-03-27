package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Represents a Team to Student Map.
 */
@Entity
@Table(name = "TeamToStudentMaps")
public class TeamToStudentMap extends BaseEntity {
    @Id
    private UUID id;

    @ManyToMany
    @JoinColumn(name = "teamId")
    private Team team;

    @ManyToMany
    @JoinColumn(name = "studentId")
    private Student student;

    @UpdateTimestamp
    private Instant updatedAt;

    public UUID getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "TeamToStudentMap [id=" + id + ", team=" + team.getId() + ", student=" + student.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            TeamToStudentMap otherTeamToStudentMap = (TeamToStudentMap) other;
            return Objects.equals(this.id, otherTeamToStudentMap.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}

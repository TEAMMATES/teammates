package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a Section.
 */
@Entity
@Table(name = "Sections")
public class Section extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Team> teams;

    @UpdateTimestamp
    private Instant updatedAt;

    protected Section() {
        // required by hibernate
    }

    public Section(Course course, String name) {
        this.setId(UUID.randomUUID());
        this.setCourse(course);
        this.setName(name);
        this.setTeams(new ArrayList<>());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Section otherSection = (Section) other;
            return Objects.equals(this.getId(), otherSection.getId());
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("section name", name), errors);

        return errors;
    }

    /**
     * Adds a team to the section.
     */
    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Section [id=" + id + ", course=" + course + ", name=" + name + ", teams=" + teams
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

}

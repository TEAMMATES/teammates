package teammates.logic.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;

/**
 * Represents a Team.
 */
@Entity
@Table(name = "Teams", uniqueConstraints = {
        @UniqueConstraint(name = "Unique name and sectionId", columnNames = { "sectionId", "name" })
})
public class Team extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sectionId")
    private Section section;

    @Column(insertable = false, updatable = false)
    private UUID sectionId;

    @OneToMany(mappedBy = "team")
    private List<Student> users;

    @Column(nullable = false)
    private String name;

    @UpdateTimestamp
    private Instant updatedAt;

    protected Team() {
        // required by hibernate
    }

    public Team(Section section, String name) {
        this.setId(UUID.randomUUID());
        this.setSection(section);
        this.setName(name);
        this.setUsers(new ArrayList<>());
    }

    /**
     * Adds a user to the team.
     */
    public void addUser(Student student) {
        this.users.add(student);
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
            Team otherTeam = (Team) other;
            return Objects.equals(this.getId(), otherTeam.getId());
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("team name", name), errors);

        return errors;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    /**
     * Sets the section of the team.
     */
    public void setSection(Section section) {
        this.section = section;
        this.sectionId = section == null ? null : section.getId();
    }

    public List<Student> getUsers() {
        return users;
    }

    public void setUsers(List<Student> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Team [id=" + id + ", users=" + users + ", name=" + name
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

}

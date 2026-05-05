package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private Set<Student> users = new HashSet<>();

    @Column(nullable = false)
    private String name;

    @UpdateTimestamp
    private Instant updatedAt;

    protected Team() {
        // required by hibernate
    }

    public Team(String name) {
        this.setId(UUID.randomUUID());
        this.setName(name);
    }

    /**
     * Adds a user to the team.
     */
    public void addUser(Student student) {
        this.users.add(student);
        student.setTeam(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Team other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
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

    public Set<Student> getUsers() {
        return users;
    }

    public void setUsers(Set<Student> users) {
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

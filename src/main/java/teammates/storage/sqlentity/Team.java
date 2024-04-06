package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a Team.
 */
@Entity
@Table(name = "Teams")
public class Team extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sectionId")
    private Section section;

    @OneToMany(mappedBy = "team")
    private List<User> users;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "TeamToStudentMaps",
            joinColumns = { @JoinColumn(name = "teamId") },
            inverseJoinColumns = { @JoinColumn(name = "studentId") })
    private Set<Student> students = new HashSet<>();

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

    public void setSection(Section section) {
        this.section = section;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
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
        return "Team [id=" + id + ", users=" + users + ", students=" + students + ", name=" + name
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

}

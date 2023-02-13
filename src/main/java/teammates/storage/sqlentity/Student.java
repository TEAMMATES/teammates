package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Represents a Student entity.
 */
@Entity
@Table(name = "Students")
public class Student extends User {
    // Cascade?
    // from hibernate docs: seems like we can omit this
    // https://docs.jboss.org/hibernate/orm/6.1/userguide/html_single/Hibernate_User_Guide.html#entity-inheritance-joined-table
    @OneToOne(mappedBy = "id")
    @Column(nullable = false)
    private int id;

    // Cascade?
    @OneToOne
    @JoinColumn(name = "teamId")
    private Team team;

    @Column(nullable = false)
    private String comments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected Student() {
        // required by Hibernate
    }

    public Student(StudentBuilder builder) {
        this.setId(builder.id);
        this.setTeam(builder.team);
        this.setComments(builder.comments);

        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }

        this.setUpdatedAt(updatedAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", team=" + team + ", comments=" + comments
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // Student Id uniquely identifies a Student
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            Student otherStudent = (Student) obj;

            return Objects.equals(this.id, otherStudent.id);
        } else {
            return false;
        }
    }

    /**
     * Builder for Student
     */
    public static class StudentBuilder {
        private int id;
        private Team team;
        private String comments;

        public StudentBuilder(int id) {
            this.id = id;
        }

        public StudentBuilder withTeam(Team team) {
            this.team = team;
            return this;
        }

        public StudentBuilder withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}

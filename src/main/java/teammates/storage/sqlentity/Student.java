package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.Table;

/**
 * Represents a Student entity.
 */
@Entity
@Table(name = "Students")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "teamId"),
    @PrimaryKeyJoinColumn(name = "userId"),
})
public class Student { // TODO: extends User
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // to cascade?
    @OneToOne(mappedBy = "id")
    @Column(nullable = false)
    private int teamId;

    // to cascade?
    // from hibernate docs: seems like we can omit this
    // https://docs.jboss.org/hibernate/orm/6.1/userguide/html_single/Hibernate_User_Guide.html#entity-inheritance-joined-table
    @OneToOne(mappedBy = "id")
    @Column(nullable = false)
    private int userId;

    @Column
    private String comments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected Student() {
        // required by Hibernate
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Student(StudentBuilder builder) {
        this.setId(builder.id);
        this.setTeamId(builder.teamId);
        this.setUserId(builder.userId);
        this.setComments(builder.comments);

        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }

        this.setUpdatedAt(updatedAt);
    }
    
    @Override
    public String toString() {
        return "Student [id=" + id + ", teamId=" + teamId + ", userId=" + userId
                + ", comments=" + comments + ", createdAt=" + createdAt
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
        private int teamId;
        private int userId;
        private String comments;

        public StudentBuilder(int id) {
            this.id = id;
        }

        public StudentBuilder withTeamId(int teamId) {
            this.teamId = teamId;
            return this;
        }

        public StudentBuilder withUserId(int userId) {
            this.userId = userId;
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

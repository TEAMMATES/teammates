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
        return "Student [id=" + super.getId() + ", team=" + super.getTeam() + ", comments=" + comments
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // Student Id uniquely identifies a Student
        return super.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            Student otherStudent = (Student) obj;

            return Objects.equals(super.getId(), otherStudent.getId());
        } else {
            return false;
        }
    }
}

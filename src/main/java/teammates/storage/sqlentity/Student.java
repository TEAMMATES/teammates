package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a Student entity.
 */
@Entity
@Table(name = "Students")
public class Student extends User {
    @Column(nullable = false)
    private String comments;

    protected Student() {
        // required by Hibernate
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Student [id=" + super.getId() + ", comments=" + comments
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
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

    @Override
    public void sanitizeForSaving() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getInvalidityInfo() {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }
}

package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

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
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Student otherStudent = (Student) other;
            return Objects.equals(super.getCourse(), otherStudent.getCourse())
                    && Objects.equals(super.getName(), otherStudent.getName())
                    && Objects.equals(super.getEmail(), otherStudent.getEmail())
                    && Objects.equals(super.getAccount().getGoogleId(),
                            otherStudent.getAccount().getGoogleId())
                    && Objects.equals(this.comments, otherStudent.comments);
            // && Objects.equals(this.team, otherStudent.team)
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        comments = SanitizationHelper.sanitizeTextField(comments);
    }

    @Override
    public List<String> getInvalidityInfo() {
        assert comments != null;

        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(super.getCourse().getId()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(super.getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForStudentRoleComments(comments), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(super.getName()), errors);

        return errors;
    }
}

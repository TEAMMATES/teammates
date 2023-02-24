package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a Student.
 */
@Entity
@Table(name = "Students")
public class Student extends User {
    @Column(nullable = false)
    private String comments;

    protected Student() {
        // required by Hibernate
    }

    public Student(Course course, Team team, String name, String email, String comments) {
        super(course, team, name, email);
        this.setComments(comments);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = SanitizationHelper.sanitizeTextField(comments);
    }

    @Override
    public String toString() {
        return "Student [id=" + super.getId() + ", comments=" + comments
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
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

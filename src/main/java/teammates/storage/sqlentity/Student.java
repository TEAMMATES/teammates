package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

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

    public Student(Course course, String name, String email, String comments) {
        super(course, name, email);
        this.setComments(comments);
    }

    public Student(Course course, String name, String email, String comments, Team team) {
        super(course, name, email);
        this.setComments(comments);
        this.setTeam(team);
    }

    /**
     * Gets the comments of the student.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments of the student.
     */
    public void setComments(String comments) {
        this.comments = SanitizationHelper.sanitizeTextField(comments);
    }

    @Override
    public String getTeamName() {
        return getTeam() != null ? getTeam().getName() : null;
    }

    @Override
    public String getSectionName() {
        return getTeam() != null ? getTeam().getSection() != null ? getTeam().getSection().getName() : null : null;
    }

    @Override
    public Section getSection() {
        return getTeam() != null ? getTeam().getSection() : null;
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

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(super.getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForStudentRoleComments(comments), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(super.getName()), errors);

        return errors;
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getRegKey())
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
    }
}

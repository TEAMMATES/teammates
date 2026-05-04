package teammates.storage.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import teammates.common.datatransfer.UserType;
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

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID teamId;

    @ManyToOne
    @JoinColumn(name = "teamId", nullable = false)
    private Team team;

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

    @Override
    public UserType getUserType() {
        return UserType.STUDENT;
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

    public Team getTeam() {
        return this.team;
    }

    public UUID getTeamId() {
        return this.teamId;
    }

    /**
     * Sets the team of the student.
     */
    public void setTeam(Team team) {
        this.team = team;
        this.teamId = team == null ? null : team.getId();
    }

    public String getTeamName() {
        return getTeam().getName();
    }

    public String getSectionName() {
        return getSection().getName();
    }

    public Section getSection() {
        return getTeam().getSection();
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

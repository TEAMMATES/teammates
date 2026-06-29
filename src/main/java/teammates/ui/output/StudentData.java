package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.entity.Student;

/**
 * The API output format of {@link Student}.
 */
public class StudentData implements ApiOutput {
    private final UUID userId;
    @Nullable
    private UUID accountId;

    private final String email;
    private final String courseId;

    private final String name;
    @Nullable
    private String comments;
    @Nullable
    private JoinState joinState;

    private final UUID teamId;
    private final String teamName;
    private final UUID sectionId;
    private final String sectionName;
    private String institute;
    private String courseName;

    @JsonCreator
    private StudentData(UUID userId, String email, String courseId, String name,
            UUID teamId, String teamName, UUID sectionId, String sectionName,
            String institute, String courseName) {
        this.userId = userId;
        this.email = email;
        this.courseId = courseId;
        this.name = name;
        this.teamId = teamId;
        this.teamName = teamName;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.institute = institute;
        this.courseName = courseName;
    }

    public StudentData(Student student) {
        this.userId = student.getId();
        this.accountId = student.getAccountId();
        this.email = student.getEmail();
        this.courseId = student.getCourseId();
        this.name = student.getName();
        this.joinState = student.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
        this.comments = student.getComments();
        this.teamId = student.getTeamId();
        this.teamName = student.getTeamName();
        this.sectionId = student.getSectionId();
        this.sectionName = student.getSectionName();
        this.institute = student.getCourse().getInstitute().getName();
        this.courseName = student.getCourse().getName();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public String getTeamName() {
        return teamName;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public String getInstitute() {
        return institute;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        setComments(null);
        setJoinState(null);
    }

    /**
     * Adds additional information for admin.
     * @param accountId The accountId of the student
     */
    public void addAdditionalInformationForAdmin(UUID accountId) {
        this.setAccountId(accountId);
    }

    public String getCourseName() {
        return courseName;
    }
}

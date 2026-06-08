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
    private String googleId;
    @Nullable
    private String comments;
    @Nullable
    private String key;
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
        this.institute = student.getCourse().getInstitute();
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

    public String getGoogleId() {
        return googleId;
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

    public String getKey() {
        return key;
    }

    public String getInstitute() {
        return institute;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public void setKey(String key) {
        this.key = key;
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
     * Adds additional information only for search result for admin.
     * @param key The registration key
     * @param googleId The googleId of the student
     * @param accountId The accountId of the student
     */
    public void addAdditionalInformationForAdminSearch(String key, String googleId, UUID accountId) {
        this.setKey(key);
        this.setGoogleId(googleId);
        this.setAccountId(accountId);
    }

    public String getCourseName() {
        return courseName;
    }
}

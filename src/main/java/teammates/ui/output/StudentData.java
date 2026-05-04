package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.entity.Student;

/**
 * The API output format of {@link Student}.
 */
public class StudentData extends ApiOutput {
    private final UUID userId;

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
    private String institute;
    @Nullable
    private JoinState joinState;

    private final String teamName;
    private final String sectionName;

    @JsonCreator
    private StudentData(UUID userId, String email, String courseId, String name,
            String teamName, String sectionName) {
        this.userId = userId;
        this.email = email;
        this.courseId = courseId;
        this.name = name;
        this.teamName = teamName;
        this.sectionName = sectionName;
    }

    public StudentData(Student student) {
        this.userId = student.getId();
        this.email = student.getEmail();
        this.courseId = student.getCourseId();
        this.name = student.getName();
        this.joinState = student.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
        this.comments = student.getComments();
        this.teamName = student.getTeamName();
        this.sectionName = student.getSectionName();
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

    public String getSectionName() {
        return sectionName;
    }

    public String getKey() {
        return key;
    }

    public String getInstitute() {
        return institute;
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
     * @param institute The institute of the student
     * @param googleId The googleId of the student
     */
    public void addAdditionalInformationForAdminSearch(String key, String institute, String googleId) {
        this.setKey(key);
        this.setInstitute(institute);
        this.setGoogleId(googleId);
    }
}

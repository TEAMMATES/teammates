package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.storage.entity.Instructor;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    private final UUID userId;
    @Nullable
    private String googleId;
    private final String courseId;
    private final String email;
    @Nullable
    private Boolean isDisplayedToStudents;
    @Nullable
    private String displayedToStudentsAs;
    private final String name;
    @Nullable
    private InstructorPermissionRole role;
    private JoinState joinState;
    @Nullable
    private String key;
    private String institute;
    private String courseName;

    @JsonCreator
    private InstructorData(UUID userId, String courseId, String email, String name,
                            String institute, String courseName) {
        this.userId = userId;
        this.courseId = courseId;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.courseName = courseName;
    }

    public InstructorData(Instructor instructor) {
        this.userId = instructor.getId();
        this.courseId = instructor.getCourseId();
        this.email = instructor.getEmail();
        this.role = instructor.getRole();
        this.isDisplayedToStudents = instructor.isDisplayedToStudents();
        this.displayedToStudentsAs = instructor.getDisplayName();
        this.name = instructor.getName();
        this.joinState = instructor.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED;
        this.institute = instructor.getCourse().getInstitute();
        this.courseName = instructor.getCourse().getName();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
    }

    public InstructorPermissionRole getRole() {
        return role;
    }

    public void setRole(InstructorPermissionRole role) {
        this.role = role;
    }

    public Boolean getIsDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public void setIsDisplayedToStudents(Boolean displayedToStudents) {
        isDisplayedToStudents = displayedToStudents;
    }

    public String getDisplayedToStudentsAs() {
        return displayedToStudentsAs;
    }

    public void setDisplayedToStudentsAs(String displayedToStudentsAs) {
        this.displayedToStudentsAs = displayedToStudentsAs;
    }

    public String getName() {
        return name;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCourseName() {
        return courseName;
    }

    /**
     * Adds additional attributes only for search result for admin.
     *
     * @param key Registration key
     * @param googleId Google ID of the instructor
     */
    public void addAdditionalInformationForAdminSearch(String key, String googleId) {
        setKey(key);
        setGoogleId(googleId);
    }
}

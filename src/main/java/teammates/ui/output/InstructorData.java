package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.storage.entity.Instructor;

/**
 * The API output format of an instructor.
 */
public class InstructorData implements ApiOutput {
    private final UUID userId;
    @Nullable
    private UUID accountId;
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
        this.accountId = instructor.getAccountId();
        this.courseId = instructor.getCourseId();
        this.email = instructor.getEmail();
        this.role = instructor.getRole();
        this.isDisplayedToStudents = instructor.isDisplayedToStudents();
        this.displayedToStudentsAs = instructor.getDisplayName();
        this.name = instructor.getName();
        this.joinState = instructor.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED;
        this.institute = instructor.getCourse().getInstitute().getName();
        this.courseName = instructor.getCourse().getName();
    }

    public UUID getUserId() {
        return userId;
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

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Hides some attributes from students.
     */
    public void hideInformationForStudent() {
        setAccountId(null);
        setJoinState(null);
        setIsDisplayedToStudents(null);
        setRole(null);
    }

}

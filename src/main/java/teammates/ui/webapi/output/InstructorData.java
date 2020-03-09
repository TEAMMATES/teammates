package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    private String googleId;
    private final String courseId;
    private final String email;
    private Boolean isDisplayedToStudents;
    private final String displayedToStudentsAs;
    private final String name;

    private InstructorPermissionRole role;
    private JoinState joinState;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.googleId = instructorAttributes.getGoogleId();
        this.courseId = instructorAttributes.getCourseId();
        this.email = instructorAttributes.getEmail();
        this.role = InstructorPermissionRole.getEnum(instructorAttributes.getRole());
        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents();
        this.displayedToStudentsAs = instructorAttributes.getDisplayedName();
        this.name = instructorAttributes.getName();

        this.joinState = instructorAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
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

    public String getName() {
        return name;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }
}

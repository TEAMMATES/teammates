package teammates.ui.webapi.output;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of {@link InstructorAttributes}.
 */
public class InstructorData extends ApiOutput {
    private String googleId;
    private final String name;
    private final String courseId;
    private final String email;
    private String role;
    private Boolean isDisplayedToStudents;
    private final String displayedName;
    private InstructorPrivileges privileges;

    private JoinState joinState;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.googleId = instructorAttributes.getGoogleId();
        this.name = instructorAttributes.getName();
        this.courseId = instructorAttributes.getCourseId();
        this.email = instructorAttributes.getEmail();
        this.role = instructorAttributes.getRole();
        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents();
        this.displayedName = instructorAttributes.getDisplayedName();
        this.privileges = instructorAttributes.privileges;
        this.joinState = instructorAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getName() {
        return name;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Boolean getIsDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public InstructorPrivileges getPrivileges() {
        return privileges;
    }

    public void setIsDisplayedToStudents(Boolean displayedToStudents) {
        isDisplayedToStudents = displayedToStudents;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }
}

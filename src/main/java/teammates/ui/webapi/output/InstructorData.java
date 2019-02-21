package teammates.ui.webapi.output;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of {@link InstructorAttributes}.
 */
public class InstructorData extends ApiOutput {
    private String googleId;
    public String courseId;
    public String name;
    public String email;
    private String role;
    private String displayedName;
    private boolean isDisplayedToStudents;
    private InstructorPrivileges privileges;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.googleId = instructorAttributes.getGoogleId();
        this.courseId = instructorAttributes.getCourseId();
        this.name = instructorAttributes.getName();
        this.email = instructorAttributes.getEmail();
        this.role = instructorAttributes.getRole();
        this.displayedName = instructorAttributes.getDisplayedName();
        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents;
        this.privileges = instructorAttributes.privileges;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public boolean getIsDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public InstructorPrivileges getPrivileges() {
        return privileges;
    }
}

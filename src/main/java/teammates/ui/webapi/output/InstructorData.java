package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    private final String courseId;
    private final String email;

    private Boolean isDisplayedToStudents;
    private final String displayedToStudentsAs;
    private final String name;

    private JoinState joinState;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.courseId = instructorAttributes.getCourseId();
        this.email = instructorAttributes.getEmail();

        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents();
        this.displayedToStudentsAs = instructorAttributes.getDisplayedName();
        this.name = instructorAttributes.getName();

        this.joinState = instructorAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
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

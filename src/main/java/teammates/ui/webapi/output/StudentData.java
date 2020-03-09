package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The API output format of {@link StudentAttributes}.
 */
public class StudentData extends ApiOutput {

    private final String email;
    private final String courseId;

    private final String name;
    private final String lastName;

    private String comments;

    private JoinState joinState;

    private final String teamName;
    private final String sectionName;

    public StudentData(StudentAttributes studentAttributes) {
        this.email = studentAttributes.getEmail();
        this.courseId = studentAttributes.getCourse();
        this.name = studentAttributes.getName();
        this.lastName = studentAttributes.getLastName();
        this.joinState = studentAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
        this.comments = studentAttributes.getComments();
        this.teamName = studentAttributes.getTeam();
        this.sectionName = studentAttributes.getSection();
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

    public String getLastName() {
        return lastName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getSectionName() {
        return sectionName;
    }
}

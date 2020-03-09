package teammates.ui.webapi.output;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents a Student search result.
 */
public class SearchStudentData extends CommonSearchUserData {
    private final String team;
    private final String section;

    @Nullable
    private final String comments;

    public SearchStudentData(StudentAttributes studentAttributes) {
        super(studentAttributes.getName(), studentAttributes.getEmail(), studentAttributes.getCourse(),
                studentAttributes.getGoogleId(), studentAttributes.isRegistered());
        this.comments = studentAttributes.getComments();
        this.team = studentAttributes.getTeam();
        this.section = studentAttributes.getSection();
    }

    public String getComments() {
        return comments;
    }

    public String getTeam() {
        return team;
    }

    public String getSection() {
        return section;
    }
}

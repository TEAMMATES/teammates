package teammates.ui.request;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.util.Const;

/**
 * The request for enrolling a student.
 */
public class StudentEnrollRequest extends BasicRequest {

    private String name;
    private String email;
    private String team;
    private String section;
    private String comments;

    @JsonCreator
    public StudentEnrollRequest(String name, String email, String team, String section, String comments) {
        this.name = name;
        this.email = email;
        this.team = team;
        this.section = section;
        this.comments = comments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(name != null && !name.isEmpty(), "Student name cannot be empty");
        assertTrue(email != null && !email.isEmpty(), "Student email cannot be empty");
        assertTrue(team != null && !team.isEmpty(), "Team cannot be empty");
        assertTrue(section != null, "Section cannot be null");
        assertTrue(comments != null, "Comments cannot be null");
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email == null ? null : this.email.toLowerCase(Locale.ROOT);
    }

    public String getTeam() {
        return this.team;
    }

    public String getSection() {
        return this.section.isEmpty() ? Const.DEFAULT_SECTION : this.section;
    }

    public String getComments() {
        return this.comments;
    }
}

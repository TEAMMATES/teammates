package teammates.ui.webapi.output;

/**
 * The API output format of feedback session student response status.
 */
public class FeedbackSessionStudentResponseData extends ApiOutput {
    private final String email;
    private final String name;
    private final String sectionName;
    private final String teamName;
    private final boolean responseStatus;

    FeedbackSessionStudentResponseData(String email, String name, String sectionName, String teamName,
                                       boolean responseStatus) {
        this.email = email;
        this.name = name;
        this.sectionName = sectionName;
        this.teamName = teamName;
        this.responseStatus = responseStatus;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean getResponseStatus() {
        return responseStatus;
    }

}

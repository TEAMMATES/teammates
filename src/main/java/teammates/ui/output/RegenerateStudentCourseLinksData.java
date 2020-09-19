package teammates.ui.output;

/**
 * The API output format for the regenerate student's course links request.
 */
public class RegenerateStudentCourseLinksData extends ApiOutput {
    private final String message;
    private final String newRegistrationKey;

    public RegenerateStudentCourseLinksData(String msg, String key) {
        message = msg;
        newRegistrationKey = key;
    }

    public String getMessage() {
        return message;
    }

    public String getNewRegistrationKey() {
        return newRegistrationKey;
    }

}

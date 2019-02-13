package teammates.ui.webapi.request;

/**
 * The save request of a list of students from feedback session.
 */
public class FeedbackSessionStudentSaveRequest extends BasicRequest {
    private String[] usersToRemind;

    public String[] getUsersToRemind() {
        return usersToRemind;
    }

    @Override
    public void validate() {

        assertTrue(usersToRemind != null, "List of users to remind cannot be null");
        assertTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }
}

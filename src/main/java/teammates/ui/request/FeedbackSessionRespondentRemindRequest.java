package teammates.ui.request;

/**
 * Sends a reminder email each to a list of respondents from a feedback session.
 */
public class FeedbackSessionRespondentRemindRequest extends BasicRequest {
    private String[] usersToRemind;

    public String[] getUsersToRemind() {
        return usersToRemind;
    }

    public void setUsersToRemind(String[] usersToRemind) {
        this.usersToRemind = usersToRemind;
    }

    @Override
    public void validate() {

        assertTrue(usersToRemind != null, "List of users to remind cannot be null");
        assertTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }
}

package teammates.ui.request;

/**
 * Sends a reminder email each to a list of respondents from a feedback session.
 */
public class FeedbackSessionRespondentRemindRequest extends BasicRequest {
    private String[] usersToRemind;
    private boolean sendCopyToInstructor;

    public String[] getUsersToRemind() {
        return usersToRemind;
    }

    public void setUsersToRemind(String[] usersToRemind) {
        this.usersToRemind = usersToRemind;
    }

    public boolean getSendCopyToInstructor() {
        return sendCopyToInstructor;
    }

    public void setSendCopyToInstructor(boolean sendCopyToInstructor) {
        this.sendCopyToInstructor = sendCopyToInstructor;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {

        assertTrue(usersToRemind != null, "List of users to remind cannot be null");
        assertTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }
}

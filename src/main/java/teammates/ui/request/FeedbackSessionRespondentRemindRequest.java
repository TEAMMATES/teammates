package teammates.ui.request;

/**
 * Sends a reminder email each to a list of respondents from a feedback session.
 */
public class FeedbackSessionRespondentRemindRequest extends BasicRequest {
    private String[] usersToRemind;
    private boolean isSendingCopyToInstructor;

    public String[] getUsersToRemind() {
        return usersToRemind;
    }

    public void setUsersToRemind(String[] usersToRemind) {
        this.usersToRemind = usersToRemind;
    }

    public boolean getIsSendingCopyToInstructor() {
        return isSendingCopyToInstructor;
    }

    public void setIsSendingCopyToInstructor(boolean isSendingCopyToInstructor) {
        this.isSendingCopyToInstructor = isSendingCopyToInstructor;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {

        assertTrue(usersToRemind != null, "List of users to remind cannot be null");
        assertTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }
}

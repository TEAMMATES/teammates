package teammates.ui.request;

import java.util.UUID;

/**
 * Sends a reminder email each to a list of respondents from a feedback session.
 */
public class FeedbackSessionRespondentRemindRequest extends BasicRequest {
    private UUID[] usersToRemind;
    private boolean isSendingCopyToInstructor;

    public UUID[] getUsersToRemind() {
        return usersToRemind;
    }

    public void setUsersToRemind(UUID[] usersToRemind) {
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

package teammates.ui.request;

/**
 * The request of specifying reminder emails to be sent.
 */
public class FeedbackSessionRemindRequest extends BasicRequest {

    private final String courseId;
    private final String feedbackSessionName;
    private final String requestingInstructorId;
    private final String[] usersToRemind;
    private final boolean isSendingCopyToInstructor;

    public FeedbackSessionRemindRequest(String courseId, String feedbackSessionName, String requestingInstructorId,
                                        String[] usersToRemind, boolean isSendingCopyToInstructor) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.requestingInstructorId = requestingInstructorId;
        this.usersToRemind = usersToRemind;
        this.isSendingCopyToInstructor = isSendingCopyToInstructor;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getRequestingInstructorId() {
        return requestingInstructorId;
    }

    public String[] getUsersToRemind() {
        return usersToRemind;
    }

    public boolean getIsSendingCopyToInstructor() {
        return isSendingCopyToInstructor;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(courseId != null, "Course ID cannot be null");
        assertTrue(feedbackSessionName != null, "Feedback session name cannot be null");
        assertTrue(usersToRemind != null, "List of users to remind cannot be null");
        assertTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }

}

package teammates.ui.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request of specifying reminder emails to be sent.
 */
public class FeedbackSessionRemindRequest extends BasicRequest {

    private final String courseId;
    private final String feedbackSessionName;
    private final String requestingInstructorId;
    private final String[] usersToRemind;
    private final boolean isSendingCopyToInstructor;

    @JsonCreator
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
        validateTrue(courseId != null, "Course ID cannot be null");
        validateTrue(feedbackSessionName != null, "Feedback session name cannot be null");
        validateTrue(usersToRemind != null, "List of users to remind cannot be null");
        validateTrue(usersToRemind.length != 0, "List of users to remind cannot be empty");
    }

}

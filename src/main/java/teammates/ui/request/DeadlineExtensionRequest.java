package teammates.ui.request;

import java.util.List;
import java.util.Map;

/**
 * The request specifying deadline extensions to create, update or revoke.
 */
public class DeadlineExtensionRequest extends BasicRequest {

    private String courseId;
    private String feedbackSessionName;
    private boolean notifyUsers;
    private Map<String, Long> studentExtensionsToModify;
    private List<String> studentExtensionsToRevoke;
    private Map<String, Long> instructorExtensionsToModify;
    private List<String> instructorExtensionsToRevoke;

    public DeadlineExtensionRequest(String courseId, String feedbackSessionName, boolean notifyUsers,
            Map<String, Long> studentExtensionsToModify, List<String> studentExtensionsToRevoke,
            Map<String, Long> instructorExtensionsToModify, List<String> instructorExtensionsToRevoke) {
        assert studentExtensionsToModify != null;
        assert studentExtensionsToRevoke != null;
        assert instructorExtensionsToModify != null;
        assert instructorExtensionsToRevoke != null;

        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.notifyUsers = notifyUsers;
        this.studentExtensionsToModify = studentExtensionsToModify;
        this.studentExtensionsToRevoke = studentExtensionsToRevoke;
        this.instructorExtensionsToModify = instructorExtensionsToModify;
        this.instructorExtensionsToRevoke = instructorExtensionsToRevoke;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean getNotifyUsers() {
        return notifyUsers;
    }

    public Map<String, Long> getStudentExtensionsToModify() {
        return studentExtensionsToModify;
    }

    public List<String> getStudentExtensionsToRevoke() {
        return studentExtensionsToRevoke;
    }

    public Map<String, Long> getInstructorExtensionsToModify() {
        return instructorExtensionsToModify;
    }

    public List<String> getInstructorExtensionsToRevoke() {
        return instructorExtensionsToRevoke;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(courseId != null, "Course ID cannot be null");
        assertTrue(feedbackSessionName != null, "Feedback session name cannot be null");
    }

}

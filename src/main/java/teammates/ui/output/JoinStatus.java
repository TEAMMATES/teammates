package teammates.ui.output;

/**
 * The join status of a course.
 */
public class JoinStatus extends ApiOutput {

    private final boolean hasJoined;
    private final String userId;

    public JoinStatus(boolean hasJoined, String userId) {
        this.hasJoined = hasJoined;
        this.userId = userId;
    }

    public boolean getHasJoined() {
        return hasJoined;
    }

    public String getUserId() {
        return userId;
    }

}

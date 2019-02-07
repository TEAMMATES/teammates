package teammates.common.exception;

@SuppressWarnings("serial")
public class FeedbackSessionNotVisibleException extends UnauthorizedAccessException {

    private final String startTimeString;

    public FeedbackSessionNotVisibleException(String message, String openingDateString) {
        super(message);
        this.startTimeString = openingDateString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }
}

package teammates.common.exception;

@SuppressWarnings("serial")
public class FeedbackSessionNotVisibleException extends UnauthorizedAccessException {
    
    private String startTimeString;
    
    public FeedbackSessionNotVisibleException(final String openingDateString){
        super();
        this.startTimeString = openingDateString;
    }

    public FeedbackSessionNotVisibleException(final String message, final String openingDateString) {
        super(message);
        this.startTimeString = openingDateString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }
}

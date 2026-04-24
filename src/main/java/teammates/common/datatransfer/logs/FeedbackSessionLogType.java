package teammates.common.datatransfer.logs;

/**
 * Constant values for the different feedback session-related log types.
 */
public enum FeedbackSessionLogType {
    // CHECKSTYLE.OFF:JavadocVariable
    ACCESS("access"),
    SUBMISSION("submission"),
    VIEW_RESULT("view result");
    // CHECKSTYLE.ON:JavadocVariable

    private final String label;

    FeedbackSessionLogType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}

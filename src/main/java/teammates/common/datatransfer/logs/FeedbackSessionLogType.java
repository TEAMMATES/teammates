package teammates.common.datatransfer.logs;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Constant values for the different feedback session-related log types.
 */
public enum FeedbackSessionLogType {
    // CHECKSTYLE.OFF:JavadocVariable
    ACCESS("access"),
    SUBMISSION("submission"),
    VIEW_RESULT("view result");
    // CHECKSTYLE.ON:JavadocVariable

    @JsonValue
    private final String label;

    FeedbackSessionLogType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Returns the enum value of a log type given its label.
     */
    public static FeedbackSessionLogType valueOfLabel(String label) {
        for (FeedbackSessionLogType logType : values()) {
            if (logType.label.equals(label)) {
                return logType;
            }
        }
        return null;
    }

}

package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const;

/**
 * Constant values for all the different log types.
 */
public enum LogType {
    // CHECKSTYLE.OFF:JavadocVariable
    FEEDBACK_SESSION_ACCESS(Const.FeedbackSessionLogTypes.ACCESS),
    FEEDBACK_SESSION_SUBMISSION(Const.FeedbackSessionLogTypes.SUBMISSION),
    FEEDBACK_SESSION_VIEW_RESULT(Const.FeedbackSessionLogTypes.VIEW_RESULT);
    // CHECKSTYLE.ON:JavadocVariable

    @JsonValue
    private final String label;

    LogType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Returns the enum value of a log type given its label.
     */
    public static LogType valueOfLabel(String label) {
        for (LogType logType : values()) {
            if (logType.label.equals(label)) {
                return logType;
            }
        }
        return null;
    }

}

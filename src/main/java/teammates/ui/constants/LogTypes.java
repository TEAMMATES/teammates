package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const;

/**
 * Constant values for all the different log types.
 */
public enum LogTypes {
    // CHECKSTYLE.OFF:JavadocVariable
    FEEDBACK_SESSION_ACCESS(Const.FeedbackSessionLogTypes.ACCESS),
    FEEDBACK_SESSION_SUBMISSION(Const.FeedbackSessionLogTypes.SUBMISSION);
    // CHECKSTYLE.ON:JavadocVariable

    @JsonValue
    private final String value;

    LogTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

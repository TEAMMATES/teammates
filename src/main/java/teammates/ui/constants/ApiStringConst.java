package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;

/**
 * Special constants used by the back-end.
 */
public enum ApiStringConst {
    // CHECKSTYLE.OFF:JavadocVariable
    DEFAULT_SECTION(Const.DEFAULT_SECTION),
    EMAIL_REGEX(escapeRegex(FieldValidator.REGEX_EMAIL)),
    USER_TEAM_FOR_INSTRUCTOR(Const.USER_TEAM_FOR_INSTRUCTOR);
    // CHECKSTYLE.ON:JavadocVariable

    private final Object value;

    ApiStringConst(Object value) {
        this.value = value;
    }

    @JsonValue
    public Object getValue() {
        return value;
    }

    /**
     * Escape regex pattern strings to ensure the pattern remains valid when converted to JS.
     */
    private static String escapeRegex(String regexStr) {
        String escapedRegexStr = regexStr;
        // Double escape backslashes
        escapedRegexStr = escapedRegexStr.replace("\\", "\\\\");
        // Replace possessive zero or more times quantifier *+ that the email pattern uses
        // with greedy zero or more times quantifier *
        // as possessive quantifiers are not supported in JavaScript
        escapedRegexStr = escapedRegexStr.replace("*+", "*");
        return escapedRegexStr;
    }

}

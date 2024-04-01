package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.FieldValidator;

/**
 * Special constants used by the back-end.
 */
public enum ApiStringConst {
    // CHECKSTYLE.OFF:JavadocVariable
    // Replace possessive zero or more times quantifier *+ that the email pattern uses
    // with greedy zero or more times quantifier *
    // as possessive quantifiers are not supported in JavaScript
    EMAIL_REGEX(doubleEscapeRegex(FieldValidator.REGEX_EMAIL.replace("*+", "*")));
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
     * Double escape regex pattern strings to ensure the pattern remains correct when converted
     */
    private static String doubleEscapeRegex(String regexStr) {
        return regexStr.replace("\\", "\\\\");
    }

}

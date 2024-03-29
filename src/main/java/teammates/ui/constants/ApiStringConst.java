package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.FieldValidator;

/**
 * Special constants used by the back-end.
 */
public enum ApiStringConst {
    // CHECKSTYLE.OFF:JavadocVariable
    // double escape regex
    URL_REGEX(FieldValidator.REGEX_HOME_PAGE_URL.replace("\\", "\\\\")),
    // CHECKSTYLE.ON:JavadocVariable

    private final Object value;

    ApiStringConst(Object value) {
        this.value = value;
    }

    @JsonValue
    public Object getValue() {
        return value;
    }

}

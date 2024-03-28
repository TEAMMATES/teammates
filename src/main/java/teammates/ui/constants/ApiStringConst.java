package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const;

/**
 * Special constants used by the back-end.
 */
public enum ApiStringConst {
    // CHECKSTYLE.OFF:JavadocVariable
    URL_REGEX(Const.URL_REGEX);
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

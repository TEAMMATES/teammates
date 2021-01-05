package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApiConst {
    // CHECKSTYLE.OFF:JavadocVariable
    PLACEHOLDER(-1);
    // CHECKSTYLE.ON:JavadocVariable

    private final Object value;

    ApiConst(Object value) {
        this.value = value;
    }

    @JsonValue
    public Object getValue() {
        return value;
    }

}

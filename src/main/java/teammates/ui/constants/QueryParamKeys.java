package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.ParamsNames;

/**
 * Constants for shared query parameter keys used in URLs.
 */
public enum QueryParamKeys {
    //CHECKSTYLE.OFF:JavadocVariable
    NEXT_URL(ParamsNames.NEXT_URL),
    LOGIN_METHOD(ParamsNames.LOGIN_METHOD);

    //CHECKSTYLE.ON:JavadocVariable
    private final String key;

    QueryParamKeys(String key) {
        this.key = key;
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}

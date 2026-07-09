package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.ParamsNames;

/**
 * Constants for shared query parameter keys used in URLs.
 */
public enum QueryParamKeys {
    //CHECKSTYLE.OFF:JavadocVariable
    NEXT_URL(ParamsNames.NEXT_URL),
    LOGIN_METHOD(ParamsNames.LOGIN_METHOD),

    NOTIFICATION_ID(ParamsNames.NOTIFICATION_ID),
    NOTIFICATION_TARGET_USER(ParamsNames.NOTIFICATION_TARGET_USER),
    NOTIFICATION_IS_FETCHING_ACTIVE(ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE);

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

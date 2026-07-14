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

    COURSE_ID(ParamsNames.COURSE_ID),
    COURSE_STATUS(ParamsNames.COURSE_STATUS),

    NOTIFICATION_ID(ParamsNames.NOTIFICATION_ID),
    NOTIFICATION_TARGET_USER(ParamsNames.NOTIFICATION_TARGET_USER),
    NOTIFICATION_IS_FETCHING_ACTIVE(ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE),

    FEEDBACK_SESSION_ID(ParamsNames.FEEDBACK_SESSION_ID),
    FEEDBACK_SESSION_START_TIME(ParamsNames.FEEDBACK_SESSION_STARTTIME),
    FEEDBACK_SESSION_END_TIME(ParamsNames.FEEDBACK_SESSION_ENDTIME),
    FEEDBACK_SESSION_MODERATED_PERSON(ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON),
    FEEDBACK_SESSION_LOG_START_TIME(ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME),
    FEEDBACK_SESSION_LOG_END_TIME(ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME),
    FEEDBACK_SESSION_LOG_TYPE(ParamsNames.FEEDBACK_SESSION_LOG_TYPE),

    QUERY_LOGS_STARTTIME(ParamsNames.QUERY_LOGS_STARTTIME),
    QUERY_LOGS_ENDTIME(ParamsNames.QUERY_LOGS_ENDTIME),

    USER_ID(ParamsNames.USER_ID),
    ACCOUNT_ID(ParamsNames.ACCOUNT_ID),

    PREVIEWAS(ParamsNames.PREVIEWAS),
    IS_PREVIEW(ParamsNames.IS_PREVIEW),

    KEY(ParamsNames.KEY),
    SEARCH_KEY(ParamsNames.SEARCH_KEY),
    LIMIT(ParamsNames.LIMIT);

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

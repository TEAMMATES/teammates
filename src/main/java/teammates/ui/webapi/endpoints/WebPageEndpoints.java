package teammates.ui.webapi.endpoints;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.WebPageURIs;

/**
 * API endpoints for web pages.
 */
public enum WebPageEndpoints {
    //CHECKSTYLE.OFF:JavadocVariable
    JOIN_PAGE(WebPageURIs.JOIN_PAGE),
    STUDENT_HOME_PAGE(WebPageURIs.STUDENT_HOME_PAGE),
    INSTRUCTOR_HOME_PAGE(WebPageURIs.INSTRUCTOR_HOME_PAGE),
    ADMIN_ACCOUNTS_PAGE(WebPageURIs.ADMIN_ACCOUNTS_PAGE),
    INSTRUCTOR_STUDENT_RECORDS_PAGE(WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE),
    SESSION_SUBMISSION_PAGE(WebPageURIs.SESSION_SUBMISSION_PAGE),
    SESSIONS_RESULT_PAGE(WebPageURIs.SESSION_RESULTS_PAGE);
    //CHECKSTYLE.ON:JavadocVariable

    private final String url;

    WebPageEndpoints(String s) {
        this.url = s;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}

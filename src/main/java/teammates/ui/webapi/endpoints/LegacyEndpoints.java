package teammates.ui.webapi.endpoints;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.LegacyURIs;

/**
 * Legacy API endpoints.
 */
public enum LegacyEndpoints {
    //CHECKSTYLE.OFF:JavadocVariable
    INSTRUCTOR_COURSE_JOIN(LegacyURIs.INSTRUCTOR_COURSE_JOIN),
    STUDENT_COURSE_JOIN(LegacyURIs.STUDENT_COURSE_JOIN),
    STUDENT_COURSE_JOIN_NEW(LegacyURIs.STUDENT_COURSE_JOIN_NEW),
    STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE(LegacyURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE),
    STUDENT_FEEDBACK_RESULTS_PAGE(LegacyURIs.STUDENT_FEEDBACK_RESULTS_PAGE),
    INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE(LegacyURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE),
    INSTRUCTOR_FEEDBACK_RESULTS_PAGE(LegacyURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE);
    //CHECKSTYLE.ON:JavadocVariable

    private final String url;

    LegacyEndpoints(String s) {
        this.url = s;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}

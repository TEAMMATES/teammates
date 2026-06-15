package teammates.common.util;

import java.util.UUID;

/**
 * Utility class for constructing frontend URL strings.
 */
public final class LinksUtil {

    private LinksUtil() {
        // utility class
    }

    // -------------------------------------------------------------------------
    // Session pages
    // -------------------------------------------------------------------------

    /**
     * Returns the absolute URL for a student to submit responses for the given feedback session.
     */
    public static String getStudentSessionSubmitUrl(UUID feedbackSessionId, String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .withRegistrationKey(regKey)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for an instructor to submit responses for the given feedback session.
     */
    public static String getInstructorSessionSubmitUrl(UUID feedbackSessionId, String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .withRegistrationKey(regKey)
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for a student to view results for the given feedback session.
     */
    public static String getStudentSessionResultsUrl(UUID feedbackSessionId, String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .withRegistrationKey(regKey)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for an instructor to view results for the given feedback session.
     */
    public static String getInstructorSessionResultsUrl(UUID feedbackSessionId, String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .withRegistrationKey(regKey)
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for an instructor to edit the given feedback session.
     */
    public static String getInstructorSessionEditUrl(UUID feedbackSessionId) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for an instructor to view the report for the given feedback session.
     */
    public static String getInstructorSessionReportUrl(UUID feedbackSessionId) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                .withFeedbackSessionId(feedbackSessionId)
                .toAbsoluteString();
    }

    // -------------------------------------------------------------------------
    // Simple page URLs (no parameters)
    // -------------------------------------------------------------------------

    /**
     * Returns the absolute URL for the session links recovery page.
     */
    public static String getSessionLinkRecoveryUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for the application home page.
     */
    public static String getHomePageUrl() {
        return Config.getFrontEndAppUrl("/").toAbsoluteString();
    }

    /**
     * Returns the absolute URL for the admin home page.
     */
    public static String getAdminHomePageUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_HOME_PAGE).toAbsoluteString();
    }

    /**
     * Returns the absolute URL for the instructor home page.
     */
    public static String getInstructorHomePageUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE).toAbsoluteString();
    }

    /**
     * Returns the absolute URL for the student home page.
     */
    public static String getStudentHomePageUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE).toAbsoluteString();
    }

    // -------------------------------------------------------------------------
    // Join / registration URLs
    // -------------------------------------------------------------------------

    /**
     * Returns the absolute URL for a student to join a course using the given registration key.
     */
    public static String getStudentCourseJoinUrl(String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(regKey)
                .withEntityType(Const.EntityType.STUDENT)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for an instructor to join a course using the given registration key.
     */
    public static String getInstructorCourseJoinUrl(String regKey) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(regKey)
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
    }

    /**
     * Returns the absolute URL for the instructor welcome page for the given account verification request.
     */
    public static String getInstructorWelcomeUrl(UUID accountVerificationRequestId) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_WELCOME_PAGE)
                .withPathSegment(accountVerificationRequestId.toString())
                .toAbsoluteString();
    }

    // -------------------------------------------------------------------------
    // Relative URLs
    // -------------------------------------------------------------------------

    /**
     * Returns a relative URL for the instructor home page with a masquerade account parameter.
     *
     * <p>Returns a relative URL (not absolute) for embedding in response objects where
     * the frontend router expects a path-only value.
     */
    public static String getInstructorHomePageRelativeUrl(UUID masqueradeAccountId) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                .withMasqueradeAccount(masqueradeAccountId)
                .toString();
    }
}

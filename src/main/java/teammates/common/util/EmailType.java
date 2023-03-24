package teammates.common.util;

/**
 * The type object that defines an email based on its purpose.
 */
public enum EmailType {

    // CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting

    DEADLINE_EXTENSION_GRANTED("TEAMMATES: Deadline extension granted [Course: %s][Feedback Session: %s]"),
    DEADLINE_EXTENSION_UPDATED("TEAMMATES: Deadline extension updated [Course: %s][Feedback Session: %s]"),
    DEADLINE_EXTENSION_REVOKED("TEAMMATES: Deadline extension revoked [Course: %s][Feedback Session: %s]"),
    FEEDBACK_OPENING_SOON("TEAMMATES: Feedback session opening soon [Course: %s][Feedback Session: %s]"),
    FEEDBACK_OPENING("TEAMMATES: Feedback session now open [Course: %s][Feedback Session: %s]"),
    FEEDBACK_SESSION_REMINDER("TEAMMATES: Feedback session reminder [Course: %s][Feedback Session: %s]"),
    FEEDBACK_CLOSING("TEAMMATES: Feedback session closing soon [Course: %s][Feedback Session: %s]"),
    FEEDBACK_CLOSED("TEAMMATES: Feedback session closed [Course: %s][Feedback Session: %s]"),
    FEEDBACK_PUBLISHED("TEAMMATES: Feedback session results published [Course: %s][Feedback Session: %s]"),
    FEEDBACK_UNPUBLISHED("TEAMMATES: Feedback session results unpublished [Course: %s][Feedback Session: %s]"),
    STUDENT_EMAIL_CHANGED("TEAMMATES: Summary of course [%s][Course ID: %s]"),
    STUDENT_COURSE_LINKS_REGENERATED("TEAMMATES: Your updated links for course [%s][Course ID: %s]"),
    INSTRUCTOR_COURSE_LINKS_REGENERATED("TEAMMATES: Your updated links for course [%s][Course ID: %s]"),
    NEW_INSTRUCTOR_ACCOUNT("TEAMMATES: Welcome to TEAMMATES! %s"),
    STUDENT_COURSE_JOIN("TEAMMATES: Invitation to join course [%s][Course ID: %s]"),
    STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET("TEAMMATES: Your account has been reset for course [%s][Course ID: %s]"),
    INSTRUCTOR_COURSE_JOIN("TEAMMATES: Invitation to join course as an instructor [%s][Course ID: %s]"),
    INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET("TEAMMATES: Your account has been reset for course [%s][Course ID: %s]"),
    USER_COURSE_REGISTER("TEAMMATES: Registered for Course [%s][Course ID: %s]"),
    SEVERE_LOGS_COMPILATION("TEAMMATES (%s): Severe Error Logs Compilation"),
    SESSION_LINKS_RECOVERY("TEAMMATES: Access links for recent feedback sessions"),
    LOGIN("TEAMMATES: Log in to TEAMMATES");

    private final String subject;

    EmailType(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the subject heading of this email type.
     */
    public String getSubject() {
        return subject;
    }

}

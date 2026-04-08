package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;

/**
 * Registry of admin-configurable email templates.
 *
 * <p>Each constant bundles the template's default subject, default body, and the
 * set of body placeholders that must be present before a custom template can be saved.
 *
 * <p>Adding a new configurable template requires a single entry here; the action
 * layer derives all registry data (allowed keys, fallback values, required placeholders)
 * from this enum, eliminating parallel data structures.
 */
public enum ConfigurableEmailTemplate {

    // CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting

    NEW_INSTRUCTOR_ACCOUNT_WELCOME(
            "TEAMMATES: Welcome to TEAMMATES! ${userName}",
            EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
            List.of("${joinUrl}")),

    NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT(
            "TEAMMATES: Acknowledgement of Instructor Account Request",
            EmailTemplates.INSTRUCTOR_NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT,
            List.of()),

    STUDENT_COURSE_JOIN(
            "TEAMMATES: Invitation to join course [${courseName}][Course ID: ${courseId}]",
            Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                    "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN),
            List.of("${joinUrl}")),

    INSTRUCTOR_COURSE_JOIN(
            "TEAMMATES: Invitation to join course as an instructor [${courseName}][Course ID: ${courseId}]",
            Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                    "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN),
            List.of("${joinUrl}")),

    LOGIN(
            "TEAMMATES: Log in to TEAMMATES",
            EmailTemplates.LOGIN_EMAIL,
            List.of("${loginLink}")),

    FEEDBACK_PUBLISHED(
            "TEAMMATES: Feedback session results published"
                    + " [Course: ${courseName}][Feedback Session: ${feedbackSessionName}]",
            EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED,
            List.of()),

    FEEDBACK_UNPUBLISHED(
            "TEAMMATES: Feedback session results unpublished"
                    + " [Course: ${courseName}][Feedback Session: ${feedbackSessionName}]",
            EmailTemplates.USER_FEEDBACK_SESSION_UNPUBLISHED,
            List.of()),

    FEEDBACK_OPENED(
            "TEAMMATES: Feedback session now open [Course: ${courseName}][Feedback Session: ${feedbackSessionName}]",
            Templates.populateTemplate(EmailTemplates.USER_FEEDBACK_SESSION_OPENED, "${status}", "is now open"),
            List.of()),

    USER_COURSE_REGISTER(
            "TEAMMATES: Registered for Course [${courseName}][Course ID: ${courseId}]",
            EmailTemplates.USER_COURSE_REGISTER,
            List.of()),

    STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET(
            "TEAMMATES: Your account has been reset for course [${courseName}][Course ID: ${courseId}]",
            Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                    "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET),
            List.of("${joinUrl}")),

    INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET(
            "TEAMMATES: Your account has been reset for course [${courseName}][Course ID: ${courseId}]",
            Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                    "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET),
            List.of("${joinUrl}")),

    NEW_ACCOUNT_REQUEST_ADMIN_ALERT(
            "TEAMMATES (Action Needed): New Account Request Received",
            EmailTemplates.ADMIN_NEW_ACCOUNT_REQUEST_ALERT,
            List.of("${adminAccountRequestsPageUrl}"));

    // CHECKSTYLE.ON:JavadocVariable

    private final String defaultSubject;
    private final String defaultBody;
    private final List<String> requiredBodyPlaceholders;

    ConfigurableEmailTemplate(String defaultSubject, String defaultBody,
            List<String> requiredBodyPlaceholders) {
        this.defaultSubject = defaultSubject;
        this.defaultBody = defaultBody;
        this.requiredBodyPlaceholders = requiredBodyPlaceholders;
    }

    /**
     * Gets the default subject for this email template.
     */
    public String getDefaultSubject() {
        return defaultSubject;
    }

    /**
     * Gets the default body for this email template.
     */
    public String getDefaultBody() {
        return defaultBody;
    }

    /**
     * Returns the required body placeholders that are absent from {@code body},
     * or an empty list if all required placeholders are present.
     */
    public List<String> getMissingPlaceholders(String body) {
        return requiredBodyPlaceholders.stream()
                .filter(placeholder -> !body.contains(placeholder))
                .collect(Collectors.toList());
    }

}

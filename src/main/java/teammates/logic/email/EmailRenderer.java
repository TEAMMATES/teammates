package teammates.logic.email;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.LinksUtil;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.logic.email.model.AccountVerificationApprovedEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
import teammates.logic.email.model.AccountVerificationRejectedEmailContext;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.InstructorCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
import teammates.logic.email.model.StudentCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;

/**
 * Pure rendering logic for email templates.
 */
public final class EmailRenderer {

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";
    private static final String FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW = "submit, edit or view";

    private EmailRenderer() {
        // utility class
    }

    /**
     * Renders the session links recovery email body for a recipient with
     * recoverable sessions.
     */
    public static RenderedEmail renderSessionLinksRecoveryEmail(SessionLinksRecoveryContext context) {
        String courseSectionsHtml = buildCourseSectionsHtml(context.recoverableCourseLinks());
        String emptyStateMessage = context.recoverableCourseLinks().isEmpty()
                ? """
                  <p>
                      We could not find any sessions associated with this email address that have opened or closed
                      in the past 180 days.
                  </p>
                  """
                : "";

        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_FOUND,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${userEmail}", SanitizationHelper.sanitizeForHtml(context.recoveryEmailAddress()),
                "${homePageLink}", LinksUtil.getHomePageUrl(),
                "${courseSections}", courseSectionsHtml,
                "${emptyStateMessage}", emptyStateMessage,
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl()));
    }

    /**
     * Renders the session links recovery email body for an email address with no
     * matching student records.
     */
    public static RenderedEmail renderSessionLinksRecoveryNotFoundEmail(String recoveryEmailAddress) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${homePageLink}", LinksUtil.getHomePageUrl(),
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl()));
    }

    /**
     * Renders the post-join course registration confirmation email body.
     */
    public static RenderedEmail renderUserCourseRegisteredEmail(
            CourseEmailContext courseContext, UserCourseRegisteredEmailContext userContext) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_COURSE_REGISTER,
                "${userName}", SanitizationHelper.sanitizeForHtml(userContext.recipientName()),
                "${userType}", userContext.isInstructor() ? "an instructor" : "a student",
                "${courseId}", SanitizationHelper.sanitizeForHtml(courseContext.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(courseContext.courseName()),
                "${appUrl}", userContext.appUrl(),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the admin alert email body for a newly created account
     * verification request.
     */
    public static RenderedEmail renderAccountVerificationCreatedAdminAlertEmail(
            AccountVerificationCreatedAdminAlertEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.ADMIN_NEW_ACCOUNT_VERIFICATION_REQUEST_ALERT,
                "${name}", context.instructorName(),
                "${institute}", context.instituteName(),
                "${emailAddress}", context.instructorEmailAddress(),
                "${comments}", context.comments() == null ? "" : context.comments(),
                "${adminAccountVerificationRequestsPageUrl}", context.adminAccountVerificationRequestsPageUrl()));
    }

    /**
     * Renders the submitter acknowledgement email body for a newly created
     * account verification request.
     */
    public static RenderedEmail renderAccountVerificationCreatedAcknowledgementEmail(
            AccountVerificationCreatedAcknowledgementEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.INSTRUCTOR_NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT,
                "${name}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${institute}", SanitizationHelper.sanitizeForHtml(context.instituteName()),
                "${emailAddress}", SanitizationHelper.sanitizeForHtml(context.instructorEmailAddress()),
                "${comments}", sanitizeOptionalHtml(context.comments()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the approval email body for an approved account verification
     * request.
     */
    public static RenderedEmail renderAccountVerificationApprovedEmail(
            AccountVerificationApprovedEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.ACCOUNT_VERIFICATION_APPROVED,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${welcomeUrl}", context.instructorWelcomeUrl(),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the rejection email body for a rejected account verification
     * request.
     */
    public static RenderedEmail renderAccountVerificationRejectedEmail(
            AccountVerificationRejectedEmailContext context) {
        return new RenderedEmail(context.reasonBodyHtml());
    }

    /**
     * Renders the student course join invitation email body.
     */
    public static RenderedEmail renderStudentCourseJoinEmail(
            CourseEmailContext courseContext, StudentCourseJoinEmailContext studentContext) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.STUDENT_COURSE_JOIN,
                "${userName}", SanitizationHelper.sanitizeForHtml(studentContext.recipientName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(courseContext.courseName()),
                "${joinUrl}", studentContext.joinUrl(),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(courseContext.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the student course rejoin email body after account unlink.
     */
    public static RenderedEmail renderStudentCourseRejoinAfterUnlinkAccountEmail(
            CourseEmailContext courseContext, StudentCourseRejoinAfterUnlinkEmailContext studentContext) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.STUDENT_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
                "${userName}", SanitizationHelper.sanitizeForHtml(studentContext.recipientName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(courseContext.courseName()),
                "${joinUrl}", studentContext.joinUrl(),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(courseContext.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the instructor course join invitation email body.
     */
    public static RenderedEmail renderInstructorCourseJoinEmail(
            CourseEmailContext courseContext, InstructorCourseJoinEmailContext instructorContext) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.INSTRUCTOR_COURSE_JOIN,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructorContext.recipientName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(courseContext.courseName()),
                "${joinUrl}", instructorContext.joinUrl(),
                "${inviterName}", SanitizationHelper.sanitizeForHtml(instructorContext.inviterName()),
                "${inviterEmail}", SanitizationHelper.sanitizeForHtml(instructorContext.inviterEmail()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the instructor course rejoin email body after account unlink.
     */
    public static RenderedEmail renderInstructorCourseRejoinAfterUnlinkAccountEmail(
            CourseEmailContext courseContext, InstructorCourseRejoinAfterUnlinkEmailContext instructorContext) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.INSTRUCTOR_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructorContext.recipientName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(courseContext.courseName()),
                "${joinUrl}", instructorContext.joinUrl(),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders the deadline extension update email body.
     */
    public static RenderedEmail renderDeadlineExtensionUpdateEmail(
            FeedbackSessionEmailContext feedbackSessionContext, DeadlineExtensionUpdateEmailContext context) {
        String oldEndTime = formatDeadline(context.oldEndTime(), feedbackSessionContext.courseTimeZone());
        String newEndTime = formatDeadline(context.newEndTime(), feedbackSessionContext.courseTimeZone());
        String status = getDeadlineExtensionStatus(context.emailType());

        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_DEADLINE_EXTENSION,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${instructorPreamble}", "",
                "${status}", status,
                "${courseId}", SanitizationHelper.sanitizeForHtml(feedbackSessionContext.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(feedbackSessionContext.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(feedbackSessionContext.feedbackSessionName()),
                "${oldEndTime}", SanitizationHelper.sanitizeForHtml(oldEndTime),
                "${newEndTime}", SanitizationHelper.sanitizeForHtml(newEndTime),
                "${sessionInstructions}", feedbackSessionContext.sessionInstructions(),
                "${submitUrl}", context.submitUrl(),
                "${feedbackAction}", FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW,
                "${particulars}", getAdditionalContactParticulars(context.isInstructor()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(feedbackSessionContext.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    private static String buildCourseSectionsHtml(List<RecoverableCourseLinks> courseSections) {
        StringBuilder html = new StringBuilder();
        for (RecoverableCourseLinks courseSection : courseSections) {
            StringBuilder sessionRowsHtml = new StringBuilder();
            for (RecoverableSessionLink sessionLink : courseSection.sessionLinks()) {
                StringBuilder linksHtml = new StringBuilder();
                boolean hasSubmitLink = sessionLink.submitUrl() != null;
                boolean hasResultsLink = sessionLink.resultsUrl() != null;

                if (hasSubmitLink) {
                    linksHtml.append("[<a href=\"")
                            .append(sessionLink.submitUrl())
                            .append("\">submission link</a>]");
                }
                if (hasSubmitLink && hasResultsLink) {
                    linksHtml.append(' ');
                }
                if (hasResultsLink) {
                    linksHtml.append("[<a href=\"")
                            .append(sessionLink.resultsUrl())
                            .append("\">result link</a>]");
                }

                sessionRowsHtml.append(Templates.populateTemplate(
                        EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_SESSION,
                        "${sessionName}", SanitizationHelper.sanitizeForHtml(sessionLink.feedbackSessionName()),
                        "${links}", linksHtml.toString()));
            }

            String courseName = SanitizationHelper.sanitizeForHtml(courseSection.courseName()
                    + " (" + courseSection.courseId() + ")");
            html.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_COURSE,
                    "${sessionFragment}", sessionRowsHtml.toString(),
                    "${courseName}", courseName));
        }
        return html.toString();
    }

    private static String formatDeadline(Instant instant, String timeZone) {
        Instant adjustedInstant = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instant, timeZone, false);
        return TimeHelper.formatInstant(adjustedInstant, timeZone, DATETIME_DISPLAY_FORMAT);
    }

    private static String getDeadlineExtensionStatus(EmailType emailType) {
        return switch (emailType) {
        case DEADLINE_EXTENSION_GRANTED -> "You have been granted a deadline extension for the following"
                + " feedback session.";
        case DEADLINE_EXTENSION_UPDATED -> "Your deadline for the following feedback session has been updated.";
        case DEADLINE_EXTENSION_REVOKED -> "Your deadline extension for the following feedback session has been"
                + " revoked.";
        default -> throw new AssertionError("Invalid deadline extension email type: " + emailType);
        };
    }

    private static String getAdditionalContactParticulars(boolean isInstructor) {
        return isInstructor ? "instructor data (e.g. wrong permission, misspelled name)"
                : "team/student data (e.g. wrong team, misspelled name)";
    }

    private static String sanitizeOptionalHtml(String value) {
        return value == null ? "" : SanitizationHelper.sanitizeForHtml(value);
    }

    private static String buildCoOwnersEmailsLine(List<EmailContact> coOwnerContacts) {
        if (coOwnerContacts.isEmpty()) {
            return "(No contactable instructors found)";
        }

        return coOwnerContacts.stream()
                .map(coOwnerContact -> SanitizationHelper.sanitizeForHtml(coOwnerContact.name())
                        + " (" + SanitizationHelper.sanitizeForHtml(coOwnerContact.email()) + ")")
                .collect(Collectors.joining(", "));
    }
}

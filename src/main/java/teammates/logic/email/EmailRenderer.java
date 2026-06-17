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
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;

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

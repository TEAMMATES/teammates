package teammates.logic.email;

import java.util.List;

import teammates.common.util.Config;
import teammates.common.util.LinksUtil;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;

/**
 * Pure rendering logic for email templates.
 */
public final class EmailRenderer {

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
}

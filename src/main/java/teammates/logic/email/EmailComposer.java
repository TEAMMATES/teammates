package teammates.logic.email;

import java.util.List;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LinksUtil;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;

/**
 * Pure composition logic for outbound emails.
 */
public class EmailComposer {

    private static final EmailComposer instance = new EmailComposer();

    EmailComposer() {
        // prevent initialization
    }

    public static EmailComposer inst() {
        return instance;
    }

    /**
     * Composes the session links recovery email for a recipient with recoverable
     * sessions.
     */
    public EmailWrapper composeSessionLinksRecoveryEmail(SessionLinksRecoveryContext context) {
        String courseSectionsHtml = buildCourseSectionsHtml(context.recoverableCourseLinks());
        String emptyStateMessage = context.recoverableCourseLinks().isEmpty()
                ? """
                  <p>
                      We could not find any sessions associated with this email address that have opened or closed
                      in the past 180 days.
                  </p>
                  """
                : "";

        String emailBody = Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_FOUND,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${userEmail}", SanitizationHelper.sanitizeForHtml(context.recoveryEmailAddress()),
                "${teammateHomePageLink}", LinksUtil.getHomePageUrl(),
                "${courseSections}", courseSectionsHtml,
                "${emptyStateMessage}", emptyStateMessage,
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl());

        return createBaseEmail(context.recoveryEmailAddress(), EmailType.SESSION_LINKS_RECOVERY, emailBody);
    }

    /**
     * Composes the session links recovery email for an email address with no
     * matching student records.
     */
    public EmailWrapper composeSessionLinksRecoveryNotFoundEmail(String recoveryEmailAddress) {
        String emailBody = Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${teammateHomePageLink}", LinksUtil.getHomePageUrl(),
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl());

        return createBaseEmail(recoveryEmailAddress, EmailType.SESSION_LINKS_RECOVERY, emailBody);
    }

    private String buildCourseSectionsHtml(List<RecoverableCourseLinks> courseSections) {
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
                    linksHtml.append(" ");
                }
                if (hasResultsLink) {
                    linksHtml.append("[<a href=\"")
                            .append(sessionLink.resultsUrl())
                            .append("\">result link</a>]");
                }

                sessionRowsHtml.append(Templates.populateTemplate(
                        EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_SESSION,
                        "${sessionName}", SanitizationHelper.sanitizeForHtml(sessionLink.feedbackSessionName()),
                        "${submitUrl}", linksHtml.toString(),
                        "${reportUrl}", ""));
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

    private EmailWrapper createBaseEmail(String recipientEmailAddress, EmailType emailType, String emailBody) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipientEmailAddress);
        email.setSenderEmail(Config.EMAIL_SENDEREMAIL);
        email.setSenderName(Config.EMAIL_SENDERNAME);
        email.setReplyTo(Config.EMAIL_REPLYTO);
        email.setType(emailType);
        email.setSubjectFromType();
        email.setContent(emailBody);
        return email;
    }
}

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
import teammates.logic.email.model.CourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.CourseSessionLinks;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.FeedbackSessionOwnerReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionParticipantReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionPreviewReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsParticipantEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsPreviewEmailContext;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionAccessLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
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
        String courseSectionsHtml = buildCourseSectionsHtml(context.courseSessionLinks());
        String emptyStateMessage = context.courseSessionLinks().isEmpty()
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
     * Renders a feedback session summary email body.
     */
    public static RenderedEmail renderFeedbackSessionSummaryEmail(
            FeedbackSessionSummaryEmailContext context, EmailType emailType) {
        String joinFragment = buildFeedbackSessionSummaryJoinFragment(context, emailType);
        String courseSectionsHtml = buildCourseSectionsHtml(context.courseSessionLinks());
        String emptyStateMessage = context.courseSessionLinks().isEmpty() ? "<p>No links found.</p>" : "";
        String template = emailType == EmailType.STUDENT_EMAIL_CHANGED
                ? EmailTemplates.USER_FEEDBACK_SESSION_RESEND_ALL_LINKS
                : EmailTemplates.USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS;

        return new RenderedEmail(Templates.populateTemplate(
                template,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${userEmail}", SanitizationHelper.sanitizeForHtml(context.recipientEmailAddress()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${joinFragment}", joinFragment,
                "${courseSections}", courseSectionsHtml,
                "${emptyStateMessage}", emptyStateMessage,
                "${additionalContactInformation}", getAdditionalContactInformationFragment(
                        context.coOwnerContacts(), context.isInstructor())));
    }

    /**
     * Renders a feedback session opened email body for a participant.
     */
    public static RenderedEmail renderFeedbackSessionOpenedParticipantEmail(
            FeedbackSessionParticipantReminderEmailContext context) {
        return renderFeedbackSessionParticipantReminderEmail(
                context,
                EmailTemplates.USER_FEEDBACK_SESSION_OPENED,
                "is now open");
    }

    /**
     * Renders a feedback session closing soon email body for a participant.
     */
    public static RenderedEmail renderFeedbackSessionClosingSoonParticipantEmail(
            FeedbackSessionParticipantReminderEmailContext context) {
        return renderFeedbackSessionParticipantReminderEmail(
                context,
                EmailTemplates.USER_FEEDBACK_SESSION_CLOSING_SOON,
                "is closing soon");
    }

    private static RenderedEmail renderFeedbackSessionParticipantReminderEmail(
            FeedbackSessionParticipantReminderEmailContext context,
            String template,
            String status) {
        String deadline = formatDeadline(context.deadline(), context.courseTimeZone())
                + (context.hasDeadlineExtension() ? " (after extension)" : "");
        return new RenderedEmail(Templates.populateTemplate(
                template,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${status}", status,
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(deadline),
                "${sessionInstructions}", context.sessionInstructions(),
                "${feedbackAction}", FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW,
                "${submitUrl}", context.submitUrl(),
                "${particulars}", getAdditionalContactParticulars(context.isInstructor()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders a feedback session opened preview email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionOpenedPreviewEmail(
            FeedbackSessionPreviewReminderEmailContext context) {
        return renderFeedbackSessionPreviewReminderEmail(
                context,
                EmailTemplates.USER_FEEDBACK_SESSION_OPENED_PREVIEW,
                "is now open");
    }

    /**
     * Renders a feedback session closing soon preview email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionClosingSoonPreviewEmail(
            FeedbackSessionPreviewReminderEmailContext context) {
        return renderFeedbackSessionPreviewReminderEmail(
                context,
                EmailTemplates.USER_FEEDBACK_SESSION_CLOSING_SOON_PREVIEW,
                "is closing soon");
    }

    /**
     * Renders a feedback session reminder email body for a participant.
     */
    public static RenderedEmail renderFeedbackSessionReminderParticipantEmail(
            FeedbackSessionParticipantReminderEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_REMINDER,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        formatDeadline(context.deadline(), context.courseTimeZone())
                        + (context.hasDeadlineExtension() ? " (after extension)" : "")),
                "${sessionInstructions}", context.sessionInstructions(),
                "${submitUrl}", context.submitUrl(),
                "${particulars}", getAdditionalContactParticulars(context.isInstructor()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders a feedback session reminder preview email body for an instructor.
     */
    public static RenderedEmail renderFeedbackSessionReminderPreviewEmail(
            FeedbackSessionPreviewReminderEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_REMINDER_PREVIEW,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        formatDeadline(context.deadline(), context.courseTimeZone())),
                "${sessionInstructions}", context.sessionInstructions(),
                "${submitUrlPlaceholder}", "{in the actual email sent to the respondents, this will be the unique link}",
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl()));
    }

    /**
     * Renders a feedback session published email body for a participant.
     */
    public static RenderedEmail renderFeedbackSessionPublishedParticipantEmail(
            FeedbackSessionResultsParticipantEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${reportUrl}", context.reportUrl(),
                "${particulars}", getAdditionalContactParticulars(context.isInstructor()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders a feedback session published preview email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionPublishedPreviewEmail(
            FeedbackSessionResultsPreviewEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED_PREVIEW,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${reportUrlPlaceholder}", "{in the actual email sent to the students, this will be the unique link}",
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl()));
    }

    /**
     * Renders a feedback session unpublished email body for a participant.
     */
    public static RenderedEmail renderFeedbackSessionUnpublishedParticipantEmail(
            FeedbackSessionResultsParticipantEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_UNPUBLISHED,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${particulars}", getAdditionalContactParticulars(context.isInstructor()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    /**
     * Renders a feedback session unpublished preview email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionUnpublishedPreviewEmail(
            FeedbackSessionResultsPreviewEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.USER_FEEDBACK_SESSION_UNPUBLISHED_PREVIEW,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl()));
    }

    /**
     * Renders a feedback session opening soon email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionOpeningSoonEmail(
            FeedbackSessionOwnerReminderEmailContext context) {
        String template = context.joinUrl() == null
                ? EmailTemplates.OWNER_FEEDBACK_SESSION_OPENING_SOON
                : EmailTemplates.OWNER_FEEDBACK_SESSION_OPENING_SOON_NOT_JOINED;
        return new RenderedEmail(Templates.populateTemplate(
                template,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${startTime}", SanitizationHelper.sanitizeForHtml(
                        formatCourseTime(context.startTime(), context.courseTimeZone())),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        formatCourseTime(context.deadline(), context.courseTimeZone())),
                "${sessionInstructions}", context.sessionInstructions(),
                "${sessionEditUrl}", context.sessionEditUrl(),
                "${joinUrl}", context.joinUrl() == null ? "" : context.joinUrl()));
    }

    /**
     * Renders a feedback session closed email body for a co-owner.
     */
    public static RenderedEmail renderFeedbackSessionClosedEmail(
            FeedbackSessionOwnerReminderEmailContext context) {
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.OWNER_FEEDBACK_SESSION_CLOSED,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${startTime}", SanitizationHelper.sanitizeForHtml(
                        formatCourseTime(context.startTime(), context.courseTimeZone())),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        formatCourseTime(context.deadline(), context.courseTimeZone())),
                "${sessionInstructions}", context.sessionInstructions(),
                "${reportUrl}", context.reportUrl()));
    }

    private static RenderedEmail renderFeedbackSessionPreviewReminderEmail(
            FeedbackSessionPreviewReminderEmailContext context,
            String template,
            String status) {
        return new RenderedEmail(Templates.populateTemplate(
                template,
                "${userName}", SanitizationHelper.sanitizeForHtml(context.recipientName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(context.courseId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(context.feedbackSessionName()),
                "${status}", status,
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        formatDeadline(context.deadline(), context.courseTimeZone())),
                "${sessionInstructions}", context.sessionInstructions(),
                "${submitUrlPlaceholder}", "{in the actual email sent to the students, this will be the unique link}",
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
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
                "${name}", SanitizationHelper.sanitizeForHtml(context.instructorName()),
                "${institute}", SanitizationHelper.sanitizeForHtml(context.instituteName()),
                "${emailAddress}", SanitizationHelper.sanitizeForHtml(context.instructorEmailAddress()),
                "${comments}", sanitizeOptionalHtml(context.comments()),
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
                "${emailAddress}", SanitizationHelper.sanitizeForHtml(context.recipientEmailAddress()),
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
        String rejectionReason = getRejectionReason(context.rejectionType());
        String additionalCommentsBlock = context.additionalComments() != null && !context.additionalComments().isBlank()
                ? "<p><strong>Additional comments:</strong> "
                        + SanitizationHelper.sanitizeForHtml(context.additionalComments()) + "</p>"
                : "";
        return new RenderedEmail(Templates.populateTemplate(
                EmailTemplates.ACCOUNT_VERIFICATION_REJECTED,
                "${instituteName}", SanitizationHelper.sanitizeForHtml(context.instituteName()),
                "${rejectionReason}", rejectionReason,
                "${additionalComments}", additionalCommentsBlock,
                "${supportEmail}", Config.SUPPORT_EMAIL));
    }

    private static String getRejectionReason(
            teammates.common.datatransfer.AccountVerificationRequestRejectionType rejectionType) {
        return switch (rejectionType) {
        case ALREADY_VERIFIED -> "Your account is already verified for this institute.";
        case CANNOT_VERIFY_IDENTITY -> "We were unable to verify that you belong to the institute.";
        case NOT_OFFICIAL_EMAIL -> "The email address provided does not appear to be an official email address issued by the institute.";
        case NOT_INSTRUCTOR_ACCOUNT -> "Your account does not appear to belong to an instructor.";
        case OTHERS -> "Unfortunately, we are unable to approve your request at this time.";
        };
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
            CourseEmailContext courseContext, CourseRejoinAfterUnlinkEmailContext studentContext) {
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
            CourseEmailContext courseContext, CourseRejoinAfterUnlinkEmailContext instructorContext) {
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

    private static String buildCourseSectionsHtml(List<CourseSessionLinks> courseSections) {
        StringBuilder html = new StringBuilder();
        for (CourseSessionLinks courseSection : courseSections) {
            StringBuilder sessionRowsHtml = new StringBuilder();
            for (SessionAccessLink sessionLink : courseSection.sessionLinks()) {
                String linksHtml = buildSessionLinksHtml(sessionLink);

                sessionRowsHtml.append(Templates.populateTemplate(
                        EmailTemplates.FRAGMENT_COURSE_SESSION_LINKS_BY_SESSION,
                        "${sessionName}", SanitizationHelper.sanitizeForHtml(sessionLink.feedbackSessionName()),
                        "${deadline}", SanitizationHelper.sanitizeForHtml(
                                formatSessionAccessDeadline(sessionLink, courseSection.courseTimeZone())),
                        "${links}", linksHtml));
            }

            if (sessionRowsHtml.isEmpty()) {
                continue;
            }

            String courseName = SanitizationHelper.sanitizeForHtml(courseSection.courseName()
                    + " (" + courseSection.courseId() + ")");
            html.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_COURSE_SESSION_LINKS_BY_COURSE,
                    "${sessionFragment}", sessionRowsHtml.toString(),
                    "${courseName}", courseName));
        }
        return html.toString();
    }

    private static String buildSessionLinksHtml(SessionAccessLink sessionLink) {
        StringBuilder linksHtml = new StringBuilder();
        boolean hasSubmitLink = sessionLink.submitUrl() != null;
        boolean hasResultsLink = sessionLink.resultsUrl() != null;
        String separator = "<br>";

        if (hasSubmitLink) {
            linksHtml.append("[<a href=\"")
                    .append(sessionLink.submitUrl())
                    .append("\">submission link</a>]");
        } else {
            linksHtml.append("(Feedback session is not yet opened)");
        }

        linksHtml.append(separator);

        if (hasResultsLink) {
            linksHtml.append("[<a href=\"")
                    .append(sessionLink.resultsUrl())
                    .append("\">result link</a>]");
        } else {
            linksHtml.append("(Feedback session is not yet published)");
        }

        return linksHtml.toString();
    }

    private static String formatSessionAccessDeadline(SessionAccessLink sessionLink, String courseTimeZone) {
        return formatDeadline(sessionLink.endTime(), courseTimeZone);
    }

    private static String buildFeedbackSessionSummaryJoinFragment(
            FeedbackSessionSummaryEmailContext context, EmailType emailType) {
        if (!context.isYetToJoinCourse()) {
            return "";
        }

        String joinFragmentTemplate;
        if (context.isInstructor()) {
            joinFragmentTemplate = EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_REGKEY_RESET;
        } else if (emailType == EmailType.STUDENT_EMAIL_CHANGED) {
            joinFragmentTemplate = EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN;
        } else {
            joinFragmentTemplate = EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_REGKEY_RESET;
        }

        return Templates.populateTemplate(
                joinFragmentTemplate,
                "${joinUrl}", context.joinUrl(),
                "${courseName}", SanitizationHelper.sanitizeForHtml(context.courseName()),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(context.coOwnerContacts()),
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private static String formatDeadline(Instant instant, String timeZone) {
        return formatCourseTime(instant, timeZone);
    }

    private static String formatCourseTime(Instant instant, String timeZone) {
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

    private static String getAdditionalContactInformationFragment(List<EmailContact> coOwnerContacts, boolean isInstructor) {
        return Templates.populateTemplate(
                EmailTemplates.FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION,
                "${particulars}", getAdditionalContactParticulars(isInstructor),
                "${coOwnersEmails}", buildCoOwnersEmailsLine(coOwnerContacts),
                "${supportEmail}", Config.SUPPORT_EMAIL);
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

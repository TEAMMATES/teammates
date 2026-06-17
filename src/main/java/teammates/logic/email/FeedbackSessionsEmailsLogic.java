package teammates.logic.email;

import java.util.List;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.FeedbackSessionOpenedParticipantEmailContext;
import teammates.logic.email.model.FeedbackSessionOpenedPreviewEmailContext;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;

/**
 * Handles email-specific orchestration for feedback session use cases.
 */
public class FeedbackSessionsEmailsLogic {

    private static final FeedbackSessionsEmailsLogic instance = new FeedbackSessionsEmailsLogic();

    private EmailQueueService emailQueueService;

    public static FeedbackSessionsEmailsLogic inst() {
        return instance;
    }

    /**
     * Initializes the outbound email queue dependency.
     */
    public void init(EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    /**
     * Enqueues the appropriate session links recovery email for the given
     * recovery context.
     */
    public void enqueueSessionLinksRecoveryEmail(SessionLinksRecoveryContext context) {
        RenderedEmail renderedEmail = context.hasMatchingStudents()
                ? EmailRenderer.renderSessionLinksRecoveryEmail(context)
                : EmailRenderer.renderSessionLinksRecoveryNotFoundEmail(context.recoveryEmailAddress());
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recoveryEmailAddress(),
                EmailType.SESSION_LINKS_RECOVERY,
                renderedEmail);
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues a feedback session summary email for the given user context.
     */
    public void enqueueFeedbackSessionSummaryEmail(FeedbackSessionSummaryEmailContext context, EmailType emailType) {
        RenderedEmail renderedEmail = EmailRenderer.renderFeedbackSessionSummaryEmail(context, emailType);
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recipientEmailAddress(),
                emailType,
                renderedEmail,
                context.courseName(),
                context.courseId());
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues feedback session opened emails using the standard queue.
     */
    public void enqueueOpenedEmails(
            List<FeedbackSessionOpenedParticipantEmailContext> participantContexts,
            List<FeedbackSessionOpenedPreviewEmailContext> previewContexts) {
        List<EmailWrapper> emails = new java.util.ArrayList<>();
        for (FeedbackSessionOpenedParticipantEmailContext context : participantContexts) {
            RenderedEmail renderedEmail = EmailRenderer.renderFeedbackSessionOpenedParticipantEmail(context);
            emails.add(EmailWrapperBuilder.build(
                    context.recipientEmailAddress(),
                    EmailType.FEEDBACK_OPENED,
                    renderedEmail,
                    context.courseName(),
                    context.feedbackSessionName()));
        }
        for (FeedbackSessionOpenedPreviewEmailContext context : previewContexts) {
            RenderedEmail renderedEmail = EmailRenderer.renderFeedbackSessionOpenedPreviewEmail(context);
            EmailWrapper email = EmailWrapperBuilder.build(
                    context.recipientEmailAddress(),
                    EmailType.FEEDBACK_OPENED,
                    renderedEmail,
                    context.courseName(),
                    context.feedbackSessionName());
            email.setIsCopy(true);
            email.setSubjectFromType(context.courseName(), context.feedbackSessionName());
            emails.add(email);
        }
        emailQueueService.enqueueStandard(emails);
    }

}

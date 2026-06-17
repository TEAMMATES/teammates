package teammates.logic.email;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.FeedbackSessionOwnerReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionParticipantReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionPreviewReminderEmailContext;
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
            List<FeedbackSessionParticipantReminderEmailContext> participantContexts,
            List<FeedbackSessionPreviewReminderEmailContext> previewContexts) {
        enqueueReminderEmails(
                participantContexts,
                previewContexts,
                EmailType.FEEDBACK_OPENED,
                EmailRenderer::renderFeedbackSessionOpenedParticipantEmail,
                EmailRenderer::renderFeedbackSessionOpenedPreviewEmail);
    }

    /**
     * Enqueues feedback session closing soon emails using the standard queue.
     */
    public void enqueueClosingSoonEmails(
            List<FeedbackSessionParticipantReminderEmailContext> participantContexts,
            List<FeedbackSessionPreviewReminderEmailContext> previewContexts) {
        enqueueReminderEmails(
                participantContexts,
                previewContexts,
                EmailType.FEEDBACK_CLOSING_SOON,
                EmailRenderer::renderFeedbackSessionClosingSoonParticipantEmail,
                EmailRenderer::renderFeedbackSessionClosingSoonPreviewEmail);
    }

    /**
     * Enqueues feedback session opening soon emails using the standard queue.
     */
    public void enqueueOpeningSoonEmails(List<FeedbackSessionOwnerReminderEmailContext> contexts) {
        enqueueOwnerReminderEmails(contexts, EmailType.FEEDBACK_OPENING_SOON,
                EmailRenderer::renderFeedbackSessionOpeningSoonEmail);
    }

    /**
     * Enqueues feedback session closed emails using the standard queue.
     */
    public void enqueueClosedEmails(List<FeedbackSessionOwnerReminderEmailContext> contexts) {
        enqueueOwnerReminderEmails(contexts, EmailType.FEEDBACK_CLOSED,
                EmailRenderer::renderFeedbackSessionClosedEmail);
    }

    private void enqueueReminderEmails(
            List<FeedbackSessionParticipantReminderEmailContext> participantContexts,
            List<FeedbackSessionPreviewReminderEmailContext> previewContexts,
            EmailType emailType,
            Function<FeedbackSessionParticipantReminderEmailContext, RenderedEmail> participantRenderer,
            Function<FeedbackSessionPreviewReminderEmailContext, RenderedEmail> previewRenderer) {
        List<EmailWrapper> emails = new ArrayList<>();
        for (FeedbackSessionParticipantReminderEmailContext context : participantContexts) {
            RenderedEmail renderedEmail = participantRenderer.apply(context);
            emails.add(EmailWrapperBuilder.build(
                    context.recipientEmailAddress(),
                    emailType,
                    renderedEmail,
                    context.courseName(),
                    context.feedbackSessionName()));
        }
        for (FeedbackSessionPreviewReminderEmailContext context : previewContexts) {
            RenderedEmail renderedEmail = previewRenderer.apply(context);
            EmailWrapper email = EmailWrapperBuilder.build(
                    context.recipientEmailAddress(),
                    emailType,
                    renderedEmail,
                    context.courseName(),
                    context.feedbackSessionName());
            // setSubjectFromType is called again to include the copy prefix in the subject
            email.setIsCopy(true);
            email.setSubjectFromType(context.courseName(), context.feedbackSessionName());
            emails.add(email);
        }
        emailQueueService.enqueueStandard(emails);
    }

    private void enqueueOwnerReminderEmails(
            List<FeedbackSessionOwnerReminderEmailContext> contexts,
            EmailType emailType,
            Function<FeedbackSessionOwnerReminderEmailContext, RenderedEmail> renderer) {
        List<EmailWrapper> emails = new ArrayList<>();
        for (FeedbackSessionOwnerReminderEmailContext context : contexts) {
            RenderedEmail renderedEmail = renderer.apply(context);
            emails.add(EmailWrapperBuilder.build(
                    context.recipientEmailAddress(),
                    emailType,
                    renderedEmail,
                    context.courseName(),
                    context.feedbackSessionName()));
        }
        emailQueueService.enqueueStandard(emails);
    }

}

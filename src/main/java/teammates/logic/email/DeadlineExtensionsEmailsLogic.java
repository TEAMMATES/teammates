package teammates.logic.email;

import java.util.List;

import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.RenderedEmail;

/**
 * Handles email-specific orchestration for deadline extension use cases.
 */
public class DeadlineExtensionsEmailsLogic {

    private static final DeadlineExtensionsEmailsLogic instance = new DeadlineExtensionsEmailsLogic();

    private EmailQueueService emailQueueService;

    public static DeadlineExtensionsEmailsLogic inst() {
        return instance;
    }

    /**
     * Initializes the outbound email queue dependency.
     */
    public void init(EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    /**
     * Enqueues deadline extension update emails for the given update contexts.
     */
    public void enqueueDeadlineExtensionUpdateEmails(
            FeedbackSessionEmailContext feedbackSessionContext, List<DeadlineExtensionUpdateEmailContext> emailContexts) {
        List<EmailWrapper> emails = emailContexts.stream()
                .map(emailContext -> buildEmail(feedbackSessionContext, emailContext))
                .toList();
        emailQueueService.enqueueStandard(emails);
    }

    private EmailWrapper buildEmail(
            FeedbackSessionEmailContext feedbackSessionContext, DeadlineExtensionUpdateEmailContext emailContext) {
        RenderedEmail renderedEmail =
                EmailRenderer.renderDeadlineExtensionUpdateEmail(feedbackSessionContext, emailContext);
        return EmailWrapperBuilder.build(
                emailContext.recipientEmailAddress(),
                emailContext.emailType(),
                renderedEmail,
                feedbackSessionContext.courseName(),
                feedbackSessionContext.feedbackSessionName());
    }
}

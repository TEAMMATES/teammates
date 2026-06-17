package teammates.logic.email;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
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

}

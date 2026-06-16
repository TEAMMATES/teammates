package teammates.logic.core;

import teammates.common.util.EmailWrapper;
import teammates.logic.email.EmailComposer;
import teammates.logic.email.EmailQueueService;
import teammates.logic.email.model.SessionLinksRecoveryContext;

/**
 * Handles email-specific orchestration for feedback session use cases.
 */
public class FeedbackSessionsEmailsLogic {

    private static final FeedbackSessionsEmailsLogic instance = new FeedbackSessionsEmailsLogic(
            EmailQueueService.inst(), EmailComposer.inst());

    private final EmailQueueService emailQueueService;
    private final EmailComposer emailComposer;

    FeedbackSessionsEmailsLogic(EmailQueueService emailQueueService, EmailComposer emailComposer) {
        this.emailQueueService = emailQueueService;
        this.emailComposer = emailComposer;
    }

    public static FeedbackSessionsEmailsLogic inst() {
        return instance;
    }

    /**
     * Enqueues the appropriate session links recovery email for the given
     * recovery context.
     */
    public void enqueueSessionLinksRecoveryEmail(SessionLinksRecoveryContext context) {
        EmailWrapper email = context.hasMatchingStudents()
                ? emailComposer.composeSessionLinksRecoveryEmail(context)
                : emailComposer.composeSessionLinksRecoveryNotFoundEmail(context.recoveryEmailAddress());
        emailQueueService.enqueuePriority(email);
    }

}

package teammates.logic.email;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.MagicLinkEmailContext;
import teammates.logic.email.model.RenderedEmail;

/**
 * Handles email-specific orchestration for magic-link sign-in use cases.
 */
public class MagicLinkEmailsLogic {

    private static final MagicLinkEmailsLogic instance = new MagicLinkEmailsLogic();

    private EmailQueueService emailQueueService;

    public static MagicLinkEmailsLogic inst() {
        return instance;
    }

    /**
     * Initializes the outbound email queue dependency.
     */
    public void init(EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    /**
     * Enqueues a magic-link sign-in email.
     */
    public void enqueueMagicLinkEmail(MagicLinkEmailContext context) {
        RenderedEmail renderedEmail = EmailRenderer.renderMagicLinkEmail(context);
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recipientEmailAddress(),
                EmailType.MAGIC_LINK_LOGIN,
                renderedEmail);
        emailQueueService.enqueuePriority(email);
    }
}

package teammates.logic.email;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.AccountVerificationApprovedEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
import teammates.logic.email.model.RenderedEmail;

/**
 * Handles email-specific orchestration for account verification use cases.
 */
public class AccountVerificationEmailsLogic {

    private static final AccountVerificationEmailsLogic instance = new AccountVerificationEmailsLogic();

    private EmailQueueService emailQueueService;

    public static AccountVerificationEmailsLogic inst() {
        return instance;
    }

    /**
     * Initializes the outbound email queue dependency.
     */
    public void init(EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    /**
     * Enqueues the admin alert for a newly created account verification request.
     */
    public void enqueueCreatedAdminAlertEmail(AccountVerificationCreatedAdminAlertEmailContext context) {
        RenderedEmail renderedEmail = EmailRenderer.renderAccountVerificationCreatedAdminAlertEmail(context);
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recipientEmailAddress(),
                EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT,
                renderedEmail);
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the submitter acknowledgement for a newly created account
     * verification request.
     */
    public void enqueueCreatedAcknowledgementEmail(AccountVerificationCreatedAcknowledgementEmailContext context) {
        RenderedEmail renderedEmail = EmailRenderer.renderAccountVerificationCreatedAcknowledgementEmail(context);
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recipientEmailAddress(),
                EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT,
                renderedEmail);
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the approval email for an approved account verification request.
     */
    public void enqueueApprovalEmail(AccountVerificationApprovedEmailContext context) {
        RenderedEmail renderedEmail = EmailRenderer.renderAccountVerificationApprovedEmail(context);
        EmailWrapper email = EmailWrapperBuilder.build(
                context.recipientEmailAddress(),
                EmailType.ACCOUNT_VERIFICATION_APPROVED,
                renderedEmail,
                context.recipientName());
        email.setBcc(Config.SUPPORT_EMAIL);
        emailQueueService.enqueuePriority(email);
    }
}

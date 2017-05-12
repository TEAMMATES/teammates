package teammates.ui.automated;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends queued admin email.
 */
public class AdminSendEmailWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String receiverEmail = getRequestParamValue(ParamsNames.ADMIN_EMAIL_RECEIVER);
        Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_RECEIVER, receiverEmail);

        String emailContent = getRequestParamValue(ParamsNames.ADMIN_EMAIL_CONTENT);
        String emailSubject = getRequestParamValue(ParamsNames.ADMIN_EMAIL_SUBJECT);

        if (emailContent == null || emailSubject == null) {
            String emailId = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ID);
            Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_ID, emailId);

            log.info("Sending large email. Going to retrieve email content and subject from datastore.");
            AdminEmailAttributes adminEmail = logic.getAdminEmailById(emailId);
            Assumption.assertNotNull(adminEmail);

            emailContent = adminEmail.getContentValue();
            emailSubject = adminEmail.getSubject();
        }

        Assumption.assertNotNull(emailContent);
        Assumption.assertNotNull(emailSubject);

        try {
            EmailWrapper email =
                    new EmailGenerator().generateAdminEmail(emailContent, emailSubject, receiverEmail);
            emailSender.sendEmail(email);
            log.info("Email sent to " + receiverEmail);
        } catch (Exception e) {
            log.severe("Unexpected error while sending admin emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}

package teammates.ui.automated;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.AdminEmailsLogic;

/**
 * Task queue worker action: sends queued admin email.
 */
public class AdminSendEmailWorkerAction extends AutomatedAction {
    
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
        Assumption.assertNotNull(receiverEmail);
        
        String emailContent = getRequestParamValue(ParamsNames.ADMIN_EMAIL_CONTENT);
        String emailSubject = getRequestParamValue(ParamsNames.ADMIN_EMAIL_SUBJECT);
        
        if (emailContent == null || emailSubject == null) {
            String emailId = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ID);
            Assumption.assertNotNull(emailId);
            
            log.info("Sending large email. Going to retrieve email content and subject from datastore.");
            AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);
            Assumption.assertNotNull(adminEmail);
            
            emailContent = adminEmail.getContent().getValue();
            emailSubject = adminEmail.getSubject();
        }
        
        Assumption.assertNotNull(emailContent);
        Assumption.assertNotNull(emailSubject);
        
        try {
            EmailWrapper email =
                    new EmailGenerator().generateAdminEmail(StringHelper.recoverFromSanitizedText(emailContent),
                                                            emailSubject, receiverEmail);
            emailSender.sendEmail(email);
            log.info("Email sent to " + receiverEmail);
        } catch (Exception e) {
            log.severe("Unexpected error while sending admin emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}

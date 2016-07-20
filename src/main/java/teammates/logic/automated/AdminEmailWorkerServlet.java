package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.EmailSender;

/**
 * Retrieves admin email content and subject by email id and sends email to the receiver
 */
@SuppressWarnings("serial")
public class AdminEmailWorkerServlet extends WorkerServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        
        String emailId = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);
        Assumption.assertNotNull(emailId);
        
        String receiverEmail = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_RECEIVER);
        Assumption.assertNotNull(receiverEmail);
        

        
        String emailContent = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_CONTENT);
        String emailSubject = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_SUBJECT);
        
        if (emailContent == null || emailSubject == null) {
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
            new EmailSender().sendEmail(email);
            log.info("Email sent to " + receiverEmail);
        } catch (Exception e) {
            log.severe("Unexpected error while sending admin emails: " + TeammatesException.toStringWithStackTrace(e));
        }

    }
    
}

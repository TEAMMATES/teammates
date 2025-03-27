package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.SendEmailRequest;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        SendEmailRequest emailRequest = getAndValidateRequestBody(SendEmailRequest.class);
        EmailWrapper email = emailRequest.getEmail();
        EmailSendingStatus status = emailSender.sendEmail(email);
        if (!status.isSuccess()) {
            // Set an arbitrary retry code outside of the range 200-299 so Cloud Tasks will automatically retry upon failure
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Successful");
    }

}

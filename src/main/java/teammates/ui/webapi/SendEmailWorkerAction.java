package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.ui.request.SendEmailRequest;

/**
 * Task queue worker action: sends queued email.
 */
class SendEmailWorkerAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        SendEmailRequest emailRequest = getAndValidateRequestBody(SendEmailRequest.class);
        EmailWrapper email = emailRequest.getEmail();
        EmailSendingStatus status = emailSender.sendEmail(email);
        if (!status.isSuccess()) {
            // Set an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Successful");
    }

}

package teammates.ui.automated;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: resend feedback sessions email to a user.
 */
public class FeedbackSessionResendEmailWorkerAction extends AutomatedAction {

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

        String userToResend = getNonNullRequestParamValue(ParamsNames.SUBMISSION_RESEND_LINK_USER);

        try {
            EmailWrapper email = new EmailGenerator().generateFeedbackSessionResendEmail(userToResend);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}

package teammates.logic.email;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.RenderedEmail;

/**
 * Utility for building outbound email wrappers with application defaults.
 */
public final class EmailWrapperBuilder {

    private EmailWrapperBuilder() {
        // utility class
    }

    /**
     * Creates an {@link EmailWrapper} for the given recipient, type and rendered
     * body.
     */
    public static EmailWrapper build(String recipientEmailAddress, EmailType emailType,
            RenderedEmail renderedEmail, Object... subjectParams) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipientEmailAddress);
        email.setSenderEmail(Config.EMAIL_SENDEREMAIL);
        email.setSenderName(Config.EMAIL_SENDERNAME);
        email.setReplyTo(Config.EMAIL_REPLYTO);
        email.setType(emailType);
        email.setSubjectFromType(subjectParams);
        email.setContent(renderedEmail.htmlContent());
        return email;
    }
}

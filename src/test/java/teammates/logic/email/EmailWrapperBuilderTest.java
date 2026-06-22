package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.RenderedEmail;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailWrapperBuilder}.
 */
public class EmailWrapperBuilderTest extends BaseTestCase {

    @Test
    public void build_validArguments_returnsWrappedEmail() {
        EmailWrapper actual = EmailWrapperBuilder.build(
                "student@teammates.tmt",
                EmailType.SESSION_LINKS_RECOVERY,
                new RenderedEmail("email-body"));

        assertEquals("student@teammates.tmt", actual.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, actual.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, actual.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, actual.getReplyTo());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY, actual.getType());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), actual.getSubject());
        assertEquals("email-body", actual.getContent());
    }

    @Test
    public void build_deadlineExtensionEmailWithSubjectParameters_returnsWrappedEmailWithFormattedSubject() {
        EmailWrapper actual = EmailWrapperBuilder.build(
                "student@teammates.tmt",
                EmailType.DEADLINE_EXTENSION_GRANTED,
                new RenderedEmail("email-body"),
                "Software Engineering",
                "Midterm Feedback");

        assertEquals("TEAMMATES: Deadline extension granted [Course: Software Engineering]"
                + "[Feedback Session: Midterm Feedback]", actual.getSubject());
    }
}

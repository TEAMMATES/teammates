package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailComposer}.
 */
public class EmailComposerTest extends BaseTestCase {

    private final EmailComposer emailComposer = EmailComposer.inst();

    @Test
    public void composeSessionLinksRecoveryEmail_courseSectionsExist_returnsRecoveryEmail() throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of(
                        new RecoverableCourseLinks(
                                "CS101",
                                "Software Engineering",
                                List.of(
                                        new RecoverableSessionLink(
                                                "Midterm Feedback",
                                                "https://example.com/submission",
                                                "https://example.com/results")))));

        EmailWrapper actual = emailComposer.composeSessionLinksRecoveryEmail(context);

        assertEquals("student@teammates.tmt", actual.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, actual.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, actual.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, actual.getReplyTo());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY, actual.getType());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), actual.getSubject());
        verifyEmailContent(actual, "/sessionLinksRecoveryComposerCourseSectionsExistEmail.html");
        assertFalse(actual.getContent().contains("${"));
    }

    @Test
    public void composeSessionLinksRecoveryEmail_courseSectionsDoNotExist_returnsRecoveryEmailWithoutLinks()
            throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of());

        EmailWrapper actual = emailComposer.composeSessionLinksRecoveryEmail(context);

        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), actual.getSubject());
        verifyEmailContent(actual, "/sessionLinksRecoveryComposerNoCourseSectionsEmail.html");
        assertFalse(actual.getContent().contains("${"));
    }

    @Test
    public void composeSessionLinksRecoveryNotFoundEmail_emailDoesNotMatchStudent_returnsNotFoundEmail()
            throws IOException {
        EmailWrapper actual = emailComposer.composeSessionLinksRecoveryNotFoundEmail("missing@teammates.tmt");

        assertEquals("missing@teammates.tmt", actual.getRecipient());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY, actual.getType());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), actual.getSubject());
        verifyEmailContent(actual, "/sessionLinksRecoveryComposerNotFoundEmail.html");
        assertFalse(actual.getContent().contains("${"));
    }

    private static void verifyEmailContent(EmailWrapper actual, String expectedEmailContentFilePathname)
            throws IOException {
        EmailChecker.verifyEmailContent(actual.getContent(), expectedEmailContentFilePathname);
    }

}

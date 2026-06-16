package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailRenderer}.
 */
public class EmailRendererTest extends BaseTestCase {

    @Test
    public void renderSessionLinksRecoveryEmail_courseSectionsExist_returnsRecoveryEmailBody() throws IOException {
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

        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryEmail(context);

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerCourseSectionsExistEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderSessionLinksRecoveryEmail_courseSectionsDoNotExist_returnsRecoveryEmailBodyWithoutLinks()
            throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of());

        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryEmail(context);

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerNoCourseSectionsEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderSessionLinksRecoveryNotFoundEmail_emailDoesNotMatchStudent_returnsNotFoundEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryNotFoundEmail("missing@teammates.tmt");

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerNotFoundEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    private static void verifyEmailContent(String actual, String expectedEmailContentFilePathname)
            throws IOException {
        EmailChecker.verifyEmailContent(actual, expectedEmailContentFilePathname);
    }

}

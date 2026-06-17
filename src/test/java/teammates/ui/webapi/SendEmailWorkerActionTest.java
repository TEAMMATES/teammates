package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import teammates.common.util.EmailWrapper;
import teammates.logic.email.MockEmailDeliveryService;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link SendEmailWorkerAction}.
 */
public class SendEmailWorkerActionTest extends BaseActionTest<SendEmailWorkerAction, MessageOutput> {

    private MockEmailDeliveryService mockEmailDeliveryService = new MockEmailDeliveryService();

    @AfterMethod
    public void tearDownMethod() {
        mockEmailDeliveryService.clearEmails();
    }

    @Override
    protected void configureAction(SendEmailWorkerAction action) {
        action.setEmailDeliveryService(mockEmailDeliveryService);
    }

    @Test(groups = GroupNames.ACTION)
    public void sendEmailWorkerAction_validEmail_deliversEmailAndReturnsSuccess() {
        SendEmailRequest request = new SendEmailRequest(buildEmail("recipient@test.tmt"));

        MessageOutput result = execute(new RequestContext().withWorkerAuth().withRequest(request));

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockEmailDeliveryService.getEmailsSent().size());
        EmailWrapper sentEmail = mockEmailDeliveryService.getEmailsSent().get(0);
        assertEquals("recipient@test.tmt", sentEmail.getRecipient());
        assertEquals("Test subject", sentEmail.getSubject());
        assertEquals("<p>Test content</p>", sentEmail.getContent());
    }

    @Test(groups = GroupNames.ACTION)
    public void sendEmailWorkerAction_nullEmail_throwsInvalidHttpRequestBodyException() {
        SendEmailRequest request = new SendEmailRequest(null);

        assertActionThrows(InvalidHttpRequestBodyException.class,
                new RequestContext().withWorkerAuth().withRequest(request));
    }

    private EmailWrapper buildEmail(String recipient) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipient);
        email.setSenderEmail("sender@test.tmt");
        email.setReplyTo("noreply@test.tmt");
        email.setSubject("Test subject");
        email.setContent("<p>Test content</p>");
        return email;
    }

}

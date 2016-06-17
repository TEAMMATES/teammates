package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Email;
import com.sendgrid.SendGrid;
import com.sun.jersey.multipart.FormDataMultiPart;

import teammates.common.util.EmailWrapper;
import teammates.logic.core.EmailSender;
import teammates.logic.core.JavamailService;
import teammates.logic.core.MailgunService;
import teammates.logic.core.MailjetService;
import teammates.logic.core.SendgridService;
import teammates.test.cases.BaseComponentTestCase;

/**
 * SUT: {@link EmailSender}
 */
public class EmailSenderTest extends BaseComponentTestCase {
    
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }
    
    private EmailWrapper getTypicalEmailWrapper() {
        String senderName = "Sender Name";
        String senderEmail = "sender@email.com";
        String replyTo = "replyto@email.com";
        List<String> recipientsList = Arrays.asList("recipient1@email.com", "recipient2@email.com");
        List<String> bccList = Arrays.asList("bcc1@email.com", "bcc2@email.com");
        String subject = "Test subject";
        String content = "<p>This is a test content</p>";
        
        EmailWrapper wrapper = new EmailWrapper();
        wrapper.setSenderName(senderName);
        wrapper.setSenderEmail(senderEmail);
        wrapper.setReplyTo(replyTo);
        for (String recipient : recipientsList) {
            wrapper.addRecipient(recipient);
        }
        for (String bcc : bccList) {
            wrapper.addBcc(bcc);
        }
        wrapper.setSubject(subject);
        wrapper.setContent(content);
        return wrapper;
    }
    
    @Test
    public void testNoExceptionThrownWhenNoMessagesToSend() {
        new EmailSender().sendEmails(new ArrayList<EmailWrapper>());
    }
    
    @Test
    public void testConvertToMimeMessage() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        MimeMessage email = new JavamailService().parseToEmail(wrapper);
        
        assertEquals(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName()), email.getFrom()[0]);
        assertEquals(new InternetAddress(wrapper.getReplyTo()), email.getReplyTo()[0]);
        
        List<Address> recipientsList = new ArrayList<Address>();
        for (String recipient : wrapper.getRecipientsList()) {
            recipientsList.add(new InternetAddress(recipient));
        }
        assertEquals(recipientsList, Arrays.asList(email.getRecipients(Message.RecipientType.TO)));
        
        List<Address> bccList = new ArrayList<Address>();
        for (String bcc : wrapper.getBccList()) {
            bccList.add(new InternetAddress(bcc));
        }
        assertEquals(bccList, Arrays.asList(email.getRecipients(Message.RecipientType.BCC)));
        
        assertEquals(wrapper.getSubject(), email.getSubject());
        assertEquals(wrapper.getContent(), email.getContent().toString());
    }
    
    @Test
    public void testConvertToSendgrid() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        SendGrid.Email email = new SendgridService().parseToEmail(wrapper);
        
        assertEquals(wrapper.getSenderEmail(), email.getFrom());
        assertEquals(wrapper.getSenderName(), email.getFromName());
        assertEquals(wrapper.getRecipientsList(), Arrays.asList(email.getTos()));
        assertEquals(wrapper.getBccList(), Arrays.asList(email.getBccs()));
        assertEquals(wrapper.getReplyTo(), email.getReplyTo());
        assertEquals(wrapper.getSubject(), email.getSubject());
        assertEquals(wrapper.getContent(), email.getHtml());
    }
    
    @Test
    public void testConvertToMailgun() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        FormDataMultiPart formData = new MailgunService().parseToEmail(wrapper);
        
        assertEquals(wrapper.getSenderName() + " <" + wrapper.getSenderEmail() + ">",
                     formData.getField("from").getValue());
        assertEquals(wrapper.getReplyTo(), formData.getField("h:Reply-To").getValue());
        assertEquals(wrapper.getSubject(), formData.getField("subject").getValue());
        assertEquals(wrapper.getContent(), formData.getField("html").getValue());
    }
    
    @Test
    public void testConvertToMailjet() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        MailjetRequest request = new MailjetService().parseToEmail(wrapper);
        JSONObject email = new JSONObject(request.getBody());
        
        assertEquals(wrapper.getSenderEmail(), email.get(Email.FROMEMAIL));
        assertEquals(wrapper.getSenderName(), email.get(Email.FROMNAME));
        assertEquals(wrapper.getReplyTo(),
                     ((JSONObject) email.get(Email.HEADERS)).getString("Reply-To"));
        assertEquals(wrapper.getSubject(), email.get(Email.SUBJECT));
        assertEquals(wrapper.getContent(), email.get(Email.HTMLPART));
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
}

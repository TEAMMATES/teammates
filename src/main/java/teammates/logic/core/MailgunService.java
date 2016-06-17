package teammates.logic.core;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

/**
 * Email sender service provided by Mailgun.
 * Reference: https://cloud.google.com/appengine/docs/java/mail/mailgun
 */
public class MailgunService implements EmailSenderService {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FormDataMultiPart parseToEmail(EmailWrapper wrapper) {
        FormDataMultiPart formData = new FormDataMultiPart();
        
        String sender = wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()
                        ? wrapper.getSenderEmail()
                        : wrapper.getSenderName() + " <" + wrapper.getSenderEmail() + ">";
        formData.field("from", sender);
        
        StringBuilder recipients = new StringBuilder();
        for (String recipient : wrapper.getRecipientsList()) {
            recipients.append(recipient).append(',');
        }
        formData.field("to", recipients.toString());
        
        if (!wrapper.getBccList().isEmpty()) {
            StringBuilder bccs = new StringBuilder();
            for (String bcc : wrapper.getBccList()) {
                bccs.append(bcc).append(',');
            }
            formData.field("bcc", bccs.toString());
        }
        
        formData.field("h:Reply-To", wrapper.getReplyTo());
        formData.field("subject", wrapper.getSubject());
        formData.field("html", wrapper.getContent());
        
        return formData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(EmailWrapper wrapper) {
        FormDataMultiPart email = parseToEmail(wrapper);
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", Config.MAILGUN_APIKEY));
        WebResource webResource =
                client.resource("https://api.mailgun.net/v3/" + Config.MAILGUN_DOMAINNAME + "/messages");
        
        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                                             .post(ClientResponse.class, email);
        if (response.getStatus() != SUCCESS_CODE) {
            log.severe("Email failed to send: " + response.getStatusInfo().getReasonPhrase());
        }
    }
    
}

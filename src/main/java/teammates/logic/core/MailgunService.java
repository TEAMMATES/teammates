package teammates.logic.core;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Email sender service provided by Mailgun.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/mail/mailgun">https://cloud.google.com/appengine/docs/java/mail/mailgun</a>
 * @see FormDataMultiPart
 */
public class MailgunService extends EmailSenderService {

    private static final Logger log = Logger.getLogger();

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

        formData.field("to", wrapper.getRecipient());

        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            formData.field("bcc", wrapper.getBcc());
        }

        formData.field("h:Reply-To", wrapper.getReplyTo());
        formData.field("subject", wrapper.getSubject());
        formData.field("html", wrapper.getContent());

        return formData;
    }

    @Override
    protected void sendEmailWithService(EmailWrapper wrapper) {
        try (FormDataMultiPart email = parseToEmail(wrapper)) {
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api", Config.MAILGUN_APIKEY));
            WebResource webResource =
                    client.resource("https://api.mailgun.net/v3/" + Config.MAILGUN_DOMAINNAME + "/messages");

            ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                    .post(ClientResponse.class, email);

            if (isNotSuccessStatus(response.getStatus())) {
                log.severe("Email failed to send: " + response.getStatusInfo().getReasonPhrase());
            }
        } catch (IOException e) {
            log.warning("Could not clean up resources after sending email: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}

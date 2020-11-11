package teammates.e2e.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.common.io.BaseEncoding;

/**
 * Provides an access to real Gmail inbox used for testing.
 *
 * <p>Authentication via a real username (Google ID) and password is required.
 */
public final class EmailAccount {

    private Gmail service;
    private String username;

    /**
     * Constructs an email client for the {@code username}.
     */
    public EmailAccount(String username) {
        this.username = username;
    }

    /**
     * Triggers the authentication process for the associated {@code username}.
     */
    public void getUserAuthenticated() throws IOException {
        // assume user is authenticated before
        service = new GmailServiceMaker(username).makeGmailService();

        int retryLimit = 5;
        while (retryLimit > 0) {
            try {
                retryLimit--;
                // touch one API endpoint to check authentication
                getListOfUnreadEmailFromSender(1L, "");
                break;
            } catch (HttpResponseException e) {
                if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_FORBIDDEN
                        || e.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED
                        || e.getStatusCode() == HttpStatusCodes.STATUS_CODE_BAD_REQUEST) {
                    System.out.println(e.getMessage());
                    // existing credential missing or not working, should do authentication for the account again
                    service = new GmailServiceMaker(username, true).makeGmailService();
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /**
     * Returns true if unread mail that arrived in the past minute contains mail with the specified subject.
     */
    public boolean isRecentEmailWithSubjectPresent(String subject, String senderEmail)
            throws IOException, MessagingException {

        List<Message> messageStubs = getListOfUnreadEmailFromSender(10L, senderEmail);

        for (Message messageStub : messageStubs) {
            Message message = service.users().messages().get(username, messageStub.getId()).setFormat("raw")
                    .execute();

            MimeMessage email = convertFromMessageToMimeMessage(message);
            boolean isSubjectEqual = email.getSubject().equals(subject);
            boolean isSentWithinLastMin =
                    message.getInternalDate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1);

            if (isSubjectEqual && isSentWithinLastMin) {
                markMessageAsRead(messageStub);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of up to maxResults number of unread emails from the sender.
     * Returns an empty list if there is no unread email from sender.
     */
    private List<Message> getListOfUnreadEmailFromSender(long maxResults, String senderEmail) throws IOException {
        List<Message> messageStubs = service.users().messages().list(username)
                .setQ("is:UNREAD from:" + senderEmail).setMaxResults(maxResults).execute()
                .getMessages();

        return messageStubs == null ? new ArrayList<>() : messageStubs;
    }

    private void markMessageAsRead(Message messageStub) throws IOException {
        ModifyMessageRequest modifyMessageRequest = new ModifyMessageRequest()
                .setRemoveLabelIds(Collections.singletonList("UNREAD"));
        service.users().messages().modify(username, messageStub.getId(), modifyMessageRequest).execute();
    }

    private MimeMessage convertFromMessageToMimeMessage(Message message) throws MessagingException {
        byte[] emailBytes = BaseEncoding.base64Url().decode(message.getRaw());

        // While we are not actually sending or receiving an email, a session is required so there will be strict parsing
        // of address headers when we create a MimeMessage. We are also passing in empty properties where we are expected to
        // supply some values because we are not actually sending or receiving any email.
        Session session = Session.getInstance(new Properties());

        return new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    }

    public String getUsername() {
        return username;
    }
}

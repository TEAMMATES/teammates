package teammates.e2e.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.common.io.BaseEncoding;

import teammates.common.util.EmailType;

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

        while (true) {
            try {
                // touch one API endpoint to check authentication
                getListOfUnreadEmailOfUser();
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
     * Retrieves the registration key among the unread emails
     * with {@code courseId} and {@code courseName} sent to the Gmail inbox.
     *
     * <p>After retrieving, marks the email as read.
     *
     * <p>If multiple emails of the same course are in the inbox, return the registration key presented in one of them.
     *
     * @return registration key (null if cannot be found).
     */
    public String getRegistrationKeyFromUnreadEmails(String courseName, String courseId)
            throws IOException, MessagingException {

        List<Message> messageStubs = getListOfUnreadEmailOfUser();

        for (Message messageStub : messageStubs) {
            Message message = service.users().messages().get(username, messageStub.getId()).setFormat("raw")
                    .execute();

            MimeMessage email = convertFromMessageToMimeMessage(message);

            if (isStudentCourseJoinRegistrationEmail(email, courseName, courseId)) {
                String body = getTextFromEmail(email);

                markMessageAsRead(messageStub);

                return getKey(body);
            }
        }

        return null;
    }

    /**
     * Returns true if unread mail contains mail with the specified subject.
     */
    public boolean isEmailWithSubjectPresent(String subject)
            throws IOException, MessagingException {

        List<Message> messageStubs = getListOfUnreadEmailOfUser();

        for (Message messageStub : messageStubs) {
            Message message = service.users().messages().get(username, messageStub.getId()).setFormat("raw")
                    .execute();

            MimeMessage email = convertFromMessageToMimeMessage(message);

            if (email.getSubject().equals(subject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks all unread emails in the user's inbox as read.
     */
    public void markAllUnreadEmailAsRead() throws IOException {
        List<Message> messageStubs = getListOfUnreadEmailOfUser();

        for (Message messageStub : messageStubs) {
            markMessageAsRead(messageStub);
        }
    }

    /**
     * Returns an empty list if there is no unread email of the user.
     */
    private List<Message> getListOfUnreadEmailOfUser() throws IOException {
        List<Message> messageStubs = service.users().messages().list(username).setQ("is:UNREAD").execute().getMessages();

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

    private boolean isStudentCourseJoinRegistrationEmail(MimeMessage message, String courseName, String courseId)
            throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.equals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                courseName, courseId));
    }

    /**
     * Gets the email message body as text.
     */
    private String getTextFromEmail(MimeMessage email) throws MessagingException, IOException {
        if (email.isMimeType("text/*")) {
            return (String) email.getContent();
        } else {
            return getTextFromPart(email);
        }
    }

    private String getTextFromPart(Part part) throws MessagingException, IOException {
        if (part.isMimeType("multipart/alternative")) {
            return getTextFromMultiPartAlternative((Multipart) part.getContent());
        } else if (part.isMimeType("multipart/digest")) {
            return getTextFromMultiPartDigest((Multipart) part.getContent());
        } else if (mimeTypeCanBeHandledAsMultiPartMixed(part)) {
            return getTextHandledAsMultiPartMixed(part);
        }

        return null;
    }

    /**
     * Returns if the part can be handled as multipart/mixed.
     */
    private boolean mimeTypeCanBeHandledAsMultiPartMixed(Part part) throws MessagingException {
        return part.isMimeType("multipart/mixed") || part.isMimeType("multipart/parallel")
                || part.isMimeType("message/rfc822")
                // as per the RFC2046 specification, other multipart subtypes are recognized as multipart/mixed
                || part.isMimeType("multipart/*");
    }

    private String getTextFromMultiPartDigest(Multipart multipart) throws IOException, MessagingException {
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("message/rfc822")) {
                String text = getTextFromPart(bodyPart);
                if (text != null) {
                    textBuilder.append(text);
                }
            }
        }
        String text = textBuilder.toString();

        if (text.isEmpty()) {
            return null;
        }

        return text;
    }

    /**
     * Returns the text from multipart/alternative, the type of text returned follows the preference of the sending agent.
     */
    private String getTextFromMultiPartAlternative(Multipart multipart) throws IOException, MessagingException {
        // search in reverse order as a multipart/alternative should have their most preferred format last
        for (int i = multipart.getCount() - 1; i >= 0; i--) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/html")) {
                return (String) bodyPart.getContent();
            } else if (bodyPart.isMimeType("text/plain")) {
                // Since we are looking in reverse order, if we did not encounter a text/html first we can return the plain
                // text because that is the best preferred format that we understand. If a text/html comes along later it
                // means the agent sending the email did not set the html text as preferable or did not set their preferred
                // order correctly, and in that case we do not handle that.
                return (String) bodyPart.getContent();
            } else if (bodyPart.isMimeType("multipart/*") || bodyPart.isMimeType("message/rfc822")) {
                String text = getTextFromPart(bodyPart);
                if (text != null) {
                    return text;
                }
            }
        }
        // we do not know how to handle the text in the multipart or there is no text
        return null;
    }

    private String getTextHandledAsMultiPartMixed(Part part) throws IOException, MessagingException {
        return getTextFromMultiPartMixed((Multipart) part.getContent());
    }

    private String getTextFromMultiPartMixed(Multipart multipart) throws IOException, MessagingException {
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/*")) {
                textBuilder.append((String) bodyPart.getContent());
            } else if (bodyPart.isMimeType("multipart/*")) {
                String text = getTextFromPart(bodyPart);
                if (text != null) {
                    textBuilder.append(text);
                }
            }
        }
        String text = textBuilder.toString();

        if (text.isEmpty()) {
            return null;
        }

        return text;
    }

    private String getKey(String body) {
        String key = body.substring(
                body.indexOf("key=") + "key=".length(),
                body.indexOf("studentemail=") - 1); //*If prompted to log in
        return key.trim();
    }

    public String getUsername() {
        return username;
    }
}

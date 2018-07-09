package teammates.test.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
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

    private EmailAccount() {
        // utility class
    }

    /**
     * Retrieve registration key sent to Gmail inbox. After retrieving, marks the email as read.
     *
     * @return registration key (null if cannot be found).
     */
    public static String getRegistrationKeyFromGmail(String username, String courseName, String courseId)
            throws IOException, MessagingException {

        Gmail service = new GmailServiceMaker(username).makeGmailService();

        ListMessagesResponse listMessagesResponse;
        while (true) {
            try {
                listMessagesResponse = service.users().messages().list(username).setQ("is:UNREAD").execute();
                break;
            } catch (GoogleJsonResponseException e) {
                if (e.getDetails().getCode() == HttpStatusCodes.STATUS_CODE_FORBIDDEN) {
                    System.out.println(e.getDetails().getMessage());
                    service = new GmailServiceMaker(username, true).makeGmailService();
                } else {
                    throw e;
                }
            }
        }

        final List<Message> messageStubs = listMessagesResponse.getMessages();

        if (isEmpty(messageStubs)) {
            return null;
        }

        for (Message messageStub : messageStubs) {
            final Message message = service.users().messages().get(username, messageStub.getId()).setFormat("raw")
                    .execute();

            final MimeMessage email = convertFromMessageToMimeMessage(message);

            if (isStudentCourseJoinRegistrationEmail(email, courseName, courseId)) {
                final String body = getTextFromEmail(email);

                markMessageAsRead(service, username, messageStub);

                return getKey(body);
            }
        }

        return null;
    }

    private static void markMessageAsRead(Gmail service, String username, Message messageStub) throws IOException {
        final ModifyMessageRequest modifyMessageRequest = new ModifyMessageRequest()
                .setRemoveLabelIds(Collections.singletonList("UNREAD"));
        service.users().messages().modify(username, messageStub.getId(), modifyMessageRequest).execute();
    }

    /**
     * Returns if the list of messages is empty.
     * @see ListMessagesResponse#getMessages()
     */
    private static boolean isEmpty(List<Message> messages) {
        return messages == null;
    }

    private static MimeMessage convertFromMessageToMimeMessage(Message message) throws MessagingException {
        byte[] emailBytes = BaseEncoding.base64Url().decode(message.getRaw());

        // While we are not actually sending or receiving an email, a session is required so there will be strict parsing
        // of address headers when we create a MimeMessage. We are also passing in empty properties where we are expected to
        // supply some values because we are not actually sending or receiving any email.
        Session session = Session.getInstance(new Properties());

        return new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    }

    private static boolean isStudentCourseJoinRegistrationEmail(MimeMessage message, String courseName, String courseId)
            throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.equals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                courseName, courseId));
    }

    /**
     * Gets the email message body as text.
     */
    private static String getTextFromEmail(MimeMessage email) throws MessagingException, IOException {
        if (email.isMimeType("text/*")) {
            return (String) email.getContent();
        } else {
            return getTextFromPart(email);
        }
    }

    private static String getTextFromPart(Part part) throws MessagingException, IOException {
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
    private static boolean mimeTypeCanBeHandledAsMultiPartMixed(Part part) throws MessagingException {
        return part.isMimeType("multipart/mixed") || part.isMimeType("multipart/parallel")
                || part.isMimeType("message/rfc822")
                // as per the RFC2046 specification, other multipart subtypes are recognized as multipart/mixed
                || part.isMimeType("multipart/*");
    }

    private static String getTextFromMultiPartDigest(Multipart multipart) throws IOException, MessagingException {
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
    private static String getTextFromMultiPartAlternative(Multipart multipart) throws IOException, MessagingException {
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

    private static String getTextHandledAsMultiPartMixed(Part part) throws IOException, MessagingException {
        return getTextFromMultiPartMixed((Multipart) part.getContent());
    }

    private static String getTextFromMultiPartMixed(Multipart multipart) throws IOException, MessagingException {
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

    private static String getKey(String body) {
        String key = body.substring(
                body.indexOf("key=") + "key=".length(),
                body.indexOf("studentemail=") - 1); //*If prompted to log in
        return key.trim();
    }
}

package teammates.test.driver;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

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
     * Retrieve registration key sent to Gmail inbox. After retrieving, marks
     * the email as read.
     *
     * @return registration key (null if cannot be found).
     */
    public static String getRegistrationKeyFromGmail(String username, String password, String courseName, String courseId)
            throws IOException, MessagingException {
        Folder inbox = getGmailInbox(username, password);
        Message[] messages = getMessages(inbox);

        // Loop over up to 5 unread messages at the top of the inbox
        int maxEmailsToCheck = Math.min(messages.length, 5);
        for (int i = messages.length - 1; i >= messages.length - maxEmailsToCheck; i--) {
            Message message = messages[i];

            if (isRegistrationEmail(message, courseName, courseId)) {
                String body = getEmailBody(message);
                message.setFlag(Flags.Flag.SEEN, true);
                inbox.close(true);
                return getKey(body);
            }
        }

        return null;
    }

    private static boolean isRegistrationEmail(Message message, String courseName, String courseId)
            throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.equals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                                                               courseName, courseId));
    }

    private static String getKey(String body) {
        String key = body.substring(
                body.indexOf("key=") + "key=".length(),
                body.indexOf("studentemail=") - 1); //*If prompted to log in
        return key.trim();
    }

    private static Folder getGmailInbox(String username, String password)
            throws MessagingException {
        Session session =
                Session.getDefaultInstance(System.getProperties(), null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", username, password);
        return store.getFolder("inbox");
    }

    private static Message[] getMessages(Folder inbox) throws MessagingException {
        // Reading the Email Index in Read / Write Mode
        inbox.open(Folder.READ_WRITE);
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        return inbox.search(ft);
    }

    private static String getEmailBody(Message message) throws IOException, MessagingException {
        String body = "";

        if (message.getContent() instanceof String) {
            body = message.getContent().toString();
        } else if (message.getContent() instanceof Multipart) {
            Multipart multipart = (Multipart) message.getContent();
            BodyPart bodypart = multipart.getBodyPart(0);
            body = bodypart.getContent().toString();
        }

        return body;
    }
}

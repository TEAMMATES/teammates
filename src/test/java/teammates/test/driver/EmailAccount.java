package teammates.test.driver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public final class EmailAccount {

    private EmailAccount() {
        // utility class
    }

    /**
     * Retrieve registration key sent to Gmail inbox. After retrieving, marks
     * the email as read.
     *      * Can be easily modified to support other mail providers
     *
     * @param username
     * @param password
     * @return registration key (null if cannot be found).
     * @throws MessagingException
     * @throws IOException
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

    /**
     * Helper function - Mark all emails of an account as read.
     *
     */
    public static void markAllEmailsSeen(String username, String password)
            throws Exception {
        Folder inbox = getGmailInbox(username, password);
        Message[] messages = getMessages(inbox);

        for (Message message : messages) {
            message.setFlag(Flags.Flag.SEEN, true);
        }

        inbox.close(true);
    }

    /**
     * Count the number of stress test emails
     *
     */
    public static int mailStressTestCount(String username, String password)
            throws Exception {
        Folder inbox = getGmailInbox(username, password);
        Message[] messages = getMessages(inbox);

        int count = 0;
        Pattern pattern = Pattern.compile("^Teammates Mail Stree Testing ");
        for (Message message : messages) {
            System.out.println(message.getSubject());
            Matcher m = pattern.matcher(message.getSubject());

            if (!m.find()) {
                continue;
            }
            count++;

        }

        inbox.close(true);
        return count;
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

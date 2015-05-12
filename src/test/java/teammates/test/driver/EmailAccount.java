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

import teammates.logic.core.Emails;

public class EmailAccount {

    /**
     * Retrieve registration key sent to Gmail inbox. After retrieving, marks 
     * the email as read.
     *      * Can be easily modified to support other mail providers
     * 
     * @param username
     * @param password
     * @return registration key (null if cannot be found).
     * @throws Exception
     */
    public static String getRegistrationKeyFromGmail(String username,
            String password, String courseId) {
        try {
            Folder inbox = getGmailInbox(username, password);
            Message[] messages = getMessages(inbox);
            
            // Loop over up to 5 unread messages at the top of the inbox
            int maxEmailsToCheck = Math.min(messages.length, 5);
            for (int i = messages.length - 1; i >= messages.length - maxEmailsToCheck; i--) {
                Message message = messages[i];
                
                if (isRegistrationEmail(message, courseId)) {
                    String body = getEmailBody(message);
                    String key = getKey(body);
                    message.setFlag(Flags.Flag.SEEN, true);
                    
                    inbox.close(true);
                    return key;
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isRegistrationEmail(Message message, String courseId)
            throws MessagingException {
        boolean isRegistrationEmail = false;
        String subject = message.getSubject();

        if (subject != null) {
            isRegistrationEmail = subject
                    .contains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN)
                    && (subject.contains(courseId));
        }

        return isRegistrationEmail;
    }

    private static String getKey(String body) {
        String key = body.substring(
                body.indexOf("key=") + "key=".length(),
                body.indexOf("studentemail=") - 1);//*If prompted to log in
        return key.trim();
    }


    /**
     * Checks whether the Publish had actually sent the e-mails to students
     * 
     * @param username
     * @param password
     * @param courseCode
     * @param evaluationName
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean checkResultEmailsSent(String username, String password,
            String courseCode, String evaluationName)
            throws MessagingException, IOException {

        // Publish RESULTS Format
        final String HEADER_EVALUATION_PUBLISH = "TEAMMATES: Evaluation Published: %s %s";
        final String TEAMMATES_APP_URL = "You can view the result here: "
                + TestProperties.inst().TEAMMATES_URL_IN_EMAILS;
        final String TEAMMATES_APP_SIGNATURE = "If you encounter any problems using the system, email TEAMMATES support";

        Folder inbox = getGmailInbox(username, password);
        Message[] messages = getMessages(inbox);
        System.out.println(messages.length + " unread message");

        // Loop over the last 5 messages
        for (int i = messages.length - 1; i >= messages.length - 5; i--) {
            Message message = messages[i];
            System.out.println(message.getSubject());

            System.out.println(String.format(HEADER_EVALUATION_PUBLISH,
                    courseCode, evaluationName));
            // matching email subject:
            if (!message.getSubject().equals(
                    String.format(HEADER_EVALUATION_PUBLISH, courseCode,
                            evaluationName))) {
                continue;
            } else {
                System.out.println("match");
            }

            String body = getEmailBody(message);

            // check line 1: "The results of the evaluation:"
            if (body.indexOf("The results of the evaluation:") == -1) {
                System.out.println("fail 1");
                continue;
            }
            // check line 2: courseCode evaluationName
            if (body.indexOf(body.indexOf(courseCode + " " + evaluationName)) == -1) {
                System.out.println("fail 2");
                continue;
            }
            // check line 3: "have been published."
            if (body.indexOf("have been published.") == -1) {
                System.out.println("fail 3");
                continue;
            }
            // check line 4: "You can view the result here: [URL]"
            if (body.indexOf(TEAMMATES_APP_URL) == -1) {
                System.out.println("fail 4");
                continue;

            }
            // check line 5: teammates signature
            if (body.indexOf(TEAMMATES_APP_SIGNATURE) == -1) {
                System.out.println("fail 5");
                continue;
            }

            // Mark the message as read
            message.setFlag(Flags.Flag.SEEN, true);
            inbox.close(true);
            return true;
        }
        
        inbox.close(true);
        return false;
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

            if (!m.find())
                continue;
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
        Message messages[] = inbox.search(ft);
                
        return messages;
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

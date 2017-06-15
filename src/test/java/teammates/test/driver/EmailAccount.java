package teammates.test.driver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
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

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EmailAccount() {
        // utility class
    }

    /**
     * Retrieve registration key sent to Gmail inbox. After retrieving, marks the email as read.
     *
     * @return registration key (null if cannot be found).
     */
    public static String getRegistrationKeyFromGmail(String username, String courseName, String courseId)
            throws IOException, MessagingException, GeneralSecurityException {

        // Build a new authorized API client service.
        Gmail service = getGmailService(username);

        String user = "me";

        // Get last 5 emails received by the user as there may be other emails received. However, this may fail unexpectedly
        // there are 5 additional emails received on top of the email from TEAMMATES.
        final ListMessagesResponse listMessagesResponse = service.users().messages().list(user).setMaxResults(5L)
                .setQ("is:unread").execute();

        final List<Message> messageStubs = listMessagesResponse.getMessages();
        if (!messageStubs.isEmpty()) {
            for (Message messageStub : messageStubs) {
                final Message message = service.users().messages().get(user, messageStub.getId()).setFormat("raw").execute();

                MimeMessage email = convertFromMessageToMimeMessage(message);

                if (isRegistrationEmail(email, courseName, courseId)) {
                    String body = getEmailMessageBodyAsText(email);

                    ModifyMessageRequest modifyMessageRequest = new ModifyMessageRequest()
                            .setRemoveLabelIds(Collections.singletonList("UNREAD"));
                    service.users().messages().modify(user, messageStub.getId(), modifyMessageRequest).execute();

                    return getKey(body);
                }
            }
        }

        return null;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     */
    private static Gmail getGmailService(String username) throws IOException {
        Credential credential = authorize(username);
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("teammates")
                .build();
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     */
    private static Credential authorize(String username) throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecretFromJson();

        System.out.println("Logging in as " + username);
        GoogleAuthorizationCodeFlow flow = buildFlow(username, clientSecrets);

        return getCredentialFromFlow(flow);
    }

    private static GoogleClientSecrets loadClientSecretFromJson() throws IOException {
        InputStream in = EmailAccount.class.getResourceAsStream("/client_secret.json");
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    private static GoogleAuthorizationCodeFlow buildFlow(String username, GoogleClientSecrets clientSecrets)
            throws IOException {
        // if the scopes ever need to change, the user will need to delete the credentials of the username found in
        // src/test/resources/<username>
        final List<String> scopes = Arrays.asList(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY);
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("src/test/resources", username));
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
    }

    /**
     * Gets the credential containing the access token from the flow if it exists. Otherwise a local server receiver is used
     * to receive authorization code and then exchanges the code for an access token.
     */
    private static Credential getCredentialFromFlow(GoogleAuthorizationCodeFlow flow) throws IOException {
        return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
    }

    private static MimeMessage convertFromMessageToMimeMessage(Message message) throws MessagingException {
        byte[] emailBytes = BaseEncoding.base64Url().decode(message.getRaw());

        // While we are not actually sending or receiving an email, a session is required so there will be strict parsing
        // of address headers when we create a MimeMessage. We are also passing in empty properties where we are expected to
        // supply some values because we are not sending or receiving any email.
        Session session = Session.getInstance(new Properties());

        return new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    }

    private static boolean isRegistrationEmail(MimeMessage message, String courseName, String courseId)
            throws MessagingException {
        String subject = message.getSubject();
        return subject != null && subject.equals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                courseName, courseId));
    }

    /**
     * Gets the email message body as text.
     */
    private static String getEmailMessageBodyAsText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            return (String) p.getContent();
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getEmailMessageBodyAsText(bp);
                    }
                    // We are not returning the plain text here because some mailers send the main body as both plain
                    // text and html. In this case, we will continue searching for html and return the html if
                    // we find it otherwise we will return the plain text
                } else if (bp.isMimeType("text/html")) {
                    String s = getEmailMessageBodyAsText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getEmailMessageBodyAsText(bp);
                }
            }
            // returns the plain text we cannot find html
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getEmailMessageBodyAsText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }

        return null;
    }

    private static String getKey(String body) {
        String key = body.substring(
                body.indexOf("key=") + "key=".length(),
                body.indexOf("studentemail=") - 1); //*If prompted to log in
        return key.trim();
    }
}

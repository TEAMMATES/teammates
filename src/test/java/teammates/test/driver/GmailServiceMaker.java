package teammates.test.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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

/**
 * Class that builds a Gmail service for use in Gmail API.
 */
final class GmailServiceMaker {

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

    private final String username;
    private final boolean shouldUseFreshCredentials;

    GmailServiceMaker(String username) {
        this(username, false);
    }

    GmailServiceMaker(String username, boolean shouldUseFreshCredentials) {
        this.username = username;
        this.shouldUseFreshCredentials = shouldUseFreshCredentials;
    }

    /**
     * Builds and returns an authorized Gmail client service.
     */
    Gmail makeGmailService() throws IOException {
        Credential credential = authorizeAndCreateCredentials();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("teammates")
                .build();
    }

    /**
     * Authorizes the user and creates an authorized Credential.
     * @return an authorized Credential
     */
    private Credential authorizeAndCreateCredentials() throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecretFromJson();

        GoogleAuthorizationCodeFlow flow = buildFlow(clientSecrets);

        if (shouldUseFreshCredentials) {
            flow.getCredentialDataStore().delete(username);
        }

        if (flow.getCredentialDataStore().get(username) == null) {
            System.out.println("Please login as: " + username);
        }

        return getCredentialFromFlow(flow);
    }

    private GoogleClientSecrets loadClientSecretFromJson() throws IOException {
        try (InputStream in = new FileInputStream(new File(TestProperties.TEST_GMAIL_API_FOLDER, "client_secret.json"))) {
            return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("You need to set up your Gmail API credentials." + System.lineSeparator()
                    + "See docs/development.md section \"Deploying to a staging server\".", e);
        }
    }

    private GoogleAuthorizationCodeFlow buildFlow(GoogleClientSecrets clientSecrets) throws IOException {
        // if the scopes need to change, the user will need to manually delete
        // <TestProperties.TEST_GMAIL_API_FOLDER>/StoredCredential
        final List<String> scopes = Arrays.asList(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY);
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TestProperties.TEST_GMAIL_API_FOLDER));
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
    private Credential getCredentialFromFlow(GoogleAuthorizationCodeFlow flow) throws IOException {
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(username);
    }
}

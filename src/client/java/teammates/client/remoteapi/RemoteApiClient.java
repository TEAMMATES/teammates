package teammates.client.remoteapi;

import java.io.IOException;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.storage.api.OfyHelper;
import teammates.test.driver.TestProperties;

/**
 * Enables access to any Datastore (local/production).
 *
 * @see <a href="https://cloud.google.com/appengine/docs/standard/java/tools/remoteapi">https://cloud.google.com/appengine/docs/standard/java/tools/remoteapi</a>
 */
public abstract class RemoteApiClient {

    protected Objectify ofy() {
        return com.googlecode.objectify.ObjectifyService.ofy();
    }

    protected void doOperationRemotely() throws IOException {

        String appUrl = TestProperties.TEAMMATES_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        RemoteApiOptions options = new RemoteApiOptions().server(appDomain, appPort);

        if (TestProperties.isDevServer()) {
            // Dev Server doesn't require credential.
            options.useDevelopmentServerCredential();
        } else {
            // Your Google Cloud SDK needs to be authenticated for Application Default Credentials
            // in order to run any script in production server.
            // Refer to https://developers.google.com/identity/protocols/application-default-credentials.
            options.useApplicationDefaultCredential();
        }

        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);

        OfyHelper.registerEntityClasses();
        Closeable objectifySession = ObjectifyService.begin();

        try {
            doOperation();
        } finally {
            objectifySession.close();
            installer.uninstall();
        }

        System.out.println("--- Remote operation completed ---");
    }

    /**
     * Prints the {@code string} on system output, followed by a newline.
     */
    protected void println(String string) {
        System.out.println(string);
    }

    /**
     * This operation is meant to be overridden by child classes.
     */
    protected abstract void doOperation();
}

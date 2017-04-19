package teammates.client.remoteapi;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.jdo.PersistenceManager;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import teammates.storage.api.CoursesDb;
import teammates.storage.api.EntitiesDb;
import teammates.test.driver.TestProperties;

public abstract class RemoteApiClient {

    protected static final PersistenceManager PM = getPm();

    private static PersistenceManager getPm() {
        try {
            // use reflection to bypass the visibility level of the method
            Method method = EntitiesDb.class.getDeclaredMethod("getPm");
            method.setAccessible(true);

            // the method is non-static and EntitiesDb is an abstract class; use any *Db to invoke it
            return (PersistenceManager) method.invoke(new CoursesDb());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
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
            // If you are trying to run script on Staging Server:
            // Step 1: Install Google Cloud SDK in your local PC first, https://cloud.google.com/sdk/downloads
            // Step 2: Run `gcloud auth login` in the terminal and choose your google account
            // Step 3: Run the script again.
            options.useApplicationDefaultCredential();
        }

        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            doOperation();
        } finally {
            installer.uninstall();
        }

        System.out.println("--- Remote operation completed ---");
    }

    /**
     * This operation is meant to be overridden by child classes.
     */
    protected abstract void doOperation();
}

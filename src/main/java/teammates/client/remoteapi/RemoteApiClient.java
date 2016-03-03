package teammates.client.remoteapi;

import java.io.IOException;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import teammates.test.driver.TestProperties;

public abstract class RemoteApiClient {
    private final String LOCALHOST = "localhost";
    
    protected void doOperationRemotely() throws IOException {
        TestProperties testProperties = TestProperties.inst();

        String appDomain = testProperties.TEAMMATES_REMOTEAPI_APP_DOMAIN;
        int appPort = testProperties.TEAMMATES_REMOTEAPI_APP_PORT;
        
        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        RemoteApiOptions options = new RemoteApiOptions().server(appDomain, appPort);

        boolean isDevServer = appDomain.equals(LOCALHOST);
        if (isDevServer) {
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

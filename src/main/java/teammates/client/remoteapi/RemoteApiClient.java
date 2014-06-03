package teammates.client.remoteapi;

import java.io.IOException;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import teammates.test.driver.TestProperties;

public abstract class RemoteApiClient {

    protected void doOperationRemotely() throws IOException {
        TestProperties testProperties = TestProperties.inst();

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:"
                + testProperties.TEAMMATES_REMOTEAPI_APP_DOMAIN + ":"
                + testProperties.TEAMMATES_REMOTEAPI_APP_PORT);

        RemoteApiOptions options = new RemoteApiOptions().server(
                testProperties.TEAMMATES_REMOTEAPI_APP_DOMAIN,
                testProperties.TEAMMATES_REMOTEAPI_APP_PORT).credentials(
                testProperties.TEST_ADMIN_ACCOUNT,
                testProperties.TEST_ADMIN_PASSWORD);

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

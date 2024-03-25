package teammates.client.connector;

import com.google.cloud.NoCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.client.util.ClientProperties;
import teammates.common.util.Config;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;

/**
 * Enables access to any datastore (local/production).
 */
public abstract class DatastoreClient {

    /**
     * Gets the Objectify instance.
     */
    protected Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Performs the entire operation routine: setting up connection to the back-end,
     * performing the operation itself, and tearing down the connection.
     */
    protected void doOperationRemotely() {

        String appUrl = ClientProperties.TARGET_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        if (ClientProperties.isTargetUrlDevServer()) {
            builder.setHost(ClientProperties.TARGET_URL);
            builder.setCredentials(NoCredentials.getInstance());
        }
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();

        try (Closeable ignored = ObjectifyService.begin()) {
            LogicStarter.initializeDependencies();
            doOperation();
        }

        System.out.println("--- Remote operation completed ---");
    }

    /**
     * Performs the remote operation to the back-end.
     */
    protected abstract void doOperation();
}

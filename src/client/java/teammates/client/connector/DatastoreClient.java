package teammates.client.connector;

import java.io.IOException;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.client.util.ClientProperties;
import teammates.common.util.Config;
import teammates.storage.api.OfyHelper;

/**
 * Enables access to any Datastore (local/production).
 */
public abstract class DatastoreClient {

    protected Objectify ofy() {
        return ObjectifyService.ofy();
    }

    protected void doOperationRemotely() throws IOException {

        String appUrl = ClientProperties.TARGET_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        if (ClientProperties.isTargetUrlDevServer()) {
            builder.setHost(ClientProperties.TARGET_URL);
        }
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();
        Closeable objectifySession = ObjectifyService.begin();

        try {
            doOperation();
        } finally {
            objectifySession.close();
        }

        System.out.println("--- Remote operation completed ---");
    }

    /**
     * This operation is meant to be overridden by child classes.
     */
    protected abstract void doOperation();
}

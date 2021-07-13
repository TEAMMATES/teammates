package teammates.storage.api;

import java.io.IOException;
import java.nio.file.Paths;

import com.google.cloud.datastore.testing.LocalDatastoreHelper;

import teammates.common.util.Config;

/**
 * Runs a local Datastore emulator instance.
 */
// CHECKSTYLE.OFF:UncommentedMain
public final class DatastoreEmulatorRunner {

    private DatastoreEmulatorRunner() {
        // prevent initialization
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        LocalDatastoreHelper localDatastoreHelper = LocalDatastoreHelper.newBuilder()
                .setConsistency(0.9) // default setting
                .setPort(Config.APP_LOCALDATASTORE_PORT)
                .setStoreOnDisk(true)
                .setDataDir(Paths.get("datastore-dev/datastore"))
                .build();
        localDatastoreHelper.start();
    }

}

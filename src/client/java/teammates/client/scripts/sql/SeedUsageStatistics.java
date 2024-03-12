package teammates.client.scripts.sql;

import java.time.Instant;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import teammates.client.connector.DatastoreClient;
import teammates.common.util.Config;
import teammates.storage.api.OfyHelper;
import teammates.storage.entity.UsageStatistics;

/**
 * Seeds the usage statistics table with dummy data.
 */
public class SeedUsageStatistics extends DatastoreClient {

    public static void main(String[] args) {
        setupObjectify();
        new SeedUsageStatistics().doOperationRemotely();
    }

    private static void setupObjectify() {
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();
        ObjectifyService.begin();
    }

    @Override
    protected void doOperation() {
        persistDummyUsageStatistics();
    }

    private void persistDummyUsageStatistics() {
        Instant startTimeOne = Instant.parse("2012-01-01T00:00:00Z");
        UsageStatistics usageStatisticsOne = new UsageStatistics(
                startTimeOne, 1, 1, 2, 3, 4, 5, 6, 7);

        Instant startTimeTwo = Instant.parse("2012-01-02T00:00:00Z");
        UsageStatistics usageStatisticsTwo = new UsageStatistics(
                startTimeTwo, 1, 2, 2, 2, 2, 2, 2, 2);

        ofy().save().entities(usageStatisticsOne, usageStatisticsTwo).now(); // save synchronously
    }
}

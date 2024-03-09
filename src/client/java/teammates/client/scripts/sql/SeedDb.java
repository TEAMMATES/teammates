package teammates.client.scripts.sql;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.common.util.Config;
import teammates.client.connector.DatastoreClient;
import teammates.client.scripts.GenerateUsageStatisticsObjects;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.test.FileHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Notification;

public class SeedDb extends DatastoreClient {
    private final LogicExtension logic = new LogicExtension();

    private Closeable closeable;

    public void setupDbLayer() throws Exception {
        LogicStarter.initializeDependencies();
    }

    public void setupObjectify() {
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();

        closeable = ObjectifyService.begin();
    }

    public void tearDownObjectify() {
        closeable.close();
    }

    protected String getSrcFolder() {
        return "src/client/java/teammates/client/scripts/sql/";
    }

    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getSrcFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    protected Instant getRandomInstant() {
        return Instant.now();
    }

    protected void persistAdditionalData() {
        int ENTITY_SIZE = 10000;
        // Each account will have this amount of read notifications
        int READ_NOTIFICATION_SIZE = 5;
        int NOTIFICATION_SIZE = 1000;
        assert (NOTIFICATION_SIZE >= READ_NOTIFICATION_SIZE);

        String[] args = {};

        Set<String> notificationsUUIDSeen = new HashSet<String>();
        ArrayList<String> notificationUUIDs = new ArrayList<>();
        HashMap<String, Instant> notificationEndTimes = new HashMap<>();

        Random rand = new Random();

        for (int j = 0; j < NOTIFICATION_SIZE; j++) {
            UUID notificationUUID = UUID.randomUUID();
            while (notificationsUUIDSeen.contains(notificationUUID.toString())) {
                notificationUUID = UUID.randomUUID();
            }
            notificationUUIDs.add(notificationUUID.toString());
            notificationsUUIDSeen.add(notificationUUID.toString());
            // Since we are not using logic class, referencing
            // MarkNotificationAsReadAction.class and CreateNotificationAction.class
            // endTime is to nearest milli not nanosecond
            Instant endTime = getRandomInstant().truncatedTo(ChronoUnit.MILLIS);
            Notification notification = new Notification(
                    notificationUUID.toString(),
                    getRandomInstant(),
                    endTime,
                    NotificationStyle.PRIMARY,
                    NotificationTargetUser.INSTRUCTOR,
                    notificationUUID.toString(),
                    notificationUUID.toString(),
                    false,
                    getRandomInstant(),
                    getRandomInstant());
            try {
                ofy().save().entities(notification).now();
                notificationEndTimes.put(notificationUUID.toString(), notification.getEndTime());
            } catch (Exception e) {
                log(e.toString());
            }
        }

        for (int i = 0; i < ENTITY_SIZE; i++) {

            if (i % (ENTITY_SIZE / 5) == 0) {
                log(String.format("Seeded the %d %% of new sets of entities",
                        (int) (100 * ((float) i / (float) ENTITY_SIZE))));
            }

            try {
                String accountRequestName = String.format("Account Request %s", i);
                String accountRequestEmail = String.format("Account Email %s", i);
                String accountRequestInstitute = String.format("Account Institute %s", i);
                AccountRequest accountRequest = AccountRequestAttributes
                        .builder(accountRequestName, accountRequestEmail, accountRequestInstitute)
                        .withRegisteredAt(Instant.now()).build().toEntity();

                String accountGoogleId = String.format("Account Google ID %s", i);
                String accountName = String.format("Account name %s", i);
                String accountEmail = String.format("Account email %s", i);
                Map<String, Instant> readNotificationsToCreate = new HashMap<>();

                for (int j = 0; j < READ_NOTIFICATION_SIZE; j++) {
                    int randIndex = rand.nextInt(NOTIFICATION_SIZE);
                    String notificationUUID = notificationUUIDs.get(randIndex);
                    assert (notificationEndTimes.get(notificationUUID) != null);
                    readNotificationsToCreate.put(notificationUUID, notificationEndTimes.get(notificationUUID));

                }
                Account account = new Account(accountGoogleId, accountName,
                        accountEmail, readNotificationsToCreate, true);

                ofy().save().entities(account).now();
                ofy().save().entities(accountRequest).now();
            } catch (Exception e) {
                log(e.toString());
            }
        }

        GenerateUsageStatisticsObjects.main(args);
    }

    private void log(String logLine) {
        System.out.println(String.format("Seeding database: %s", logLine));
    }

    protected void persistData() {
        // Persisting basic data bundle
        DataBundle dataBundle = getTypicalDataBundle();
        try {
            logic.persistDataBundle(dataBundle);
            persistAdditionalData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new SeedDb().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        try {
            // LogicStarter.initializeDependencies();
            this.persistData();
        } catch (Exception e) {

        }
    }
}

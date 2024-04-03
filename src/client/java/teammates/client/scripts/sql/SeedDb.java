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

import teammates.client.connector.DatastoreClient;
import teammates.client.scripts.GenerateUsageStatisticsObjects;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Notification;
import teammates.test.FileHelper;

/**
 * SeedDB class.
 */
@SuppressWarnings("PMD")
public class SeedDb extends DatastoreClient {
    private final LogicExtension logic = new LogicExtension();

    private static final int MAX_ENTITY_SIZE = 10000;

    private Closeable closeable;

    /**
     * Sets up the dependencies needed for the DB layer.
     */
    public void setupDbLayer() throws Exception {
        LogicStarter.initializeDependencies();
    }

    /**
     * Sets up objectify service.
     */
    public void setupObjectify() {
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();

        closeable = ObjectifyService.begin();
    }

    /**
     * Closes objectify service.
     */
    public void tearDownObjectify() {
        closeable.close();
    }

    protected String getSrcFolder() {
        return "src/client/java/teammates/client/scripts/sql/";
    }

    /**
     * Loads the data bundle from JSON file.
     */
    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getSrcFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the typical data bundle.
     */
    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    /**
     * Gets a random instant.
     */
    protected Instant getRandomInstant() {
        return Instant.now();
    }

    /**
     * Persists additional data.
     */
    protected void persistAdditionalData() {
        String[] args = {};
        // Each account will have this amount of read notifications
        seedNotificationAccountAndAccountRequest(5, 1000);
        seedCourse();

        GenerateUsageStatisticsObjects.main(args);
    }

    private void seedCourse() {
        log("Seeding courses");
        for (int i = 0; i < MAX_ENTITY_SIZE; i++) {
            if (i % (MAX_ENTITY_SIZE / 5) == 0) {
                log(String.format("Seeded %d %% of new sets of entities",
                        (int) (100 * ((float) i / (float) MAX_ENTITY_SIZE))));
            }
            try {
                String courseName = String.format("Course %s", i);
                String courseInstitute = String.format("Institute %s", i);
                String courseTimeZone = String.format("Time Zone %s", i);
                Course course = new Course(UUID.randomUUID().toString(), courseName, courseTimeZone, courseInstitute,
                        getRandomInstant(), getRandomInstant(), false);
                ofy().save().entities(course).now();
            } catch (Exception e) {
                log(e.toString());
            }
        }
    }

    private void seedNotificationAccountAndAccountRequest(int constReadNotificationSize, int constNotificationSize) {
        assert constNotificationSize >= constReadNotificationSize;
        log("Seeding Notifications, Account and Account Request");

        Set<String> notificationsUuidSeen = new HashSet<String>();
        ArrayList<String> notificationUuids = new ArrayList<>();
        Map<String, Instant> notificationEndTimes = new HashMap<>();

        Random rand = new Random();

        for (int j = 0; j < constNotificationSize; j++) {
            UUID notificationUuid = UUID.randomUUID();
            while (notificationsUuidSeen.contains(notificationUuid.toString())) {
                notificationUuid = UUID.randomUUID();
            }
            notificationUuids.add(notificationUuid.toString());
            notificationsUuidSeen.add(notificationUuid.toString());
            // Since we are not using logic class, referencing
            // MarkNotificationAsReadAction.class and CreateNotificationAction.class
            // endTime is to nearest milli not nanosecond
            Instant endTime = getRandomInstant().truncatedTo(ChronoUnit.MILLIS);
            Notification notification = new Notification(
                    notificationUuid.toString(),
                    getRandomInstant(),
                    endTime,
                    NotificationStyle.PRIMARY,
                    NotificationTargetUser.INSTRUCTOR,
                    notificationUuid.toString(),
                    notificationUuid.toString(),
                    false,
                    getRandomInstant(),
                    getRandomInstant());
            try {
                ofy().save().entities(notification).now();
                notificationEndTimes.put(notificationUuid.toString(), notification.getEndTime());
            } catch (Exception e) {
                log(e.toString());
            }
        }

        for (int i = 0; i < MAX_ENTITY_SIZE; i++) {

            if (i % (MAX_ENTITY_SIZE / 5) == 0) {
                log(String.format("Seeded %d %% of new sets of entities",
                        (int) (100 * ((float) i / (float) MAX_ENTITY_SIZE))));
            }

            try {
                String accountRequestName = String.format("Account Request %s", i);
                String accountRequestEmail = String.format("Account Email %s", i);
                String accountRequestInstitute = String.format("Account Institute %s", i);
                AccountRequest accountRequest = AccountRequestAttributes
                        .builder(accountRequestEmail, accountRequestInstitute, accountRequestName)
                        .withRegisteredAt(Instant.now()).build().toEntity();

                String accountGoogleId = String.format("Account Google ID %s", i);
                String accountName = String.format("Account name %s", i);
                String accountEmail = String.format("Account email %s", i);
                Map<String, Instant> readNotificationsToCreate = new HashMap<>();

                for (int j = 0; j < constReadNotificationSize; j++) {
                    int randIndex = rand.nextInt(constNotificationSize);
                    String notificationUuid = notificationUuids.get(randIndex);
                    assert notificationEndTimes.get(notificationUuid) != null;
                    readNotificationsToCreate.put(notificationUuid, notificationEndTimes.get(notificationUuid));
                }

                Account account = new Account(accountGoogleId, accountName,
                        accountEmail, readNotificationsToCreate, false);

                ofy().save().entities(account).now();
                ofy().save().entities(accountRequest).now();
            } catch (Exception e) {
                log(e.toString());
            }
        }
    }

    private void log(String logLine) {
        System.out.println(String.format("Seeding database: %s", logLine));
    }

    /**
     * Persists the data to database.
     */
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
            e.printStackTrace();
        }
    }
}

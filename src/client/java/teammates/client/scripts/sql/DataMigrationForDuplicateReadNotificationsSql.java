package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.ReadNotification;
import teammates.test.FileHelper;

// CHECKSTYLE.ON:ImportOrder

/**
 * Data migration script to remove duplicate ReadNotification entries.
 * Keeps only one ReadNotification per (accountId, notificationId) pair.
 */
@SuppressWarnings("PMD")
public class DataMigrationForDuplicateReadNotificationsSql extends DatastoreClient {
    // the folder where the cursor position and console output is saved as a file
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfScannedEntities;
    AtomicLong numberOfDuplicatesFound;
    AtomicLong numberOfDuplicatesRemoved;

    private DataMigrationForDuplicateReadNotificationsSql() {
        numberOfScannedEntities = new AtomicLong();
        numberOfDuplicatesFound = new AtomicLong();
        numberOfDuplicatesRemoved = new AtomicLong();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new DataMigrationForDuplicateReadNotificationsSql().doOperationRemotely();
    }

    /**
     * Returns the log prefix.
     */
    protected String getLogPrefix() {
        return "Duplicate ReadNotifications Removing:";
    }

    private boolean isPreview() {
        return false;
    }

    public void doOperationRemotely() {
        log("Migration started");

        try {
            HibernateUtil.beginTransaction();
            removeDuplicateReadNotifications();
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            log("Error: " + e.getMessage());
            e.printStackTrace();
        }

        log(String.format("Scanned: %d", numberOfScannedEntities.get()));
        log(String.format("Duplicates Found: %d", numberOfDuplicatesFound.get()));
        log(String.format("Duplicates Removed: %d", numberOfDuplicatesRemoved.get()));
        log("Migration completed");
    }

    private void removeDuplicateReadNotifications() {
        // Query all ReadNotifications
        List<ReadNotification> allReadNotifications = HibernateUtil.createQuery(
            HibernateUtil.getCriteriaBuilder().createQuery(ReadNotification.class)
        ).getResultList();

        numberOfScannedEntities.set(allReadNotifications.size());
        log("Total ReadNotifications found: " + allReadNotifications.size());

        // Group by (accountId, notificationId) to find duplicates
        Map<String, List<ReadNotification>> groupedByAccountNotification = new HashMap<>();
        for (ReadNotification rn : allReadNotifications) {
            String key = rn.getAccount().getId() + "::" + rn.getNotification().getId();
            groupedByAccountNotification.computeIfAbsent(key, k -> new ArrayList<>()).add(rn);
        }

        // Find and mark duplicates for deletion
        List<UUID> idsToDelete = new ArrayList<>();
        for (List<ReadNotification> group : groupedByAccountNotification.values()) {
            if (group.size() > 1) {
                numberOfDuplicatesFound.addAndGet(group.size());
                log("Found " + group.size() + " duplicates for account="
                    + group.get(0).getAccount().getId() + ", notification="
                    + group.get(0).getNotification().getId());

                // Keep the first one, mark the rest for deletion
                for (int i = 1; i < group.size(); i++) {
                    idsToDelete.add(group.get(i).getId());
                }
            }
        }

        // Delete duplicates
        if (!idsToDelete.isEmpty()) {
            for (UUID id : idsToDelete) {
                ReadNotification rn = HibernateUtil.get(ReadNotification.class, id);
                if (rn != null) {
                    HibernateUtil.remove(rn);
                    numberOfDuplicatesRemoved.incrementAndGet();
                }
            }
            log("Deleted " + numberOfDuplicatesRemoved.get() + " duplicate ReadNotifications");
        }
    }
}

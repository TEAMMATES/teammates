package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.test.FileHelper;

/**
 * Patch createdAt attribute for Notification.
 * Assumes that the notification was previously migrated using DataMigrationForNotificationSql.java
 */
@SuppressWarnings("PMD")
public class PatchCreatedAtTimeNotification extends DatastoreClient {
    // the folder where the cursor position and console output is saved as a file
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    // 100 is the optimal batch size as there won't be too much time interval
    // between read and save (if transaction is not used)
    // cannot set number greater than 300
    // see
    // https://stackoverflow.com/questions/41499505/objectify-queries-setting-limit-above-300-does-not-work
    private static final int BATCH_SIZE = 100;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfScannedKey;
    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfUpdatedEntities;

    private PatchCreatedAtTimeNotification() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new PatchCreatedAtTimeNotification().doOperationRemotely();
    }

    /**
     * Returns the log prefix.
     */
    protected String getLogPrefix() {
        return String.format("Notification Patch Migration:");
    }

    private boolean isPreview() {
        return false;
    }

    /**
     * Returns whether the account has been migrated.
     */
    protected boolean isMigrationNeeded(teammates.storage.entity.Notification entity) {
        return true;
    }

    /**
     * Returns the filter query.
     */
    protected Query<teammates.storage.entity.Notification> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Notification.class);
    }

    private void doMigration(teammates.storage.entity.Notification entity) {
        try {
            if (!isMigrationNeeded(entity)) {
                return;
            }
            if (!isPreview()) {
                migrateEntity(entity);
            }
        } catch (Exception e) {
            logError("Problem patching usage stats " + entity);
            logError(e.getMessage());
        }
    }

    /**
     * Migrates the entity. In this case, add entity to buffer.
     */
    protected void migrateEntity(teammates.storage.entity.Notification oldEntity) {
        HibernateUtil.beginTransaction();

        UUID notificationId = UUID.fromString(oldEntity.getNotificationId());
        teammates.storage.sqlentity.Notification newNotif =
                HibernateUtil.get(teammates.storage.sqlentity.Notification.class, notificationId);
        if (newNotif == null) {
            HibernateUtil.commitTransaction();
            throw new Error("No notification found with id " + notificationId);
        }

        newNotif.setCreatedAt(oldEntity.getCreatedAt());
        HibernateUtil.commitTransaction();
        numberOfAffectedEntities.incrementAndGet();
        numberOfUpdatedEntities.incrementAndGet();
    }

    @Override
    protected void doOperation() {
        log("Running " + getClass().getSimpleName() + "...");
        log("Preview: " + isPreview());

        Cursor cursor = readPositionOfCursorFromFile().orElse(null);
        if (cursor == null) {
            log("Start from the beginning");
        } else {
            log("Start from cursor position: " + cursor.toUrlSafe());
        }

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<teammates.storage.entity.Notification> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<teammates.storage.entity.Notification> iterator;

            iterator = filterQueryKeys.iterator();

            while (iterator.hasNext()) {
                shouldContinue = true;

                doMigration(iterator.next());

                numberOfScannedKey.incrementAndGet();
            }

            if (shouldContinue) {
                cursor = iterator.getCursorAfter();
                savePositionOfCursorToFile(cursor);
                log(String.format("Cursor Position: %s", cursor.toUrlSafe()));
                log(String.format("Number Of Entity Key Scanned: %d", numberOfScannedKey.get()));
                log(String.format("Number Of Entity affected: %d", numberOfAffectedEntities.get()));
                log(String.format("Number Of Entity updated: %d", numberOfUpdatedEntities.get()));
            }
        }

        deleteCursorPositionFile();
        log(isPreview() ? "Preview Completed!" : "Migration Completed!");
        log("Total number of entities: " + numberOfScannedKey.get());
        log("Number of affected entities: " + numberOfAffectedEntities.get());
        log("Number of updated entities: " + numberOfUpdatedEntities.get());
    }

    /**
     * Saves the cursor position to a file so it can be used in the next run.
     */
    private void savePositionOfCursorToFile(Cursor cursor) {
        try {
            FileHelper.saveFile(
                    BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor", cursor.toUrlSafe());
        } catch (IOException e) {
            logError("Fail to save cursor position " + e.getMessage());
        }
    }

    /**
     * Reads the cursor position from the saved file.
     *
     * @return cursor if the file can be properly decoded.
     */
    private Optional<Cursor> readPositionOfCursorFromFile() {
        try {
            String cursorPosition = FileHelper.readFile(BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor");
            return Optional.of(Cursor.fromUrlSafe(cursorPosition));
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Deletes the cursor position file.
     */
    private void deleteCursorPositionFile() {
        FileHelper.deleteFile(BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor");
    }

    /**
     * Logs a comment.
     */
    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));

        Path logPath = Paths.get(BASE_LOG_URI + this.getClass().getSimpleName() + ".log");
        try (OutputStream logFile = Files.newOutputStream(logPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            logFile.write((logLine + System.lineSeparator()).getBytes(Const.ENCODING));
        } catch (Exception e) {
            System.err.println("Error writing log line: " + logLine);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Logs an error and persists it to the disk.
     */
    protected void logError(String logLine) {
        System.err.println(logLine);

        log("[ERROR]" + logLine);
    }

}

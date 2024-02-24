package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.Session;
import org.hibernate.Transaction;

import teammates.common.util.HibernateUtil;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Notification;
import teammates.test.FileHelper;
import teammates.storage.sqlentity.ReadNotification;

public class DataMigrationForAccountAndReadNotificationSql extends DatastoreClient {
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

    // buffer of entities to save
    private List<teammates.storage.sqlentity.Account> entitiesAccountSavingBuffer;
    private List<ReadNotification> entitiesReadNotificationSavingBuffer;

    public static void main(String[] args) {
        new DataMigrationForAccountAndReadNotificationSql().doOperationRemotely();
        // SeedDb seedDb = new SeedDb();
        // try {
        // seedDb.seedSetup();

        // DataMigrationForAccountAndReadNotificationSql script = new
        // DataMigrationForAccountAndReadNotificationSql();
        // script.doOperation();

        // seedDb.seedTearDown();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    private DataMigrationForAccountAndReadNotificationSql() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesAccountSavingBuffer = new ArrayList<>();
        entitiesReadNotificationSavingBuffer = new ArrayList<>();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    private boolean isPreview() {
        return false;
    }

    protected boolean isMigrationNeeded(teammates.storage.entity.Account entity) {
        // TODO: detect if account / read notif are migrated or not
        return true;
    }

    protected Query<teammates.storage.entity.Account> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Account.class);
    }

    private void doMigration(teammates.storage.entity.Account entity) {
        try {
            if (!isMigrationNeeded(entity)) {
                return;
            }
            numberOfAffectedEntities.incrementAndGet();
            if (!isPreview()) {
                migrateEntity(entity);
                numberOfUpdatedEntities.incrementAndGet();
            }
        } catch (Exception e) {
            logError("Problem migrating account " + entity);
            logError(e.getMessage());
        }
    }

    protected void migrateEntity(teammates.storage.entity.Account oldAccount) {
        teammates.storage.sqlentity.Account newAccount = new teammates.storage.sqlentity.Account(
                oldAccount.getGoogleId(),
                oldAccount.getName(),
                oldAccount.getEmail());

        entitiesAccountSavingBuffer.add(newAccount);

        if (!oldAccount.getReadNotifications().isEmpty()) {
            for (Map.Entry<String, Instant> entry : oldAccount.getReadNotifications().entrySet()) {
                HibernateUtil.beginTransaction();
                UUID notificationId = UUID.fromString(entry.getKey());
                Notification newNotification = HibernateUtil.get(Notification.class, notificationId);
                HibernateUtil.commitTransaction();
                ReadNotification newReadNotification = new ReadNotification(newAccount, newNotification);
                entitiesReadNotificationSavingBuffer.add(newReadNotification);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
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
            Query<teammates.storage.entity.Account> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<teammates.storage.entity.Account> iterator;

            iterator = filterQueryKeys.iterator();

            while (iterator.hasNext()) {
                shouldContinue = true;

                doMigration(iterator.next());

                numberOfScannedKey.incrementAndGet();
            }

            if (shouldContinue) {
                cursor = iterator.getCursorAfter();
                flushEntitiesSavingBuffer();
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
     * Flushes the saving buffer by issuing Cloud SQL save request.
     */
    private void flushEntitiesSavingBuffer() {
        if (!entitiesAccountSavingBuffer.isEmpty() && !isPreview()) {
            log("Saving account in batch..." + entitiesAccountSavingBuffer.size());

            HibernateUtil.beginTransaction();
            for (teammates.storage.sqlentity.Account account : entitiesAccountSavingBuffer) {
                HibernateUtil.persist(account);
            }
            if (!entitiesReadNotificationSavingBuffer.isEmpty()) {
                log("Saving notification in batch..." + entitiesReadNotificationSavingBuffer.size());
                for (teammates.storage.sqlentity.ReadNotification rf : entitiesReadNotificationSavingBuffer) {
                    HibernateUtil.persist(rf);
                }
            }

            HibernateUtil.flushSession();
            HibernateUtil.clearSession();
            HibernateUtil.commitTransaction();
        }
        entitiesAccountSavingBuffer.clear();
        entitiesReadNotificationSavingBuffer.clear();
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

    protected void log(String logLine) {
        System.out.println(logLine);

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
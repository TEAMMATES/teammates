package teammates.client.scripts;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.BaseEntity;
import teammates.test.FileHelper;

/**
 * Base script to be used as a template for all data migration scripts.
 *
 * <ul>
 * <li>Supports full scan of entities without {@code OutOfMemoryError}.</li>
 * <li>Supports automatic continuation from the last failure point (Checkpoint feature).</li>
 * <li>Supports transaction between {@link #isMigrationNeeded(BaseEntity)} and {@link #migrateEntity(BaseEntity)}.</li>
 * <li>Supports batch saving if transaction is not used.</li>
 * </ul>
 *
 * @param <T> The entity type to be migrated by the script.
 */
public abstract class DataMigrationEntitiesBaseScript<T extends BaseEntity> extends RemoteApiClient {

    // the folder where the cursor position and console output is saved as a file
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    // 100 is the optimal batch size as there won't be too much time interval
    // between read and save (if transaction is not used)
    // cannot set number greater than 300
    // see https://stackoverflow.com/questions/41499505/objectify-queries-setting-limit-above-300-does-not-work
    private static final int BATCH_SIZE = 100;

    /*
     * Creates the folder that will contain the stored log.
     */
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    protected AtomicLong numberOfScannedKey;
    protected AtomicLong numberOfAffectedEntities;
    protected AtomicLong numberOfUpdatedEntities;

    // buffer of entities to save
    private List<T> entitiesSavingBuffer;

    public DataMigrationEntitiesBaseScript() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesSavingBuffer = new ArrayList<>();
    }

    /**
     * Gets the query for the entities that need data migration.
     */
    protected abstract Query<T> getFilterQuery();

    /**
     * If true, the script will not perform actual data migration.
     */
    protected abstract boolean isPreview();

    /**
     * Checks whether data migration is needed.
     *
     * <p>Causation: this method might be called in multiple threads if using transaction.</p>
     */
    protected abstract boolean isMigrationNeeded(T entity) throws Exception;

    /**
     * Migrates the entity.
     *
     * <p>Causation: this method might be called in multiple threads if using transaction.</p>
     */
    protected abstract void migrateEntity(T entity) throws Exception;

    /**
     * Determines whether the migration should be done in a transaction.
     *
     * <p>Transaction is useful for data consistency. However, there are some limitations on operations
     * inside a transaction. In addition, the performance of the script will also be affected.
     *
     * @see <a href="https://cloud.google.com/appengine/docs/standard/java/datastore/transactions#what_can_be_done_in_a_transaction">What can be done in a transaction</a>
     * @see <a href="https://cloud.google.com/appengine/docs/standard/java/tools/remoteapi#transaction-efficiency">Transactions are less efficient</a>
     */
    protected boolean shouldUseTransaction() {
        return false;
    }

    /**
     * Migrates the entity without transaction for better performance.
     */
    private void migrateWithoutTrx(T entity) {
        doMigration(entity);
    }

    /**
     * Migrates the entity and counts the statistics.
     */
    private void doMigration(T entity) {
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
            logError("Problem migrating entity " + entity);
            logError(e.getMessage());
        }
    }

    /**
     * Migrates the entity in a transaction to ensure data consistency.
     */
    private void migrateWithTrx(Key<T> entityKey) {
        Runnable task = () -> {
            // the read place a "lock" on the object to migrate
            T entity = ofy().load().key(entityKey).now();
            doMigration(entity);
        };
        if (isPreview()) {
            // even transaction is enabled, there is no need to use transaction in preview mode
            task.run();
        } else {
            ofy().transact(task);
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
            log("Start from cursor position: " + cursor.toWebSafeString());
        }

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<T> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResultIterator<?> iterator;
            if (shouldUseTransaction()) {
                iterator = filterQueryKeys.keys().iterator();
            } else {
                iterator = filterQueryKeys.iterator();
            }

            while (iterator.hasNext()) {
                shouldContinue = true;

                // migrate
                if (shouldUseTransaction()) {
                    migrateWithTrx((Key<T>) iterator.next());
                } else {
                    migrateWithoutTrx((T) iterator.next());
                }

                numberOfScannedKey.incrementAndGet();
            }

            if (shouldContinue) {
                cursor = iterator.getCursor();
                flushEntitiesSavingBuffer();
                savePositionOfCursorToFile(cursor);
                log(String.format("Cursor Position: %s", cursor.toWebSafeString()));
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
     * Stores the entity to save in a buffer and saves it later.
     */
    protected void saveEntityDeferred(T entity) {
        if (shouldUseTransaction()) {
            throw new RuntimeException("Batch saving is not supported for transaction!");
        }
        entitiesSavingBuffer.add(entity);
    }

    /**
     * Flushes the saving buffer by issuing Datastore save request.
     */
    private void flushEntitiesSavingBuffer() {
        if (!entitiesSavingBuffer.isEmpty() && !isPreview()) {
            log("Saving entities in batch..." + entitiesSavingBuffer.size());
            ofy().save().entities(entitiesSavingBuffer).now();
        }
        entitiesSavingBuffer.clear();
    }

    /**
     * Saves the cursor position to a file so it can be used in the next run.
     */
    private void savePositionOfCursorToFile(Cursor cursor) {
        try {
            FileHelper.saveFile(
                    BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor", cursor.toWebSafeString());
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
            String cursorPosition =
                    FileHelper.readFile(BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor");
            return Optional.of(Cursor.fromWebSafeString(cursorPosition));
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
     * Logs a line and persists it to the disk.
     */
    protected void log(String logLine) {
        System.out.println(logLine);

        Path logPath = Paths.get(BASE_LOG_URI + this.getClass().getSimpleName() + ".log");
        try (OutputStream logFile = Files.newOutputStream(logPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            logFile.write((logLine + System.lineSeparator()).getBytes());
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

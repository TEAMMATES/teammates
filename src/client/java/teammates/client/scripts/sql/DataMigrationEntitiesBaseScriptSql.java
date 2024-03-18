package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.test.FileHelper;

/**
 * Base script to be used as a template for all data migration scripts.
 *
 * <ul>
 * <li>Supports full scan of entities without {@code OutOfMemoryError}.</li>
 * <li>Supports automatic continuation from the last failure point (Checkpoint
 * feature).</li>
 * <li>Supports transaction between {@link #isMigrationNeeded(BaseEntity)} and
 * {@link #migrateEntity(BaseEntity)}.</li>
 * <li>Supports batch saving if transaction is not used.</li>
 * </ul>
 *
 * @param <E> The datastore entity type to be migrated by the script.
 * @param <T> The SQL entity type to be migrated by the script.
 */
public abstract class DataMigrationEntitiesBaseScriptSql<
        E extends teammates.storage.entity.BaseEntity, T extends teammates.storage.sqlentity.BaseEntity>
        extends DatastoreClient {

    /* NOTE
     * Before running the migration, please enable hibernate.jdbc.batch_size, hibernate.order_updates,
     * hibernate.batch_versioned_data, hibernate.jdbc.fetch_size in HibernateUtil.java
     * for optimized batch-insertion, batch-update and batch-fetching. Also, verify that your schema
     * meets the conditions for them.
    */

    // the folder where the cursor position and console output is saved as a file
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    private static final int BATCH_SIZE = 1000;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfScannedKey;
    AtomicLong numberOfUpdatedEntities;

    // buffer of entities to save
    private List<T> entitiesSavingBuffer;

    public DataMigrationEntitiesBaseScriptSql() {
        numberOfAffectedEntities = new AtomicLong();
        numberOfScannedKey = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesSavingBuffer = new ArrayList<>();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    /**
     * Gets the query for the entities that need data migration.
     */
    protected abstract Query<E> getFilterQuery();

    /**
     * If true, the script will not perform actual data migration.
     */
    protected abstract boolean isPreview();

    /**
     * Sets the criterias used in {@link #isMigrationNeeded(E entity)} on whether migration is needed.
     * Ran during initialization.
     */
    protected abstract void setMigrationCriteria();

    /**
     * Checks whether data migration is needed.
     *
     * <p>Causation: this method might be called in multiple threads if using
     * transaction.
     * </p>
     */
    protected abstract boolean isMigrationNeeded(E entity);

    /**
     * Migrates the entity.
     *
     * <p>Causation: this method might be called in multiple threads if using
     * transaction.
     * </p>
     */
    protected abstract void migrateEntity(E oldEntity) throws Exception;

    /**
     * Determines whether the migration should be done in a transaction.
     *
     * <p>Transaction is useful for data consistency. However, there are some
     * limitations on operations
     * inside a transaction. In addition, the performance of the script will also be
     * affected.
     *
     * @see <a href=
     *      "https://cloud.google.com/datastore/docs/concepts/cloud-datastore-transactions#what_can_be_done_in_a_transaction">What
     *      can be done in a transaction</a>
     */
    protected boolean shouldUseTransaction() {
        return false;
    }

    /**
     * Returns the prefix for the log line.
     */
    protected String getLogPrefix() {
        return String.format("%s Migrating:", this.getClass().getSimpleName());
    }

    /**
     * Migrates the entity without transaction for better performance.
     */
    private void migrateWithoutTrx(E entity) {
        doMigration(entity);
    }

    /**
     * Migrates the entity and counts the statistics.
     */
    private void doMigration(E entity) {
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
    private void migrateWithTrx(Key<E> entityKey) {
        Runnable task = () -> {
            // the read place a "lock" on the object to migrate
            E entity = ofy().load().key(entityKey).now();
            doMigration(entity);
        };
        if (isPreview()) {
            // even transaction is enabled, there is no need to use transaction in preview
            // mode
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
        setMigrationCriteria();

        Cursor cursor = readPositionOfCursorFromFile().orElse(null);
        if (cursor == null) {
            log("Start from the beginning");
        } else {
            log("Start from cursor position: " + cursor.toUrlSafe());
        }

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<E> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<?> iterator;
            if (shouldUseTransaction()) {
                iterator = filterQueryKeys.keys().iterator();
            } else {
                iterator = filterQueryKeys.iterator();
            }

            while (iterator.hasNext()) {
                shouldContinue = true;

                // migrate
                if (shouldUseTransaction()) {
                    migrateWithTrx((Key<E>) iterator.next());
                } else {
                    migrateWithoutTrx((E) iterator.next());
                }

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
     * Stores the entity to save in a buffer and saves it later.
     */
    protected void saveEntityDeferred(T entity) {
        if (shouldUseTransaction()) {
            throw new RuntimeException("Batch saving is not supported for transaction!");
        }
        entitiesSavingBuffer.add(entity);
    }

    /**
     * Flushes the saving buffer by issuing Cloud SQL save request.
     */
    private void flushEntitiesSavingBuffer() {
        if (!entitiesSavingBuffer.isEmpty() && !isPreview()) {
            log("Saving entities in batch..." + entitiesSavingBuffer.size());

            long startTime = System.currentTimeMillis();
            HibernateUtil.beginTransaction();
            for (T entity : entitiesSavingBuffer) {
                HibernateUtil.persist(entity);
            }

            HibernateUtil.flushSession();
            HibernateUtil.clearSession();
            HibernateUtil.commitTransaction();
            long endTime = System.currentTimeMillis();
            log("Flushing " + entitiesSavingBuffer.size() + " took " + (endTime - startTime) + " milliseconds");
        }
        entitiesSavingBuffer.clear();
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
     * Logs a line and persists it to the disk.
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

    /**
     * Returns true if {@code text} contains at least one of the {@code strings} or
     * if {@code strings} is empty.
     * If {@code text} is null, false is returned.
     */
    private static boolean isTextContainingAny(String text, List<String> strings) {
        if (text == null) {
            return false;
        }

        if (strings.isEmpty()) {
            return true;
        }

        return strings.stream().anyMatch(s -> text.contains(s));
    }

    /**
     * Returns true if the {@code string} has evidence of having been sanitized.
     * A string is considered sanitized if it does not contain any of the chars '<',
     * '>', '/', '\"', '\'',
     * and contains at least one of their sanitized equivalents or the sanitized
     * equivalent of '&'.
     *
     * <p>
     * Eg. "No special characters", "{@code
     *
    <p>
     * &quot;with quotes&quot;
     *
    </p>
     * }" are considered to be not sanitized.<br>
     * "{@code &lt;p&gt; a p tag &lt;&#x2f;p&gt;}" is considered to be sanitized.
     * </p>
     */
    static boolean isSanitizedHtml(String string) {
        return string != null
                && !isTextContainingAny(string, Arrays.asList("<", ">", "\"", "/", "\'"))
                && isTextContainingAny(string, Arrays.asList("&lt;", "&gt;", "&quot;", "&#x2f;", "&#39;", "&amp;"));
    }

    /**
     * Returns the desanitized {@code string} if it is sanitized, otherwise returns
     * the unchanged string.
     */
    static String desanitizeIfHtmlSanitized(String string) {
        return isSanitizedHtml(string) ? desanitizeFromHtml(string) : string;
    }

    /**
     * Recovers a html-sanitized string using {@link #sanitizeForHtml}
     * to original encoding for appropriate display.<br>
     * It restores encoding for < > \ / ' & <br>
     * The method should only be used once on sanitized html
     *
     * @return recovered string
     */
    private static String desanitizeFromHtml(String sanitizedString) {
        if (sanitizedString == null) {
            return null;
        }

        return sanitizedString.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#x2f;", "/")
                .replace("&#39;", "'")
                .replace("&amp;", "&");
    }

}

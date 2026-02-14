package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.test.FileHelper;

// CHECKSTYLE.ON:ImportOrder
/**
 * Patch Migration Script for Usage Statistics
 * Batch select datastore usage statistics, and then batch select SQL entities
 * with the same start timestamps.
 * Compare the size of the two list, if is not equal, find the missing one in
 * SQL and migrate it.
 */
@SuppressWarnings("PMD")
public class PatchDataMigrationForUsageStatisticsSql extends DatastoreClient {
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

    private List<teammates.storage.sqlentity.UsageStatistics> entitiesSavingBuffer;

    private PatchDataMigrationForUsageStatisticsSql() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesSavingBuffer = new ArrayList<>();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new PatchDataMigrationForUsageStatisticsSql().doOperationRemotely();
    }

    /**
     * Returns the log prefix.
     */
    protected String getLogPrefix() {
        return String.format("Usage Statistics Patch Migration:");
    }

    private boolean isPreview() {
        return false;
    }

    /**
     * Returns whether the account has been migrated.
     */
    protected boolean isMigrationNeeded(teammates.storage.entity.UsageStatistics entity) {
        return true;
    }

    /**
     * Returns the filter query.
     */
    protected Query<teammates.storage.entity.UsageStatistics> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.UsageStatistics.class);
    }

    private void doMigration(teammates.storage.entity.UsageStatistics entity) {
        try {
            if (!isMigrationNeeded(entity)) {
                return;
            }
            if (!isPreview()) {
                migrateEntity(entity);
            }
        } catch (Exception e) {
            logError("Problem migrating usage stats " + entity);
            logError(e.getMessage());
        }
    }

    /**
     * Migrates the entity. In this case, add entity to buffer.
     */
    protected void migrateEntity(teammates.storage.entity.UsageStatistics oldEntity) {
        UsageStatistics newEntity = new UsageStatistics(
                oldEntity.getStartTime(),
                oldEntity.getTimePeriod(),
                oldEntity.getNumResponses(),
                oldEntity.getNumCourses(),
                oldEntity.getNumStudents(),
                oldEntity.getNumInstructors(),
                oldEntity.getNumAccountRequests(),
                oldEntity.getNumEmails(),
                oldEntity.getNumSubmissions());

        entitiesSavingBuffer.add(newEntity);
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
            Query<teammates.storage.entity.UsageStatistics> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<teammates.storage.entity.UsageStatistics> iterator;

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
        if (!entitiesSavingBuffer.isEmpty() && !isPreview()) {
            log("Checking usage stats in batch..." + entitiesSavingBuffer.size());

            // Entity identity is (startTime, timePeriod). Find which buffer entities are missing in SQL.
            HibernateUtil.beginTransaction();
            CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
            CriteriaQuery<teammates.storage.sqlentity.UsageStatistics> cr = cb.createQuery(
                    teammates.storage.sqlentity.UsageStatistics.class);
            Root<teammates.storage.sqlentity.UsageStatistics> root = cr
                    .from(teammates.storage.sqlentity.UsageStatistics.class);

            List<Instant> instantList = entitiesSavingBuffer.stream()
                    .map(teammates.storage.sqlentity.UsageStatistics::getStartTime)
                    .distinct()
                    .collect(Collectors.toList());
            cr.select(root).where(root.get("startTime").in(instantList));
            TypedQuery<teammates.storage.sqlentity.UsageStatistics> query = HibernateUtil.createQuery(cr);
            List<teammates.storage.sqlentity.UsageStatistics> sqlEntitiesFound = query.getResultList();

            Set<String> sqlKeys = sqlEntitiesFound.stream()
                    .map(entity -> entity.getStartTime().toEpochMilli() + "|"
                            + entity.getTimePeriod())
                    .collect(Collectors.toSet());

            for (teammates.storage.sqlentity.UsageStatistics entity : entitiesSavingBuffer) {
                String key = entity.getStartTime().toEpochMilli() + "|" + entity.getTimePeriod();
                if (sqlKeys.contains(key)) {
                    continue;
                }
                // entity is not found in SQL
                log("Migrating missing usage stats: startTime=" + entity.getStartTime()
                        + ", timePeriod=" + entity.getTimePeriod());
                numberOfAffectedEntities.incrementAndGet();
                numberOfUpdatedEntities.incrementAndGet();
                HibernateUtil.persist(entity);
            }

            HibernateUtil.flushSession();
            HibernateUtil.clearSession();
            HibernateUtil.commitTransaction();

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

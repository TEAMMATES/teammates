package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
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
public class PatchCreatedAtAccountRequest extends DatastoreClient {
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

    private PatchCreatedAtAccountRequest() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new PatchCreatedAtAccountRequest().doOperationRemotely();
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
    protected boolean isMigrationNeeded(teammates.storage.entity.AccountRequest entity) {
        return true;
    }

    /**
     * Returns the filter query.
     */
    protected Query<teammates.storage.entity.AccountRequest> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.AccountRequest.class);
    }

    private void doMigration(teammates.storage.entity.AccountRequest entity) {
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
    protected void migrateEntity(teammates.storage.entity.AccountRequest oldEntity) {
        HibernateUtil.beginTransaction();

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.AccountRequest> cr = cb.createQuery(teammates.storage.sqlentity.AccountRequest.class);
        Root<teammates.storage.sqlentity.AccountRequest> root = cr.from(teammates.storage.sqlentity.AccountRequest.class);

        cr.select(root).where(cb.equal(root.get("institute"), oldEntity.getInstitute()))
            .where(cb.equal(root.get("email"), oldEntity.getEmail()))
            .where(cb.equal(root.get("name"), oldEntity.getName()));

        List<teammates.storage.sqlentity.AccountRequest> matchingAccounts = HibernateUtil.createQuery(cr).getResultList();
        
        if (matchingAccounts.size() > 1) {
            throw new Error("More than one matching account request found");
        } else if (matchingAccounts.size() == 0){
            throw new Error("No matching account found");
        }

        // Get first items since there is guaranteed to be one
        teammates.storage.sqlentity.AccountRequest newAccountReq = matchingAccounts.get(0);
        newAccountReq.setCreatedAt(oldEntity.getCreatedAt());
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
            Query<teammates.storage.entity.AccountRequest> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<teammates.storage.entity.AccountRequest> iterator;

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

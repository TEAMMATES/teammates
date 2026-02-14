package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.FileHelper;

/**
 * Patch createdAt attribute for Account Request.
 * Assumes that the notification was previously migrated using DataMigrationForAccountRequestSql.java
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
    private static final int BATCH_SIZE = 500;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfScannedKey;
    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfUpdatedEntities;

    private List<AccountRequest> entitiesSavingBuffer;

    private PatchCreatedAtAccountRequest() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();
        entitiesSavingBuffer = new LinkedList<>();

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
        return String.format("Account Request Patch Migration:");
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
        if (!isMigrationNeeded(entity)) {
            return;
        }
        if (!isPreview()) {
            migrateEntity(entity);
        }
    }

    /**
     * Migrates the entity. In this case, add entity to buffer.
     */
    protected void migrateEntity(teammates.storage.entity.AccountRequest oldEntity) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.AccountRequest> cr =
                cb.createQuery(teammates.storage.sqlentity.AccountRequest.class);
        Root<teammates.storage.sqlentity.AccountRequest> root = cr.from(teammates.storage.sqlentity.AccountRequest.class);
        Predicate instituteMatch = cb.equal(root.get("institute"), oldEntity.getInstitute());
        Predicate emailMatch = cb.equal(root.get("email"), oldEntity.getEmail());
        Predicate nameMatch = cb.equal(root.get("name"), oldEntity.getName());
        Predicate keyMatch = oldEntity.getRegistrationKey() == null
                ? cb.isNull(root.get("registrationKey"))
                : cb.equal(root.get("registrationKey"), oldEntity.getRegistrationKey());
        cr.select(root).where(cb.and(instituteMatch, emailMatch, nameMatch, keyMatch));

        List<teammates.storage.sqlentity.AccountRequest> matchingAccounts = HibernateUtil.createQuery(cr).getResultList();

        if (matchingAccounts.isEmpty()) {
            throw new Error("No matching account request found");
        }

        if (matchingAccounts.size() > 1) {
            throw new Error("More than one matching account request found");
        }

        // Get first items since there is guaranteed to be one
        teammates.storage.sqlentity.AccountRequest newAccountReq = matchingAccounts.get(0);
        newAccountReq.setCreatedAt(oldEntity.getCreatedAt());

        saveEntityDeferred(newAccountReq);
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
            HibernateUtil.beginTransaction();
            shouldContinue = false;
            Query<teammates.storage.entity.AccountRequest> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResults<teammates.storage.entity.AccountRequest> iterator;

            iterator = filterQueryKeys.iterator();

            while (iterator.hasNext()) {
                shouldContinue = true;
                teammates.storage.entity.AccountRequest accReq = iterator.next();
                try {
                    doMigration(accReq);
                } catch (Exception e) {
                    logError("Problem patching account request " + accReq.getId());
                    logError(e.getMessage());
                    e.printStackTrace();
                    return;
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

    /**
     * Stores the entity to save in a buffer and saves it later.
     */
    protected void saveEntityDeferred(AccountRequest entity) {
        entitiesSavingBuffer.add(entity);
    }

    /**
     * Flushes the saving buffer by issuing Cloud SQL save request.
     */
    private void flushEntitiesSavingBuffer() {
        if (!entitiesSavingBuffer.isEmpty() && !isPreview()) {
            log("Saving entities in batch..." + entitiesSavingBuffer.size());

            long startTime = System.currentTimeMillis();
            for (AccountRequest entity : entitiesSavingBuffer) {
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

}

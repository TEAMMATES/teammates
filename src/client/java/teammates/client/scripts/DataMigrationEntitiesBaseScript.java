package teammates.client.scripts;

import java.util.concurrent.atomic.AtomicLong;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.util.StringHelper;
import teammates.storage.entity.BaseEntity;

/**
 * Base script to be used as a template for all data migration scripts.
 *
 * <ul>
 * <li>Supports full scan of entities without {@code OutOfMemoryError}.</li>
 * <li>Supports continuation from the last failure point (Checkpoint feature).</li>
 * <li>Supports transaction between {@link #isMigrationNeeded(Key)} and {@link #migrateEntity(Key)}.</li>
 * </ul>
 *
 * @param <T> The entity type to be migrated by the script.
 */
public abstract class DataMigrationEntitiesBaseScript<T extends BaseEntity> extends RemoteApiClient {

    protected AtomicLong numberOfScannedKey;
    protected AtomicLong numberOfAffectedEntities;
    protected AtomicLong numberOfUpdatedEntities;

    public DataMigrationEntitiesBaseScript() {
        numberOfScannedKey = new AtomicLong();
        numberOfAffectedEntities = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();
    }

    /**
     * Gets the query for the entities that need data migration.
     */
    protected abstract Query<T> getFilterQuery();

    /**
     * If true, the script will not perform actual data migration.
     *
     * <p>Causation: this method might be called in multiple threads.</p>
     */
    protected abstract boolean isPreview();

    /**
     * Gets the last position of cursor where the migration script stopped.
     *
     * <p>Use empty string/null if no last position;
     * Position can be obtained from the console output of the last script run.</p>
     */
    protected abstract String getLastPositionOfCursor();

    /**
     * Determines how often the cursor position information should be printed.
     *
     * <p>Should choose a reasonable value based on the number of entities in the server.</p>
     */
    protected abstract int getCursorInformationPrintCycle();

    /**
     * Checks whether data migration is needed.
     *
     * <p>Causation: this method might be called in multiple threads.</p>
     */
    protected abstract boolean isMigrationNeeded(Key<T> entity) throws Exception;

    /**
     * Migrates the entity.
     *
     * <p>Causation: this method might be called in multiple threads.</p>
     */
    protected abstract void migrateEntity(Key<T> entity) throws Exception;

    /**
     * Determines whether the migration should be done in a transaction.
     *
     * <p>Transaction is useful for data consistency. However, there are some limitations on operations
     * inside a transaction.
     *
     * @see <a href="https://cloud.google.com/appengine/docs/standard/java/datastore/transactions#what_can_be_done_in_a_transaction">What can be done in a transaction</a>
     */
    protected boolean shouldUseTransaction() {
        return true;
    }

    /**
     * Does data migration ({@link #isMigrationNeeded(Key)} and {@link #migrateEntity(Key)}) in transaction
     * to ensure data consistency.
     */
    private void migrate(Key<T> entityKey) {
        Runnable task = () -> {
            try {
                if (!isMigrationNeeded(entityKey)) {
                    return;
                }
                numberOfAffectedEntities.incrementAndGet();
                if (!isPreview()) {
                    migrateEntity(entityKey);
                    numberOfUpdatedEntities.incrementAndGet();
                }
            } catch (Exception e) {
                System.err.println("Problem migrating entity with key: " + entityKey);
                System.err.println(e.getMessage());
            }
        };
        if (shouldUseTransaction()) {
            ofy().transact(task);
        } else {
            task.run();
        }
    }

    @Override
    protected void doOperation() {
        println("Running " + getClass().getSimpleName() + "...");
        println("Preview: " + isPreview());

        Cursor cursor = null;
        String cursorPosition = getLastPositionOfCursor();
        if (StringHelper.isEmpty(cursorPosition)) {
            println("Start from the beginning, you may want to record the cursor position\n"
                    + "in order to start from the last stopped position when re-run the script.");
        } else {
            println("Start from cursor position: " + cursorPosition);
            cursor = Cursor.fromWebSafeString(cursorPosition);
        }

        String entityType = "Unknown";
        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<T> filterQueryKeys = getFilterQuery().limit(getCursorInformationPrintCycle());
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }
            QueryResultIterator<Key<T>> iterator = filterQueryKeys.keys().iterator();

            while (iterator.hasNext()) {
                shouldContinue = true;
                Key<T> keyOfEntityToMigrate = iterator.next();

                // get entity type
                if ("Unknown".equals(entityType)) {
                    entityType = keyOfEntityToMigrate.getKind();
                }

                // migrate
                migrate(keyOfEntityToMigrate);
                numberOfScannedKey.incrementAndGet();
            }

            if (shouldContinue) {
                cursor = iterator.getCursor();
                println(String.format("Cursor Position: %s", cursor.toWebSafeString()));
                println(String.format("Number Of Entity Key Scanned: %d", numberOfScannedKey.get()));
                println(String.format("Number Of Entity affected: %d", numberOfAffectedEntities.get()));
                println(String.format("Number Of Entity updated: %d", numberOfUpdatedEntities.get()));
            }
        }

        println(isPreview() ? "Preview Completed!" : "Migration Completed!");
        println("Total number of " + entityType + "s: " + numberOfScannedKey.get());
        println("Number of affected " + entityType + "s: " + numberOfAffectedEntities.get());
        println("Number of updated " + entityType + "s: " + numberOfUpdatedEntities.get());
    }

}

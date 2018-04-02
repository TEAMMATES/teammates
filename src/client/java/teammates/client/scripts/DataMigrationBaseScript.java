package teammates.client.scripts;

import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;

/**
 * Base script to be used as template for all data migration scripts.
 *
 * @param <T> The entity type to be migrated by the script.
 */
public abstract class DataMigrationBaseScript<T> extends RemoteApiClient {

    /**
     * If true, the script will not perform actual data migration.
     */
    protected abstract boolean isPreview();

    /**
     * Gets the entity type for logging purposes.
     */
    protected String getEntityType(T entity) {
        return entity.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Gets entity info for logging purposes.
     */
    protected String getEntityInfo(T entity) {
        return entity.toString();
    }

    /**
     * Gets all the entities that are to be filtered for migration.
     */
    protected abstract List<T> getEntities();

    /**
     * Checks whether data migration is needed.
     */
    protected abstract boolean isMigrationNeeded(T entity);

    /**
     * Prints information necessary for previewing the migration process.
     */
    protected abstract void printPreviewInformation(T entity);

    /**
     * Migrates the entity.
     */
    protected abstract void migrate(T entity) throws Exception;

    /**
     * Performs any post-migration action.
     *
     * <p>This can also be used for batch migration, i.e. in that case {@link #migrate(T)}
     * will only be used to queue up the entities that need to be migrated.
     */
    protected abstract void postAction();

    @Override
    protected void doOperation() {
        List<T> entities = getEntities();
        if (entities.isEmpty()) {
            println("There is nothing to migrate.");
            return;
        }

        int numberOfAffectedEntities = 0;
        int numberOfUpdatedEntities = 0;
        String entityType = getEntityType(entities.get(0));

        LoopHelper loopHelper = new LoopHelper(100, entityType + "s processed.");
        println("Running " + getClass().getName() + "...");
        println("Preview: " + isPreview());
        for (T entity : entities) {
            loopHelper.recordLoop();
            boolean isMigrationNeeded = isMigrationNeeded(entity);
            if (!isMigrationNeeded) {
                continue;
            }
            numberOfAffectedEntities++;
            if (isPreview()) {
                printPreviewInformation(entity);
                continue;
            }
            try {
                migrate(entity);
                numberOfUpdatedEntities++;
            } catch (Exception e) {
                println("Problem migrating " + getEntityInfo(entity));
                println(e.getMessage());
            }
        }

        postAction();

        println("Total number of " + entityType + "s: " + loopHelper.getCount());
        println("Number of affected " + entityType + "s: " + numberOfAffectedEntities);
        println("Number of updated " + entityType + "s: " + numberOfUpdatedEntities);
    }

}

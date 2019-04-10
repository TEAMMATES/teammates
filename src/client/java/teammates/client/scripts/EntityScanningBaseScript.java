package teammates.client.scripts;

import teammates.storage.entity.BaseEntity;

/**
 * Base script to be used as a template to scan entities in the Datastore.
 *
 * @param <T> The entity to scan.
 */
public abstract class EntityScanningBaseScript<T extends BaseEntity> extends DataMigrationEntitiesBaseScript<T> {

    /**
     * Checks whether an entity matches a scanning criteria.
     *
     * <p>For example, a scanning criteria could be whether a field is null in the entity.</p>
     */
    protected abstract boolean isEntityMatchScanningCriteria(T entity);

    @Override
    protected boolean isMigrationNeeded(T entity) throws Exception {
        return isEntityMatchScanningCriteria(entity);
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void migrateEntity(T entity) throws Exception {
        // scanning won't do write operation
    }

    @Override
    protected boolean isPreview() {
        // scanning won't do write operation
        return true;
    }

}

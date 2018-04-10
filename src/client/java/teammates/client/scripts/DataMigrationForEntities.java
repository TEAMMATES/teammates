package teammates.client.scripts;

import teammates.common.datatransfer.attributes.EntityAttributes;

/**
 * Base script for all data migration scripts involving entities wrapped by {@link EntityAttributes}.
 *
 * @param <T> The entity attributes type involved in migration by the script.
 */
public abstract class DataMigrationForEntities<T extends EntityAttributes<?>> extends DataMigrationBaseScript<T> {

    @Override
    protected String getEntityType(T entity) {
        return entity.getEntityTypeAsString().toLowerCase();
    }

    @Override
    protected String getEntityInfo(T entity) {
        return getEntityType(entity) + " with ID " + entity.getIdentificationString();
    }

}

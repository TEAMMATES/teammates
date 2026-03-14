
package teammates.storage.sqlapi;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 */
class EntitiesDb {
    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * If there is no persistent instance currently associated with the session, it will be loaded.
     */
    protected <T extends BaseEntity> T merge(T entity) {
        assert entity != null;

        T newEntity = HibernateUtil.merge(entity);
        return newEntity;
    }

    /**
     * Associate {@code entity} with the persistence context.
     */
    protected void persist(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.persist(entity);
    }

    /**
     * Deletes {@code entity} from persistence context.
     */
    protected void delete(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.remove(entity);
    }
}

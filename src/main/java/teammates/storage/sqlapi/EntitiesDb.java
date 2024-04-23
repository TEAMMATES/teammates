
package teammates.storage.sqlapi;

import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 */
class EntitiesDb {

    static final Logger log = Logger.getLogger();

    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * If there is no persistent instance currently associated with the session, it will be loaded.
     */
    protected <T extends BaseEntity> T merge(T entity) {
        assert entity != null;

        T newEntity = HibernateUtil.merge(entity);
        log.info("Entity saved: " + newEntity.toString());
        return newEntity;
    }

    /**
     * Associate {@code entity} with the persistence context.
     */
    protected void persist(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.persist(entity);
        log.info("Entity persisted: " + entity.toString());
    }

    /**
     * Deletes {@code entity} from persistence context.
     */
    protected void delete(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.remove(entity);
        log.info("Entity deleted: " + entity.toString());
    }
}
